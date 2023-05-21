package vc.util;

import vc.swagger.mojang_api.handler.ProfilesApi;
import vc.swagger.mojang_api.model.ProfileLookup;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerLookup {
    private final ProfilesApi profilesApi = new ProfilesApi();

    public Optional<ProfileLookup> getPlayerProfile(final String username) {
        List<ProfileLookup> profileUuid = profilesApi.getProfileUuid(List.of(username));
        return profileUuid.stream().findFirst();
    }

    public UUID getProfileUUID(final ProfileLookup profile) {
        return UUID.fromString(profile
                .getId()
                .replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
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
        return getPlayerProfile(username).map(this::getProfileUUID);
    }
}
