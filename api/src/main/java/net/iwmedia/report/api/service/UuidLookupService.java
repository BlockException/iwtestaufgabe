package net.iwmedia.report.api.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UuidLookupService {
    CompletableFuture<Optional<UUID>> lookupUuid(String playerName, boolean bedrockCandidate);
}
