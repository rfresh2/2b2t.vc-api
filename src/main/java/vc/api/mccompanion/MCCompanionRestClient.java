package vc.api.mccompanion;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import vc.api.mccompanion.model.MCCompanionBedrockResponse;
import vc.api.mccompanion.model.MCCompanionJavaResponse;

import java.util.UUID;

@Component
public class MCCompanionRestClient {
    private final RestClient restClient;
    public MCCompanionRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://api.mccompanion.net")
            .requestFactory(requestFactory)
            .build();
    }

    public MCCompanionBedrockResponse getBedrockProfile(final String gamertag) {
        var response = restClient.get()
            .uri("/api/lookup/bedrock/{gamertag}", gamertag)
            .retrieve()
            .body(MCCompanionBedrockResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mccompanion.net");
        }
        return response;
    }

    public MCCompanionBedrockResponse getBedrockProfile(final UUID uuid) {
        var response = restClient.get()
            .uri("/api/lookup/bedrock/{xuid}", xuidFromUUID(uuid))
            .retrieve()
            .body(MCCompanionBedrockResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mccompanion.net");
        }
        return response;
    }

    public MCCompanionJavaResponse getProfile(String username) {
        var response = restClient.get()
            .uri("/api/lookup/java/{username}", username)
            .retrieve()
            .body(MCCompanionJavaResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mccompanion.net");
        }
        return response;
    }

    public MCCompanionJavaResponse getProfile(UUID uuid) {
        var response = restClient.get()
            .uri("/api/lookup/java/{uuid}", uuid.toString())
            .retrieve()
            .body(MCCompanionJavaResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mccompanion.net");
        }
        return response;
    }

    public String xuidFromUUID(UUID uuid) {
        return Long.toUnsignedString(uuid.getLeastSignificantBits());
    }
}
