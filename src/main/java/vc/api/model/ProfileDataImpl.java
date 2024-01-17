package vc.api.model;

import java.util.UUID;

public record ProfileDataImpl(String name, UUID uuid) implements ProfileData { }
