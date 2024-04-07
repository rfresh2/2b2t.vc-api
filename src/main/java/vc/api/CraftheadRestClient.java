package vc.api;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import vc.api.model.CraftheadProfileResponse;
import vc.api.model.ProfileData;

@Component
public class CraftheadRestClient {
    private final RestClient restClient;

    public CraftheadRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://crafthead.net")
            .requestFactory(requestFactory)
            .defaultHeader("User-Agent", "2b2t.vc-api")
            .build();
    }

    public ProfileData getProfile(final String username) {
        var response = restClient.get()
            .uri("/profile/{username}", username)
            .retrieve()
            .body(CraftheadProfileResponse.class);
        if (response == null || response.id() == null) {
            throw new RestClientException("Received invalid response from crafthead");
        }
        return response;
    }
}
