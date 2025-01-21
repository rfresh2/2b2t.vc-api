package vc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vc.api.CraftheadRestClient;
import vc.api.MinetoolsRestClient;
import vc.api.MojangRestClient;
import vc.controller.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIf("vc.ApiTests#isTestEnabled")
public class ApiTests {

    static boolean isTestEnabled() {
        return Boolean.parseBoolean(System.getProperty("apiTests", "false"));
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MojangRestClient mojangRestClient;
    @Autowired
    private CraftheadRestClient craftheadRestClient;
    @Autowired
    private MinetoolsRestClient minetoolsRestClient;

    @Test
    public void homepageTest() {
        httpRequest("/", String.class);
        // basically no-op
    }

    @Test
    public void swaggerUiTest() {
        httpRequest("/swagger-ui/index.html", String.class);
        // basically no-op
    }

    @Test
    public void botsApiTest() {
        var botsResponse = httpRequest("/bots/month", BotController.BotsMonthResponse.class);
        assertNotNull(botsResponse);
        assertFalse(botsResponse.players().isEmpty());
    }

    @Test
    public void chatsApiTest() {
        var chatsResponse = httpRequest("/chats?playerName={playerName}",
            ChatsController.ChatsResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(chatsResponse);
        assertTrue(chatsResponse.total() > 0);
    }

    @Test
    public void chatsWindowTest() {
        var chatWindowResponse = httpRequest(
            "/chats/window?startDate={startDate}&endDate={endDate}",
            ChatsController.ChatWindowResponse.class,
            Map.of(
                "startDate", "2023-01-01T00:00:00",
                "endDate", "2023-01-01T01:00:00"
            ));
        assertNotNull(chatWindowResponse);
        assertFalse(chatWindowResponse.chats().isEmpty());
    }

    @Test
    public void chatWindowMissingStartDateTest() {
        var chatWindowResponse = httpRequest(
            "/chats/window?endDate={endDate}",
            String.class,
            Map.of(
                "endDate", "2023-01-01T01:00:00"
            ));
        assertNotNull(chatWindowResponse);
        assertEquals("startDate is required", chatWindowResponse);
    }

    @Test
    public void wordCountApiTest() {
        var wordCountResponse = httpRequest("/chats/word-count?word={word}",
            ChatsController.WordCount.class,
            Map.of(
                "word", "test"
            ));
        assertNotNull(wordCountResponse);
        assertTrue(wordCountResponse.count() > 0);
    }

    @Test
    public void wordSearchApiTest() {
        var wordSearchResponse = httpRequest("/chats/search?word={word}&endDate={endDate}",
            ChatsController.ChatSearchResponse.class,
            Map.of(
                "word", "test",
                "endDate", "2021-01-01"
            ));
        assertNotNull(wordSearchResponse);
        assertTrue(wordSearchResponse.total() > 0);
    }

    @Test
    public void connectionsApiTest() {
        var connectionsResponse = httpRequest("/connections?playerName={playerName}",
            ConnectionsController.ConnectionsResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(connectionsResponse);
        assertTrue(connectionsResponse.total() > 0);
    }

    @Test
    public void connectionsWindowTest() {
        var connectionsWindowResponse = httpRequest("/connections/window?startDate={startDate}&endDate={endDate}",
               ConnectionsController.ConnectionsWindowResponse.class,
               Map.of(
                   "startDate", "2023-01-01T00:00:00Z",
                   "endDate", "2023-01-01T01:00:00Z"
               ));
        assertNotNull(connectionsWindowResponse);
        assertFalse(connectionsWindowResponse.connections().isEmpty());
    }

    @Test
    public void dataDumpApiTest() {
        var dataDumpResponse = httpRequest("/dump/player?playerName={playerName}",
            String.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(dataDumpResponse);
        assertFalse(dataDumpResponse.isEmpty());
    }

    @Test
    public void deathsApiTest() {
        var deathsResponse = httpRequest("/deaths?playerName={playerName}",
            DeathsController.DeathsResponse.class,
            Map.of(
                "playerName", "rfresh2"
            ));
        assertNotNull(deathsResponse);
        assertTrue(deathsResponse.total() > 0);
    }

    @Test
    public void deathsWindowTest() {
        var deathsWindowResponse = httpRequest("/deaths/window?startDate={startDate}&endDate={endDate}",
               DeathsController.DeathsWindowResponse.class,
               Map.of(
                   "startDate", "2023-01-01T00:00:00",
                   "endDate", "2023-01-01T01:00:00"
               ));
        assertNotNull(deathsWindowResponse);
        assertFalse(deathsWindowResponse.deaths().isEmpty());
    }

    @Test
    public void killsApiTest() {
        var killsResponse = httpRequest("/kills?playerName={playerName}",
            DeathsController.KillsResponse.class,
            Map.of(
                "playerName", "rfresh2"
            ));
        assertNotNull(killsResponse);
        assertTrue(killsResponse.total() > 0);
    }

    @Test
    public void deathsTopMonthTest() {
        var deathsTopMonthResponse = httpRequest("/deaths/top/month",
            DeathsController.PlayerDeathOrKillCountResponse.class);
        assertNotNull(deathsTopMonthResponse);
        assertFalse(deathsTopMonthResponse.players().isEmpty());
    }

    @Test
    public void killsTopMonthTest() {
        var killsTopMonthResponse = httpRequest("/kills/top/month",
            DeathsController.PlayerDeathOrKillCountResponse.class);
        assertNotNull(killsTopMonthResponse);
        assertFalse(killsTopMonthResponse.players().isEmpty());
    }

    @Test
    public void nonPrioTimeLimitsTest() {
        var nonPrioTimeLimitsResponse = httpRequest("/limits/session-time-limit",
            LimitsController.SessionTimeLimitResponse.class);
        assertNotNull(nonPrioTimeLimitsResponse);
        assertEquals(LimitsController.SessionTimeLimitResponse.INSTANCE, nonPrioTimeLimitsResponse);
    }

    @Test
    public void playtimeApiTest() {
        var playtimeResponse = httpRequest("/playtime?playerName={playerName}",
            PlaytimeController.PlaytimeResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(playtimeResponse);
        assertTrue(playtimeResponse.playtimeSeconds() > 0);
    }

    @Test
    public void playtimeTopMonthApiTest() {
        var playtimeTopMonthResponse = httpRequest("/playtime/top/month",
            PlaytimeController.PlaytimeMonthResponse.class);
        assertNotNull(playtimeTopMonthResponse);
        assertFalse(playtimeTopMonthResponse.players().isEmpty());
    }

    @Test
    public void playtimeTopAllTimeApiTest() {
        var playtimeAllTimeResponse = httpRequest("/playtime/top",
            PlaytimeController.PlaytimeAllTimeResponse.class);
        assertNotNull(playtimeAllTimeResponse);
        assertFalse(playtimeAllTimeResponse.players().isEmpty());
    }

    @Test
    public void queueApiTest() {
        var queueResponse = httpRequest("/queue",
            QueueController.QueueData.class);
        assertNotNull(queueResponse);
        assertTrue(queueResponse.regular() >= 0);
    }

    @Test
    public void queueMonthTest() {
        var queueMonthResponse = httpRequest("/queue/month",
            QueueController.QueueLengthHistory.class);
        assertNotNull(queueMonthResponse);
        assertFalse(queueMonthResponse.queueData().isEmpty());
    }

    @Test
    public void queueEtaEquationApiTest() {
        var queueResponse = httpRequest("/queue/eta-equation",
            QueueController.QueueEtaEquation.class);
        assertNotNull(queueResponse);
        assertEquals(QueueController.QueueEtaEquation.INSTANCE, queueResponse);
    }

    @Test
    public void seenApiTest() {
        var seenResponse = httpRequest("/seen?playerName={playerName}",
            SeenController.SeenResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(seenResponse);
        assertTrue(seenResponse.firstSeen().isBefore(seenResponse.lastSeen()));
    }

    @Test
    public void statsApiTest() {
        var statsResponse = httpRequest("/stats/player?playerName={playerName}",
            StatsController.PlayerStats.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(statsResponse);
        assertTrue(statsResponse.leaveCount() > 0);
    }

    @Test
    public void tablistApiTest() {
        var tablistResponse = httpRequest("/tablist",
            TabListController.TablistResponse.class);
        assertNotNull(tablistResponse);
        assertFalse(tablistResponse.players().isEmpty());
    }

    @Test
    public void tablistInfoApiTest() {
        var tablistResponse = httpRequest("/tablist/info",
            TabListController.TablistInfoResponse.class);
        assertNotNull(tablistResponse);
        assertFalse(tablistResponse.players().isEmpty());
    }

    @Test
    public void priorityPlayersApiTest() {
        var response = httpRequest("/players/priority",
            PriorityPlayersController.PriorityPlayersResponse.class);
        assertNotNull(response);
        assertFalse(response.players().isEmpty());
    }

    @Test
    public void playerLookupMinetoolsTest() {
        var response = minetoolsRestClient.getProfileFromUsername("rfresh2");
        assertNotNull(response);
        assertEquals("rfresh2", response.name());
    }

    @Test
    public void playerLookupMojangTest() {
        var response = mojangRestClient.getProfileFromUsername("rfresh2");
        assertNotNull(response);
        assertEquals("rfresh2", response.name());
    }

    @Test
    public void playerLookupCraftheadTest() {
        var response = craftheadRestClient.getProfile("rfresh2");
        assertNotNull(response);
        assertEquals("rfresh2", response.name());
    }

    @Test
    public void timeTest() {
        var response = httpRequest("/time",
            TimeController.TimeResponse.class);
        assertNotNull(response);
        assertTrue(response.worldTime() > 0);
        assertNotNull(response.lastUpdated());
    }

    private <T> T httpRequest(String url, Class<T> responseType) {
        return restTemplate.getForObject("http://localhost:" + port + url, responseType);
    }

    private <T> T httpRequest(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        return restTemplate.getForObject("http://localhost:" + port + url, responseType, uriVariables);
    }
}
