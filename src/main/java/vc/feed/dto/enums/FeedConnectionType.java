package vc.feed.dto.enums;


public enum FeedConnectionType {

    JOIN("JOIN"),

    LEAVE("LEAVE");

    private final String literal;

    FeedConnectionType(String literal) {
        this.literal = literal;
    }
}
