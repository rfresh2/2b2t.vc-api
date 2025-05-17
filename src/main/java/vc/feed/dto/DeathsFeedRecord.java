package vc.feed.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeathsFeedRecord(
    OffsetDateTime time,
    String deathMessage,
    String victimPlayerName,
    UUID victimPlayerUuid,
    String killerPlayerName,
    UUID killerPlayerUuid
) {
}
