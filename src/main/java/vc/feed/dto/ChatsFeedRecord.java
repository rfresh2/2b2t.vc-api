package vc.feed.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ChatsFeedRecord(OffsetDateTime time, String chat, String playerName, UUID playerUuid) { }
