package vc.api;

import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import vc.api.model.CraftheadProfileResponse;
import vc.api.model.ProfileData;

import java.util.ArrayList;

@Component
public class CraftheadRestClient {
    private final RestClient restClient;

    public CraftheadRestClient(final ClientHttpRequestFactory requestFactory) {
        var builder = RestClient.builder()
            .baseUrl("https://crafthead.net")
            .requestFactory(requestFactory)
            .defaultHeader("User-Agent", "2b2t.vc-discord");
        builder.messageConverters(converters -> converters.stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .forEach(c -> {
                var supportedMedia = c.getSupportedMediaTypes();
                final ArrayList<MediaType> newMediaTypes = new ArrayList<>(supportedMedia);
                newMediaTypes.add(new MediaType("text", "plain"));
                ((MappingJackson2HttpMessageConverter) c).setSupportedMediaTypes(newMediaTypes);
            })
        );
        this.restClient = builder.build();
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
