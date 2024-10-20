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
        restTemplate.getForObject("http://localhost:" + port + "/", String.class);
        // basically no-op
    }

    @Test
    public void swaggerUiTest() {
        restTemplate.getForObject("http://localhost:" + port + "/swagger-ui/index.html", String.class);
        // basically no-op
    }

    @Test
    public void botsApiTest() {
        BotController.BotsMonthResponse botsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/bots/month",
            BotController.BotsMonthResponse.class);
        assertNotNull(botsResponse);
        assertFalse(botsResponse.players().isEmpty());
    }

    @Test
    public void chatsApiTest() {
        var chatsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/chats?playerName={playerName}",
            ChatsController.ChatsResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(chatsResponse);
        assertTrue(chatsResponse.total() > 0);
    }

    @Test
    public void wordCountApiTest() {
        var wordCountResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/chats/word-count?word={word}",
            ChatsController.WordCount.class,
            Map.of(
                "word", "test"
            ));
        assertNotNull(wordCountResponse);
        assertTrue(wordCountResponse.count() > 0);
    }

    @Test
    public void wordSearchApiTest() {
        var wordSearchResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/chats/search?word={word}&endDate={endDate}",
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
        var connectionsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/connections?playerName={playerName}",
            ConnectionsController.ConnectionsResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(connectionsResponse);
        assertTrue(connectionsResponse.total() > 0);
    }

    @Test
    public void dataDumpApiTest() {
        var dataDumpResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/dump/player?playerName={playerName}",
            String.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(dataDumpResponse);
        assertFalse(dataDumpResponse.isEmpty());
    }

    @Test
    public void deathsApiTest() {
        var deathsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/deaths?playerName={playerName}",
            DeathsController.DeathsResponse.class,
            Map.of(
                "playerName", "rfresh2"
            ));
        assertNotNull(deathsResponse);
        assertTrue(deathsResponse.total() > 0);
    }

    @Test
    public void killsApiTest() {
        var killsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/kills?playerName={playerName}",
            DeathsController.KillsResponse.class,
            Map.of(
                "playerName", "rfresh2"
            ));
        assertNotNull(killsResponse);
        assertTrue(killsResponse.total() > 0);
    }

    @Test
    public void deathsTopMonthTest() {
        var deathsTopMonthResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/deaths/top/month",
            DeathsController.PlayerDeathOrKillCountResponse.class);
        assertNotNull(deathsTopMonthResponse);
        assertFalse(deathsTopMonthResponse.players().isEmpty());
    }

    @Test
    public void killsTopMonthTest() {
        var killsTopMonthResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/kills/top/month",
            DeathsController.PlayerDeathOrKillCountResponse.class);
        assertNotNull(killsTopMonthResponse);
        assertFalse(killsTopMonthResponse.players().isEmpty());
    }

    @Test
    public void playtimeApiTest() {
        var playtimeResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/playtime?playerName={playerName}",
            PlaytimeController.PlaytimeResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(playtimeResponse);
        assertTrue(playtimeResponse.playtimeSeconds() > 0);
    }

    @Test
    public void playtimeTopMonthApiTest() {
        var playtimeTopMonthResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/playtime/top/month",
            PlaytimeController.PlaytimeMonthResponse.class);
        assertNotNull(playtimeTopMonthResponse);
        assertFalse(playtimeTopMonthResponse.players().isEmpty());
    }

    @Test
    public void queueApiTest() {
        var queueResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/queue",
            QueueController.QueueData.class);
        assertNotNull(queueResponse);
        assertTrue(queueResponse.regular() >= 0);
    }

    @Test
    public void queueMonthTest() {
        var queueMonthResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/queue/month",
            QueueController.QueueLengthHistory.class);
        assertNotNull(queueMonthResponse);
        assertFalse(queueMonthResponse.queueData().isEmpty());
    }

    @Test
    public void queueEtaEquationApiTest() {
        var queueResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/queue/eta-equation",
            QueueController.QueueEtaEquation.class);
        assertNotNull(queueResponse);
        assertEquals(QueueController.QueueEtaEquation.INSTANCE, queueResponse);
    }

    @Test
    public void seenApiTest() {
        var seenResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/seen?playerName={playerName}",
            SeenController.SeenResponse.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(seenResponse);
        assertTrue(seenResponse.firstSeen().isBefore(seenResponse.lastSeen()));
    }

    @Test
    public void statsApiTest() {
        var statsResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/stats/player?playerName={playerName}",
            StatsController.PlayerStats.class,
            Map.of(
                "playerName", "hausemaster"
            ));
        assertNotNull(statsResponse);
        assertTrue(statsResponse.leaveCount() > 0);
    }

    @Test
    public void tablistApiTest() {
        var tablistResponse = restTemplate.getForObject(
            "http://localhost:" + port + "/tablist",
            TabListController.TablistResponse.class);
        assertNotNull(tablistResponse);
        assertFalse(tablistResponse.players().isEmpty());
    }

    @Test
    public void priorityPlayersApiTest() {
        var response = restTemplate.getForObject(
            "http://localhost:" + port + "/players/priority",
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
}
