package teamcity;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FogBugzNotifierBuildFeature extends BuildFeature {
    public static final String FEATURE_TYPE = "io.goit.teamcity.FogBugzNotifierBuildFeature";

    private final PluginDescriptor myPluginDescriptor;

    public FogBugzNotifierBuildFeature(PluginDescriptor myPluginDescriptor) {
        this.myPluginDescriptor = myPluginDescriptor;
    }

    @NotNull
    @Override
    public String getType() {
        return FEATURE_TYPE;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Report build status to FogBugz";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return myPluginDescriptor.getPluginResourcesPath("notifierSettings.jsp");
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return true;
    }

    @NotNull
    @Override
    public String describeParameters(@NotNull Map<String, String> params) {
        String url = params.get("fbAddress");

        return "Report build status to FogBugz at "+ url;
    }
}
