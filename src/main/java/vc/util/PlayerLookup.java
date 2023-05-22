package vc.util;

import vc.swagger.minetools_api.handler.UuidApi;
import vc.swagger.minetools_api.model.UUIDAndUsername;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

public class PlayerLookup {
    private final UuidApi uuidApi = new UuidApi();

    public record PlayerIdentity(UUID uuid, String username) {
    }

    public Optional<PlayerIdentity> getPlayerProfile(final String username) {
        final UUIDAndUsername uuidAndUsername = uuidApi.getUUIDAndUsername(username);
        if (uuidAndUsername == null) return Optional.empty();
        if (uuidAndUsername.getStatus() != UUIDAndUsername.StatusEnum.OK) return Optional.empty();
        return Optional.of(new PlayerIdentity(UUID.fromString(uuidAndUsername
                .getId()
                .replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")
        ), uuidAndUsername.getName()));
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
        return getPlayerProfile(username).map(PlayerIdentity::uuid);
    }
}
