package vc.feed.dto;

import vc.feed.dto.enums.FeedConnectionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConnectionsFeedRecord(OffsetDateTime time, FeedConnectionType connection, String playerName, UUID playerUuid) { }
