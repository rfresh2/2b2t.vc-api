package vc.api.mcprofile;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import vc.api.mcprofile.model.MCProfileBedrockResponse;
import vc.api.mcprofile.model.MCProfileJavaResponse;

import java.util.UUID;

@Component
public class MCProfileRestClient {
    private final RestClient restClient;
    public MCProfileRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://mcprofile.io/api/v1")
            .requestFactory(requestFactory)
            .build();
    }

    public MCProfileBedrockResponse getBedrockProfile(final String gamertag) {
        var response = restClient.get()
            .uri("/bedrock/gamertag/{gamertag}", gamertag)
            .retrieve()
            .body(MCProfileBedrockResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mcprofile.io");
        }
        return response;
    }


    public MCProfileBedrockResponse getBedrockProfile(final UUID uuid) {
        var response = restClient.get()
            .uri("/bedrock/xuid/{xuid}", xuidFromUUID(uuid))
            .retrieve()
            .body(MCProfileBedrockResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mcprofile.io");
        }
        return response;
    }

    public MCProfileJavaResponse getProfile(String username) {
        var response = restClient.get()
            .uri("/java/username/{username}", username)
            .retrieve()
            .body(MCProfileJavaResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mcprofile.io");
        }
        return response;
    }

    public MCProfileJavaResponse getProfile(UUID uuid) {
        var response = restClient.get()
            .uri("/java/uuid/{uuid}", uuid)
            .retrieve()
            .body(MCProfileJavaResponse.class);
        if (response == null) {
            throw new RestClientException("Received invalid response from mcprofile.io");
        }
        return response;
    }

    public String xuidFromUUID(UUID uuid) {
        return Long.toUnsignedString(uuid.getLeastSignificantBits());
    }
}
