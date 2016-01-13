package teamcity;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import java.util.Map;

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
        for (SBuildFeatureDescriptor feature : build.getBuildFeaturesOfType(FogBugzNotifierBuildFeature.FEATURE_TYPE)) {
            Map<String, String> params = feature.getParameters();
            String url = params.get("fbAddress");

            LOG.debug(String.format("Build feature '%s': server address=%s", feature.getType(), url));
        }
    }
}
