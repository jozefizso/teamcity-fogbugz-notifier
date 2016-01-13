package teamcity;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FogBugzNotifierBuildFeatureTest {
    @Test
    public void FogBugzNotifierBuildFeature_FEATURE_TYPE_HasCorrectValue() throws Exception {
        // Arrange & Act
        String actualFogBugzNotifierBuildFeatureType = FogBugzNotifierBuildFeature.FEATURE_TYPE;

        // Assert
        assertEquals(actualFogBugzNotifierBuildFeatureType, "io.goit.teamcity.FogBugzNotifierBuildFeature");
    }

    @Test
    public void constructor_objectHasCorrectPropertyValues() throws Exception {
        // Arrange
        PluginDescriptor descMock = mock(PluginDescriptor.class);

        // Act
        FogBugzNotifierBuildFeature buildFeature = new FogBugzNotifierBuildFeature(descMock);

        // Assert
        assertEquals(buildFeature.getType(), FogBugzNotifierBuildFeature.FEATURE_TYPE);
        assertEquals(buildFeature.getDisplayName(), "Report build status to FogBugz");
        assertTrue(buildFeature.isMultipleFeaturesPerBuildTypeAllowed(), "FogBugzNotifierBuildFeature can be defined multiple times per build.");
    }

    @DataProvider(name = "providePluginResourcesPaths")
    public Object[][] providePluginResourcesPaths() {
        return new Object[][] {
            { "", "notifierSettings.jsp" },
            { "/", "/notifierSettings.jsp" },
            { "/teamcity/plugins/.unpacked/notifier/resources/", "/teamcity/plugins/.unpacked/notifier/resources/notifierSettings.jsp" }
        };
    }

    @Test(dataProvider = "providePluginResourcesPaths")
    public void getEditParametersUrl_PluginResourcesPathProvidedByPluginDescriptor_ReturnsCorrectUrl(String pluginResourcePath, String expectedEditParametersUrl) throws Exception {
        // Arrange
        PluginDescriptorStub descriptorStub = new PluginDescriptorStub();
        descriptorStub.setPluginResourcePath(pluginResourcePath);

        FogBugzNotifierBuildFeature buildFeature = new FogBugzNotifierBuildFeature(descriptorStub);

        // Act
        String actualEditParametersUrl = buildFeature.getEditParametersUrl();

        // Assert
        assertEquals(actualEditParametersUrl, expectedEditParametersUrl);
    }

    @Test
    public void describeParameters_PluginResourcesPathProvidedByPluginDescriptor_ReturnsCorrectUrl() throws Exception {
        // Arrange
        PluginDescriptorStub descriptorStub = new PluginDescriptorStub();
        FogBugzNotifierBuildFeature buildFeature = new FogBugzNotifierBuildFeature(descriptorStub);

        Map<String, String> params = new HashMap<String, String>();
        params.put("fbAddress", "https://fb.localtest.me/");

        // Act
        String actualEditParametersUrl = buildFeature.describeParameters(params);

        // Assert
        assertEquals(actualEditParametersUrl, "Report build status to FogBugz at https://fb.localtest.me/");
    }
}
