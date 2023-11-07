package vc.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vc.swagger.minetools_api.handler.UuidApi;
import vc.swagger.minetools_api.model.UUIDAndPlayerName;
import vc.swagger.mojang_api.handler.ProfileApi;
import vc.swagger.mojang_api.model.UUIDAndUser;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerLookup {
    private static final Logger logger = LoggerFactory.getLogger(PlayerLookup.class);
    private final ProfileApi mojangApi;
    private final UuidApi mineToolsApi;
    private final Cache<String, PlayerIdentity> uuidCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(10))
        .maximumSize(250)
        .build();

    public PlayerLookup(final ProfileApi mojangApi, final UuidApi mineToolsApi) {
        this.mojangApi = mojangApi;
        this.mineToolsApi = mineToolsApi;
    }

    public record PlayerIdentity(UUID uuid, String playerName) { }

    public Optional<PlayerIdentity> getPlayerIdentity(final String playerName) {
        final PlayerIdentity identityFromCache = uuidCache.getIfPresent(playerName);
        if (identityFromCache != null)
            return Optional.of(identityFromCache);

        Optional<PlayerIdentity> playerIdentity = lookupIdentityMojang(playerName).or(() -> lookupIdentityMinetools(playerName));
        playerIdentity.ifPresent(identity -> uuidCache.put(playerName, identity));
        return playerIdentity;
    }

    private Optional<PlayerIdentity> lookupIdentityMojang(final String playerName) {
        try {
            UUIDAndUser uuidAndUsername = mojangApi.getProfileFromUsername(playerName);
            if (uuidAndUsername == null) return Optional.empty();
            final PlayerIdentity playerIdentity = new PlayerIdentity(
                UUID.fromString(uuidAndUsername
                                    .getId()
                                    .replaceFirst(
                                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                        "$1-$2-$3-$4-$5")
                ), uuidAndUsername.getName());
            return Optional.of(playerIdentity);
        } catch (final Exception e) {
            logger.error("Error while looking up player identity using Mojang API", e);
            return Optional.empty();
        }
    }

    private Optional<PlayerIdentity> lookupIdentityMinetools(final String playerName) {
        try {
            UUIDAndPlayerName uuidAndUsername = mineToolsApi.getUUIDAndPlayerName(playerName);
            if (uuidAndUsername == null) return Optional.empty();
            final PlayerIdentity playerIdentity = new PlayerIdentity(
                UUID.fromString(uuidAndUsername
                                    .getId()
                                    .replaceFirst(
                                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                                        "$1-$2-$3-$4-$5")
                ), uuidAndUsername.getName());
            uuidCache.put(playerName, playerIdentity);
            return Optional.of(playerIdentity);
        } catch (final Exception e) {
            logger.error("Error while looking up player identity using Minetools API", e);
            return Optional.empty();
        }
    }

    public URL getAvatarURL(UUID uuid) {
        return getAvatarURL(uuid.toString().replace("-", ""));
    }

    public URL getAvatarURL(String playerName) {
        try {
            return new URL(String.format("https://minotar.net/helm/%s/64", playerName));
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<UUID> getOrResolveUuid(final UUID uuid, final String username) {
        if (uuid != null) return Optional.of(uuid);
        return getPlayerIdentity(username.trim()).map(PlayerIdentity::uuid);
    }

    public Optional<PlayerIdentity> getOrResolvePlayerIdentity(final UUID uuid, final String username) {
        if (uuid != null) return Optional.of(new PlayerIdentity(uuid, username));
        return getPlayerIdentity(username.trim());
    }
}
