package teamcity;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PluginDescriptorStub implements PluginDescriptor {
    private String pluginResourcePath;

    public PluginDescriptorStub() {
        this.pluginResourcePath = "";
    }

    @NotNull
    public String getPluginName() {
        return null;
    }

    @NotNull
    public String getPluginResourcesPath() {
        return null;
    }

    @NotNull
    public String getPluginResourcesPath(@NotNull String relativePath) {
        return this.pluginResourcePath + relativePath;
    }

    public void setPluginResourcePath(String pluginResourcePath) {
        this.pluginResourcePath = pluginResourcePath;
    }

    @Nullable
    public String getParameterValue(@NotNull String key) {
        return null;
    }

    @Nullable
    public String getPluginVersion() {
        return null;
    }

    @NotNull
    public File getPluginRoot() {
        return null;
    }
}

