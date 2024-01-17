package vc.api;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import vc.api.model.MojangProfileResponse;
import vc.api.model.ProfileData;

@Component
public class MojangRestClient {
    private final RestClient restClient;

    public MojangRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://api.mojang.com")
            .requestFactory(requestFactory)
            .build();
    }

    public ProfileData getProfileFromUsername(final String username) {
        return restClient.get()
            .uri("/users/profiles/minecraft/{username}", username)
            .retrieve()
            .body(MojangProfileResponse.class);
    }
}
