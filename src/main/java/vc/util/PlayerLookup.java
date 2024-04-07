package vc.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import vc.api.CraftheadRestClient;
import vc.api.MinetoolsRestClient;
import vc.api.MojangRestClient;
import vc.api.model.ProfileData;
import vc.api.model.ProfileDataImpl;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerLookup {
    private static final Logger logger = LoggerFactory.getLogger(PlayerLookup.class);
    private final MojangRestClient mojangRestClient;
    private final CraftheadRestClient craftheadRestClient;
    private final MinetoolsRestClient minetoolsRestClient;
    private final Cache<String, ProfileData> uuidCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(250)
        .build();

    public PlayerLookup(
        MojangRestClient mojangRestClient,
        CraftheadRestClient craftheadRestClient,
        MinetoolsRestClient minetoolsRestClient
    ) {
        this.mojangRestClient = mojangRestClient;
        this.craftheadRestClient = craftheadRestClient;
        this.minetoolsRestClient = minetoolsRestClient;
    }

    public Optional<ProfileData> getPlayerIdentity(final String playerName) {
        final ProfileData identityFromCache = uuidCache.getIfPresent(playerName.toLowerCase().trim());
        if (identityFromCache != null)
            return Optional.of(identityFromCache);
        var playerIdentity = lookupIdentityMojang(playerName)
            .or(() -> lookupIdentityCrafthead(playerName)
                .or(() -> lookupIdentityMinetools(playerName)));
        playerIdentity.ifPresent(identity -> uuidCache.put(playerName.toLowerCase().trim(), identity));
        return playerIdentity;
    }

    private Optional<ProfileData> lookupIdentityMojang(final String playerName) {
        try {
            ProfileData profile = mojangRestClient.getProfileFromUsername(playerName);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from Mojang: {}", e.getStatusCode().value(), playerName);
        } catch (final RestClientException e) {
            logger.error("Bad status response from Mojang: {}", playerName, e);
        } catch (final Exception e) {
            logger.error("Mojang unexpected error: {}", playerName, e);
        }
        return Optional.empty();
    }

    private Optional<ProfileData> lookupIdentityCrafthead(final String playerName) {
        try {
            ProfileData profile = craftheadRestClient.getProfile(playerName);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from Crafthead: {}", e.getStatusCode().value(), playerName);
        } catch (final RestClientException e) {
            logger.error("Bad status response from Crafthead: {}", playerName);
        } catch (final Exception e) {
            logger.error("Crafthead unexpected error: {}", playerName, e);
        }
        return Optional.empty();
    }

    private Optional<ProfileData> lookupIdentityMinetools(final String playerName) {
        try {
            ProfileData profile = minetoolsRestClient.getProfileFromUsername(playerName);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from MineTools: {}", e.getStatusCode().value(), playerName);
        } catch (final RestClientException e) {
            logger.error("Bad status response from MineTools: {}", playerName);
        } catch (final Exception e) {
            logger.error("MineTools unexpected error: {}", playerName, e);
        }
        return Optional.empty();
    }

    public URL getAvatarURL(UUID uuid) {
        return getAvatarURL(uuid.toString().replace("-", ""));
    }

    public URL getAvatarURL(String playerName) {
        try {
            return new URL(String.format("https://crafthead.net/helm/%s/64", playerName));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<UUID> getOrResolveUuid(final UUID uuid, final String username) {
        if (uuid != null) return Optional.of(uuid);
        return getPlayerIdentity(username.trim()).map(ProfileData::uuid);
    }

    public Optional<ProfileData> getOrResolvePlayerIdentity(final UUID uuid, final String username) {
        if (uuid != null) return Optional.of(new ProfileDataImpl(username, uuid));
        return getPlayerIdentity(username.trim());
    }
}
