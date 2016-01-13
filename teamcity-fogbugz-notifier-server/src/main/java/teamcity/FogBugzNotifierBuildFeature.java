package teamcity;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FogBugzNotifierBuildFeature extends BuildFeature {
    private static final String FEATURE_TYPE = "FogBugzNotifierBuildFeature";

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
        return "FogBugz Extended Notifications";
    }

    @Nullable
    @Override
    public String getEditParametersUrl() {
        return myPluginDescriptor.getPluginResourcesPath("notifierSettings.jsp");
    }

    @Override
    public boolean isMultipleFeaturesPerBuildTypeAllowed() {
        return false;
    }

    @Override
    public String describeParameters(Map<String, String> params) {
        return "Example Build Feature plugin";
    }
}
