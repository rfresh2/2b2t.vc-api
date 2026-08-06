package vc.api.mccompanion.model;

import vc.api.model.ProfileData;

import java.util.UUID;

public record MCCompanionJavaResponse(
    String username,
    UUID uuid,
    String skinUrl
) implements ProfileData {

    @Override
    public String name() {
        return username;
    }
}
