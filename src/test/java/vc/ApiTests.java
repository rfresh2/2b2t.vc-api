package vc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import vc.controller.*;
import vc.data.dto.tables.pojos.Queuelength;

import java.util.List;
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

    @Test
    public void botsApiTest() {
        List botsResponse = restTemplate.getForObject("http://localhost:" + port + "/bots/month", List.class);
        assertNotNull(botsResponse);
        assertFalse(botsResponse.isEmpty());
    }

    @Test
    public void chatsApiTest() {
        var chatsResponse = restTemplate.getForObject("http://localhost:" + port + "/chats?playerName={playerName}",
                                                      ChatsController.ChatsResponse.class,
                                                      Map.of(
                                                                                "playerName", "rfresh2"
                                                                            ));
        assertNotNull(chatsResponse);
        assertTrue(chatsResponse.total() > 0);
    }

    @Test
    public void connectionsApiTest() {
        var connectionsResponse = restTemplate.getForObject("http://localhost:" + port + "/connections?playerName={playerName}",
                                                                            ConnectionsController.ConnectionsResponse.class,
                                                                            Map.of(
                                                                                "playerName", "rfresh2"
                                                                            ));
        assertNotNull(connectionsResponse);
        assertTrue(connectionsResponse.total() > 0);
    }

    @Test
    public void dataDumpApiTest() {
        var dataDumpResponse = restTemplate.getForObject("http://localhost:" + port + "/dump/player?playerName={playerName}",
                                                         String.class,
                                                         Map.of(
                                                             "playerName", "rfresh2"
                                                         ));
        assertNotNull(dataDumpResponse);
        assertFalse(dataDumpResponse.isEmpty());
    }

    @Test
    public void deathsApiTest() {
        var deathsResponse = restTemplate.getForObject("http://localhost:" + port + "/deaths?playerName={playerName}",
                                                            DeathsController.DeathsResponse.class,
                                                            Map.of(
                                                                "playerName", "rfresh2"
                                                            ));
        assertNotNull(deathsResponse);
        assertTrue(deathsResponse.total() > 0);
    }

    @Test
    public void killsApiTest() {
        var killsResponse = restTemplate.getForObject("http://localhost:" + port + "/kills?playerName={playerName}",
                                                       DeathsController.KillsResponse.class,
                                                       Map.of(
                                                           "playerName", "rfresh2"
                                                       ));
        assertNotNull(killsResponse);
        assertTrue(killsResponse.total() > 0);
    }

    @Test
    public void namesApiTest() {
        var namesResponse = restTemplate.getForObject("http://localhost:" + port + "/names?playerName={playerName}",
                                                       List.class,
                                                       Map.of(
                                                           "playerName", "rfresh2"
                                                       ));
        assertNotNull(namesResponse);
        assertFalse(namesResponse.isEmpty());
    }

    @Test
    public void playtimeApiTest() {
        var playtimeResponse = restTemplate.getForObject("http://localhost:" + port + "/playtime?playerName={playerName}",
                                                            PlaytimeController.PlaytimeResponse.class,
                                                            Map.of(
                                                                "playerName", "rfresh2"
                                                            ));
        assertNotNull(playtimeResponse);
        assertTrue(playtimeResponse.getPlaytimeSeconds() > 0);
    }

    @Test
    public void playtimeTopMonthApiTest() {
        var playtimeTopMonthResponse = restTemplate.getForObject("http://localhost:" + port + "/playtime/top/month",
                                                            List.class);
        assertNotNull(playtimeTopMonthResponse);
        assertFalse(playtimeTopMonthResponse.isEmpty());
    }

    @Test
    public void queueApiTest() {
        var queueResponse = restTemplate.getForObject("http://localhost:" + port + "/queue",
                                                            Queuelength.class);
        assertNotNull(queueResponse);
        assertTrue(queueResponse.getRegular() >= 0);
    }

    @Test
    public void seenApiTest() {
        var seenResponse = restTemplate.getForObject("http://localhost:" + port + "/seen?playerName={playerName}",
                                                            SeenController.SeenResponse.class,
                                                            Map.of(
                                                                "playerName", "rfresh2"
                                                            ));
        assertNotNull(seenResponse);
        assertTrue(seenResponse.firstSeen().isBefore(seenResponse.lastSeen()));
    }

    @Test
    public void statsApiTest() {
        var statsResponse = restTemplate.getForObject("http://localhost:" + port + "/stats/player?playerName={playerName}",
                                                      StatsController.PlayerStats.class,
                                                      Map.of(
                                                          "playerName", "rfresh2"
                                                      ));
        assertNotNull(statsResponse);
        assertTrue(statsResponse.leaveCount() > 0);
    }

    @Test
    public void tablistApiTest() {
        var tablistResponse = restTemplate.getForObject("http://localhost:" + port + "/tablist",
                                                            List.class);
        assertNotNull(tablistResponse);
        assertFalse(tablistResponse.isEmpty());
    }
}
