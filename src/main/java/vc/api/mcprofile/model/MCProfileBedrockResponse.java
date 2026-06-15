package vc.api.mcprofile.model;

import vc.api.model.ProfileData;

import java.util.UUID;

public record MCProfileBedrockResponse(
    String gamertag,
    String xuid,
    UUID floodgateuid,
    String icon,
    String skin,
    boolean linked,
    String java_uuid,
    String java_name
) implements ProfileData {
    @Override
    public String name() {
        return ("." + gamertag).replace(" ", "_");
    }

    @Override
    public UUID uuid() {
        return floodgateuid;
    }
}
