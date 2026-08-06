package vc.api.mccompanion.model;

import vc.api.model.ProfileData;

import java.util.UUID;

public record MCCompanionBedrockResponse (
    String gamertag,
    String xuid,
    UUID floodgateuid,
    String skinUrl
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
