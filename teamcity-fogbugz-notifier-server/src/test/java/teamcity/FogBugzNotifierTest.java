package teamcity;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import jdk.nashorn.internal.parser.JSONParser;
import jetbrains.buildServer.serverSide.BuildsManager;
import jetbrains.buildServer.serverSide.SBuildServer;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

public class FogBugzNotifierTest {

    @org.testng.annotations.Test
    public void register_notifierInstance_registerInstanceAsListener() throws Exception {
        // Arrange
        SBuildServer serverMock = mock(SBuildServer.class);
        BuildsManager buildsManagerMock = mock(BuildsManager.class);
        FogBugzNotifier notifier = new FogBugzNotifier(serverMock, buildsManagerMock);

        // Act
        notifier.register();

        // Assert
        verify(serverMock).addListener(notifier);
    }


    @org.testng.annotations.Test(enabled = false)
    public void postData_integrationFogBugzInstance_postsCorrectBuildStatus() throws Exception {
        // Arrange
        SBuildServer serverMock = mock(SBuildServer.class);
        BuildsManager buildsManagerMock = mock(BuildsManager.class);
        FogBugzNotifier notifier = new FogBugzNotifier(serverMock, buildsManagerMock);

        String fogbugzUrl = "http://localhost/fb";
        String token = "5pkrbmabac5bidigiriac70dtkc55h";
        DateTime finished = new DateTime(2016, 12, 17, 14, 57, 25, DateTimeZone.UTC);

        FogBugzEventData data = new FogBugzEventData();
        data.setAction("event");
        data.setEventType("build-success");
        data.setToken(token);
        data.setBug("6");
        data.setEventUtc(finished);

        // Act
        String response = notifier.postData(fogbugzUrl, token, data);

        // Assert
        assertThat(response, not(isEmptyOrNullString()));
        JsonObject responseJson = Json.parse(response).asObject();

        int ixExtendedEventId = responseJson.getInt("ixExtendedEvent", 0);
        assertThat(ixExtendedEventId, greaterThan(0));
    }
}
