package vc.api.model;

import java.util.UUID;

public record MojangProfileResponse(String name, String id) implements ProfileData {
    public UUID uuid() {
        return UUID.fromString(id.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
