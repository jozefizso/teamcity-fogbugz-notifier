package teamcity;

import com.google.common.base.Strings;
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.issueTracker.Issue;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.parameters.ParametersProvider;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.SUser;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        String buildName = finishedBuild.getBuildTypeName();
        String buildDescription = getBuildDescription(finishedBuild);
        String statusDescription = finishedBuild.getStatusDescriptor().getText();

        String message = String.format("%s\n<p>%s</p>", buildDescription, statusDescription);

        WebLinks links = new WebLinks(this.server);
        String viewResultsUrl = links.getViewResultsUrl(finishedBuild);

        for (SBuildFeatureDescriptor feature : finishedBuild.getBuildFeaturesOfType(FogBugzNotifierBuildFeature.FEATURE_TYPE)) {
            Map<String, String> featureParams = feature.getParameters();
            String url = featureParams.get("fbAddress");
            String token = featureParams.get("sToken");
            String moduleName = featureParams.get("sModuleName");
            String branchName = featureParams.get("sBranchName");

            LOG.debug(String.format("Build %s is configured with fbAddress=%s, sToken=%s", buildId, url, token));

            for (Issue issue : finishedBuild.getRelatedIssues()) {
                String bugzId = issue.getId();

                FogBugzEventData data = new FogBugzEventData();
                data.setAction("event");
                data.setEventType(buildStatusEventType);
                data.setBug(bugzId);
                data.setEventUtc(dtFinished);
                data.setPersonName(personName);
                data.setMessage(message);
                data.setExternalUrl(viewResultsUrl);
                data.setBuildName(buildName);
                data.setModuleName(moduleName);
                data.setBranchName(branchName);

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

    private String getBuildDescription(SBuild finishedBuild) {
        ParametersProvider parameters = finishedBuild.getParametersProvider();
        String buildNumber = parameters.get("build.number");
        String buildRevision = parameters.get("build.vcs.number");
        String buildCvsTag = parameters.get("BuildCvsTag");

        StringBuffer sb = new StringBuffer();
        sb.append("<p>");
        if (!Strings.isNullOrEmpty(buildNumber)) {
            sb.append(String.format("<strong>Build Number:</strong> #%s<br>", buildNumber));
        }
        if (!Strings.isNullOrEmpty(buildNumber)) {
            sb.append(String.format("<strong>Revision:</strong> %s<br>", buildRevision));
        }
        if (!Strings.isNullOrEmpty(buildNumber)) {
            sb.append(String.format("<strong>BuildCvsTag:</strong> #%s<br>", buildCvsTag));
        }
        sb.append("</p>");

        return sb.toString();
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
