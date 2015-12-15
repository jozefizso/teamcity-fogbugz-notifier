package teamcity;

import jetbrains.buildServer.serverSide.SBuildServer;

import static org.mockito.Mockito.*;

public class FogBugzNotifierTests {

    @org.testng.annotations.Test
    public void register_notifierInstance_registerInstanceAsListener() throws Exception {
        // Arrange
        SBuildServer serverMock = mock(SBuildServer.class);
        FogBugzNotifier notifier = new FogBugzNotifier(serverMock);

        // Act
        notifier.register();

        // Assert
        verify(serverMock).addListener(notifier);
    }
}
