package teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.messages.BuildMessage1;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FogBugzNotifier extends BuildServerAdapter {
    private static Logger LOG = Logger.getInstance(FogBugzNotifier.class.getName());

    private SBuildServer server;

    public FogBugzNotifier(@NotNull SBuildServer server) {
        this.server = server;
    }

    @PostConstruct
    public void register() {
        this.server.addListener(this);
    }

    @Override
    public void buildFinished(@NotNull SRunningBuild build) {
        Status buildStatus = build.getBuildStatus();
        String buildStatusText = buildStatus.getText();
        Date finished = build.getFinishDate();
        if (finished == null) {
            finished = new Date();
        }

        DateTime dtFinished = new DateTime(finished);

        String statusDescription = build.getStatusDescriptor().getText();
        String rootUrl = this.server.getRootUrl();

        for (SBuildFeatureDescriptor feature : build.getBuildFeaturesOfType(FogBugzNotifierBuildFeature.FEATURE_TYPE)) {
            Map<String, String> featureParams = feature.getParameters();
            String url = featureParams.get("fbAddress");
            String token = featureParams.get("sToken");

            FogBugzEventData data = new FogBugzEventData();
            data.setAction("event");
            data.setEventType("build-success");
            data.setBug("6");
            data.setEventUtc(dtFinished);
            data.setMessage(statusDescription);
            data.setExternalUrl(rootUrl);

            //LOG.debug(String.format("Build feature '%s': server address=%s", feature.getType(), url));
            //LOG.info(buildStatusText +": "+ statusDescription);

            try {
                postData(url, token, data);
            }
            catch (Exception ex) {
                LOG.error("Failed to notify FogBugz Extended Events plugin.", ex);
            }
        }
    }

    private String postData(String url, List<NameValuePair> params) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        UrlEncodedFormEntity paramsEntity = new UrlEncodedFormEntity(params);
        httpPost.setEntity(paramsEntity);

        CloseableHttpResponse response = client.execute(httpPost);
        String responseString = new BasicResponseHandler().handleResponse(response);

        response.close();
        client.close();

        return responseString;
    }

    public String postData(String fogbugzUrl, String token, FogBugzEventData data) throws Exception {
        String pluginUrl = fogbugzUrl + "/default.asp?pg=pgPluginRaw&sPluginId=FBExtendedEvents%40goit.io";

        data.setToken(token);
        List<NameValuePair> params = data.toParams();

        String response = postData(pluginUrl, params);
        return response;
    }
}
