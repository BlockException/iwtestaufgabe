package net.iwmedia.report.common.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.iwmedia.report.api.service.UuidLookupService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultUuidLookupService implements UuidLookupService {
    private static final Pattern UUID_PATTERN = Pattern.compile("\\\"id\\\":\\\"([0-9a-fA-F]{32})\\\"");
    private final HttpClient httpClient;
    private final Cache<String, UUID> cache;

    public DefaultUuidLookupService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(6, TimeUnit.HOURS)
                .maximumSize(2000)
                .build();
    }

    @Override
    public CompletableFuture<Optional<UUID>> lookupUuid(String playerName, boolean bedrockCandidate) {
        UUID cached = cache.getIfPresent(playerName.toLowerCase(Locale.ROOT));
        if (cached != null) {
            return CompletableFuture.completedFuture(Optional.of(cached));
        }

        String endpoint = bedrockCandidate ? "https://mc-api.io/api/v2/uuid/bedrock/%s" : "https://mc-api.io/api/v2/uuid/java/%s";
        String uri = String.format(endpoint, playerName);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .timeout(Duration.ofSeconds(7))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        return Optional.<UUID>empty();
                    }
                    return parseUuid(response.body()).map(uuid -> {
                        cache.put(playerName.toLowerCase(Locale.ROOT), uuid);
                        return uuid;
                    });
                })
                .exceptionally(ex -> Optional.empty());
    }

    private Optional<UUID> parseUuid(String body) {
        Matcher matcher = UUID_PATTERN.matcher(body);
        if (matcher.find()) {
            String raw = matcher.group(1);
            return Optional.of(UUID.fromString(raw.replaceFirst("(.{8})(.{4})(.{4})(.{4})(.+)", "$1-$2-$3-$4-$5")));
        }
        return Optional.empty();
    }
}
