package vc.api.mojang;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import vc.api.model.ProfileData;
import vc.api.mojang.model.MojangProfileResponse;

import java.util.UUID;

@Component
public class MojangRestClient {
    private final RestClient restClient;

    public MojangRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://api.minecraftservices.com")
            .requestFactory(requestFactory)
            .build();
    }

    public ProfileData getProfile(final String username) {
        return restClient.get()
            .uri("/minecraft/profile/lookup/name/{username}", username)
            .retrieve()
            .body(MojangProfileResponse.class);
    }

    public ProfileData getProfile(final UUID uuid) {
        return restClient.get()
            .uri("/minecraft/profile/lookup/{uuid}", uuid)
            .retrieve()
            .body(MojangProfileResponse.class);
    }
}
