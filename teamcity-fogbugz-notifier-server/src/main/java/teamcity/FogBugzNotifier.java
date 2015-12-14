package teamcity;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.SRunningBuild;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;

public class FogBugzNotifier extends BuildServerAdapter {
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
        super.buildFinished(build);
    }
}
