package vc.api;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import vc.api.model.MinetoolsUuidResponse;
import vc.api.model.ProfileData;

import java.util.Objects;

@Component
public class MinetoolsRestClient {
    private final RestClient restClient;

    public MinetoolsRestClient(final ClientHttpRequestFactory requestFactory) {
        this.restClient = RestClient.builder()
            .baseUrl("https://api.minetools.eu")
            .requestFactory(requestFactory)
            .build();
    }

    public ProfileData getProfileFromUsername(final String username) {
        var response = restClient.get()
            .uri("/uuid/{username}", username)
            .retrieve()
            .body(MinetoolsUuidResponse.class);
        if (response == null || !Objects.equals(response.status(), "OK")) {
            throw new RestClientException("Received invalid response from minetools");
        }
        return response;
    }
}
