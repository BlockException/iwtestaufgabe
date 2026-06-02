package net.iwmedia.report.common.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import net.iwmedia.report.api.model.ReportStatus;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.UUID;

@Indexes({
        @Index(fields = @dev.morphia.annotations.Field("status")),
        @Index(fields = @dev.morphia.annotations.Field("reported")),
        @Index(fields = @dev.morphia.annotations.Field("reporter")),
        @Index(fields = @dev.morphia.annotations.Field("createdAt"))
})
@Entity("reports")
public class ReportModel {
    @Id
    private ObjectId id;
    private UUID reporter;
    private UUID reported;
    private String reporterName;
    private String reportedName;
    private String reason;
    private ReportStatus status;
    private UUID handledBy;
    private String handledByName;
    private String modNote;
    private String server;
    private Instant createdAt;
    private Instant handledAt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public UUID getReporter() {
        return reporter;
    }

    public void setReporter(UUID reporter) {
        this.reporter = reporter;
    }

    public UUID getReported() {
        return reported;
    }

    public void setReported(UUID reported) {
        this.reported = reported;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReportedName() {
        return reportedName;
    }

    public void setReportedName(String reportedName) {
        this.reportedName = reportedName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public UUID getHandledBy() {
        return handledBy;
    }

    public void setHandledBy(UUID handledBy) {
        this.handledBy = handledBy;
    }

    public String getHandledByName() {
        return handledByName;
    }

    public void setHandledByName(String handledByName) {
        this.handledByName = handledByName;
    }

    public String getModNote() {
        return modNote;
    }

    public void setModNote(String modNote) {
        this.modNote = modNote;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getHandledAt() {
        return handledAt;
    }

    public void setHandledAt(Instant handledAt) {
        this.handledAt = handledAt;
    }
}
