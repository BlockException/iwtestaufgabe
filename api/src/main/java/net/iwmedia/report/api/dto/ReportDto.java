package net.iwmedia.report.api.dto;

import net.iwmedia.report.api.model.ReportStatus;
import java.time.Instant;
import java.util.UUID;

public record ReportDto(
        String id,
        UUID reporter,
        UUID reported,
        String reporterName,
        String reportedName,
        String reason,
        ReportStatus status,
        UUID handledBy,
        String handledByName,
        String modNote,
        String server,
        Instant createdAt,
        Instant handledAt
) {
}
