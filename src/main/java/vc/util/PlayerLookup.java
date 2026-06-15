package vc.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import vc.api.crafthead.CraftheadRestClient;
import vc.api.mcprofile.MCProfileRestClient;
import vc.api.mcprofile.model.MCProfileBedrockResponse;
import vc.api.model.ProfileData;
import vc.api.mojang.MojangRestClient;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class PlayerLookup {
    private static final Logger logger = LoggerFactory.getLogger(PlayerLookup.class);
    private final MojangRestClient mojangRestClient;
    private final CraftheadRestClient craftheadRestClient;
    private final MCProfileRestClient mcProfileRestClient;
    private final Cache<String, ProfileData> uuidCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(30))
        .maximumSize(250)
        .build();
    public static final Pattern validUsernamePattern = Pattern.compile("[a-zA-Z0-9_]{1,16}");
    public static final Pattern bedrockUsernamePattern = Pattern.compile("\\.[a-zA-Z0-9_]{1,16}");

    public PlayerLookup(
        MojangRestClient mojangRestClient,
        CraftheadRestClient craftheadRestClient,
        MCProfileRestClient mcProfileRestClient
    ) {
        this.mojangRestClient = mojangRestClient;
        this.craftheadRestClient = craftheadRestClient;
        this.mcProfileRestClient = mcProfileRestClient;
    }

    public Optional<ProfileData> getPlayerIdentity(final String playerName) {
        final ProfileData identityFromCache = uuidCache.getIfPresent(playerName.toLowerCase().trim());
        if (identityFromCache != null)
            return Optional.of(identityFromCache);
        if (isBedrockUsername(playerName)) {
            // https://github.com/GeyserMC/Floodgate/blob/a7729114bf00a3f5c6756cd66f9c94e2bfcb8ed0/core/src/main/java/org/geysermc/floodgate/addon/data/HandshakeDataImpl.java#L69-L77
            var bedrockName = playerName.substring(1).replace("_", " "); // chop off . prefix
            var playerIdentity = lookupIdentityBedrock(bedrockName);
            playerIdentity.ifPresent(identity -> uuidCache.put(playerName.toLowerCase().trim(), identity));
            return playerIdentity.map(r -> (ProfileData) r);
        }
        var playerIdentity = lookupIdentityMojang(playerName)
            .or(() -> lookupIdentityCrafthead(playerName)
                .or(() -> lookupIdentityMCProfile(playerName)));
        playerIdentity.ifPresent(identity -> uuidCache.put(playerName.toLowerCase().trim(), identity));
        return playerIdentity;
    }

    public Optional<ProfileData> getPlayerIdentity(final UUID uuid) {
        final ProfileData identityFromCache = uuidCache.getIfPresent(uuid.toString());
        if (identityFromCache != null)
            return Optional.of(identityFromCache);
        if (isBedrockUUID(uuid)) {
            var playerIdentity = lookupIdentityBedrock(uuid);
            playerIdentity.ifPresent(identity -> uuidCache.put(uuid.toString(), identity));
            return playerIdentity.map(r -> (ProfileData) r);
        }
        var playerIdentity = lookupIdentityMojang(uuid)
            .or(() -> lookupIdentityCrafthead(uuid)
                .or(() -> lookupIdentityMCProfile(uuid)));
        playerIdentity.ifPresent(identity -> uuidCache.put(uuid.toString(), identity));
        return playerIdentity;
    }

    private Optional<ProfileData> lookupIdentityMojang(final String playerName) {
        try {
            ProfileData profile = mojangRestClient.getProfile(playerName);
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

    private Optional<ProfileData> lookupIdentityMojang(final UUID uuid) {
        try {
            ProfileData profile = mojangRestClient.getProfile(uuid);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from Mojang: {}", e.getStatusCode().value(), uuid);
        } catch (final RestClientException e) {
            logger.error("Bad status response from Mojang: {}", uuid, e);
        } catch (final Exception e) {
            logger.error("Mojang unexpected error: {}", uuid, e);
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

    private Optional<ProfileData> lookupIdentityCrafthead(final UUID uuid) {
        try {
            ProfileData profile = craftheadRestClient.getProfile(uuid);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from Crafthead: {}", e.getStatusCode().value(), uuid);
        } catch (final RestClientException e) {
            logger.error("Bad status response from Crafthead: {}", uuid);
        } catch (final Exception e) {
            logger.error("Crafthead unexpected error: {}", uuid, e);
        }
        return Optional.empty();
    }

    private Optional<ProfileData> lookupIdentityMCProfile(final String playerName) {
        try {
            ProfileData profile = mcProfileRestClient.getProfile(playerName);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from MCProfile: {}", e.getStatusCode().value(), playerName);
        } catch (final RestClientException e) {
            logger.error("Bad status response from MCProfile: {}", playerName);
        } catch (final Exception e) {
            logger.error("MCProfile unexpected error: {}", playerName, e);
        }
        return Optional.empty();
    }

    private Optional<ProfileData> lookupIdentityMCProfile(final UUID uuid) {
        try {
            ProfileData profile = mcProfileRestClient.getProfile(uuid);
            return Optional.of(profile);
        } catch (final RestClientResponseException e) {
            logger.error("{} from MCProfile: {}", e.getStatusCode().value(), uuid);
        } catch (final RestClientException e) {
            logger.error("Bad status response from MCProfile: {}", uuid);
        } catch (final Exception e) {
            logger.error("MCProfile unexpected error: {}", uuid, e);
        }
        return Optional.empty();
    }

    private Optional<MCProfileBedrockResponse> lookupIdentityBedrock(final String playerName) {
        try {
            var response = mcProfileRestClient.getBedrockProfile(playerName);
            return Optional.of(response);
        } catch (final RestClientResponseException e) {
            logger.error("{} from MCProfile: {}", e.getStatusCode().value(), playerName);
        } catch (final RestClientException e) {
            logger.error("Bad status response from MCProfile: {}", playerName);
        } catch (final Exception e) {
            logger.error("MCProfile unexpected error: {}", playerName, e);
        }
        return Optional.empty();
    }

    private Optional<MCProfileBedrockResponse> lookupIdentityBedrock(final UUID uuid) {
        try {
            var response = mcProfileRestClient.getBedrockProfile(uuid);
            return Optional.of(response);
        } catch (final RestClientResponseException e) {
            logger.error("{} from MCProfile: {}", e.getStatusCode().value(), uuid);
        } catch (final RestClientException e) {
            logger.error("Bad status response from MCProfile: {}", uuid);
        } catch (final Exception e) {
            logger.error("MCProfile unexpected error: {}", uuid, e);
        }
        return Optional.empty();
    }

    public URL getAvatarURL(UUID uuid) {
        return getAvatarURL(uuid.toString().replace("-", ""));
    }

    public URL getAvatarURL(String playerName) {
        try {
            return URI.create(String.format("https://crafthead.net/helm/%s/64", playerName)).toURL();
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<UUID> getOrResolveUuid(final UUID uuid, final String username) {
        if (uuid != null) return Optional.of(uuid);
        if (username == null || invalidUsername(username)) return Optional.empty();
        return getPlayerIdentity(username.trim()).map(ProfileData::uuid);
    }

    public Optional<ProfileData> getOrResolvePlayerIdentity(final UUID uuid, final String username) {
        if (uuid != null) return getPlayerIdentity(uuid);
        if (username == null || invalidUsername(username)) return Optional.empty();
        return getPlayerIdentity(username.trim());
    }

    public boolean isBedrockUsername(final String username) {
        return bedrockUsernamePattern.matcher(username).matches();
    }

    public boolean invalidUsername(final String username) {
        return !validUsernamePattern.matcher(username).matches() && !bedrockUsernamePattern.matcher(username).matches();
    }

    public boolean isBedrockUUID(UUID uuid) {
        if (uuid.getMostSignificantBits() != 0L) return false;
        if (uuid.getLeastSignificantBits() == 0L) return false;
        return Long.compareUnsigned(uuid.getLeastSignificantBits(), 0x0009000000000000L) > 0;
    }
}
