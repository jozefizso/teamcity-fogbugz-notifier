package teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.issueTracker.Issue;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FogBugzNotifier extends BuildServerAdapter {
    private static Logger LOG = Logger.getInstance(FogBugzNotifier.class.getName());

    private SBuildServer server;
    private BuildsManager buildsManager;

    public FogBugzNotifier(@NotNull SBuildServer server, @NotNull BuildsManager buildsManager) {
        this.server = server;
        this.buildsManager = buildsManager;
    }

    @PostConstruct
    public void register() {
        this.server.addListener(this);
    }

    @Override
    public void buildFinished(@NotNull SRunningBuild runningBuild) {
        long buildId = runningBuild.getBuildId();

        LOG.debug(String.format("Build %s finished. Preparing to send data to FogBugz.", buildId));

        SBuild finishedBuild = this.buildsManager.findBuildInstanceById(buildId);

        Status buildStatus = finishedBuild.getBuildStatus();
        String buildStatusEventType = buildStatus.isSuccessful() ? "build-success" : "build-failure";
        Date finished = finishedBuild.getFinishDate();
        if (finished == null) {
            finished = new Date();
        }
        DateTime dtFinished = new DateTime(finished);

        String personName = null;
        TriggeredBy triggeredBy = finishedBuild.getTriggeredBy();
        if (triggeredBy != null && triggeredBy.isTriggeredByUser()) {
            SUser user = triggeredBy.getUser();
            personName = user.getUsername();
        }

        String statusDescription = finishedBuild.getStatusDescriptor().getText();

        WebLinks links = new WebLinks(this.server);
        String viewResultsUrl = links.getViewResultsUrl(finishedBuild);

        for (SBuildFeatureDescriptor feature : finishedBuild.getBuildFeaturesOfType(FogBugzNotifierBuildFeature.FEATURE_TYPE)) {
            Map<String, String> featureParams = feature.getParameters();
            String url = featureParams.get("fbAddress");
            String token = featureParams.get("sToken");

            LOG.debug(String.format("Build %s is configured with fbAddress=%s, sToken=%s", buildId, url, token));

            for (Issue issue : finishedBuild.getRelatedIssues()) {
                String bugzId = issue.getId();

                FogBugzEventData data = new FogBugzEventData();
                data.setAction("event");
                data.setEventType(buildStatusEventType);
                data.setBug(bugzId);
                data.setEventUtc(dtFinished);
                data.setPersonName(personName);
                data.setMessage(statusDescription);
                data.setExternalUrl(viewResultsUrl);

                try {
                    LOG.info(String.format("Sending data to FogBugz case '%s'", bugzId));
                    postData(url, token, data);
                }
                catch (Exception ex) {
                    LOG.error("Failed to notify FogBugz Extended Events plugin.", ex);
                }
            }
        }
    }

    public String postData(String fogbugzUrl, String token, FogBugzEventData data) throws Exception {
        String pluginUrl = fogbugzUrl + "/default.asp?pg=pgPluginRaw&sPluginId=FBExtendedEvents%40goit.io";

        data.setToken(token);
        List<NameValuePair> params = data.toParams();

        String response = postData(pluginUrl, params);
        return response;
    }

    private String postData(String url, List<NameValuePair> params) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params);
        httpPost.setEntity(paramsEntity);

        String paramsString = EntityUtils.toString(paramsEntity);
        LOG.debug(String.format("Posting to FogBugz: %s POST data: %s", url, paramsString));

        CloseableHttpResponse response = client.execute(httpPost);
        String responseString = new BasicResponseHandler().handleResponse(response);

        response.close();
        client.close();

        return responseString;
    }
}
