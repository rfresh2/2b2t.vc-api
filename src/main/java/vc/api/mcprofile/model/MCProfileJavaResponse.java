package vc.api.mcprofile.model;

import vc.api.model.ProfileData;

import java.util.UUID;

public record MCProfileJavaResponse(
    String username,
    UUID uuid,
    String skin,
    String cape,
    boolean linked
) implements ProfileData {
    @Override
    public String name() {
        return username();
    }
}
