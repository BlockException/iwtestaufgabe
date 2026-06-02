package net.iwmedia.report.common.service;

import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import net.iwmedia.report.api.dto.ReportDto;
import net.iwmedia.report.api.model.ReportStatus;
import net.iwmedia.report.api.service.ReportRepository;
import net.iwmedia.report.common.model.ReportModel;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReportRepositoryImpl implements ReportRepository {
    private final MongoService mongoService;

    public ReportRepositoryImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public void saveReport(ReportDto reportDto) {
        Datastore datastore = mongoService.getDatastore();
        ReportModel model = toModel(reportDto);
        datastore.save(model);
    }

    @Override
    public long countOpenReports() {
        return mongoService.getDatastore()
                .find(ReportModel.class)
                .filter("status", ReportStatus.OPEN)
                .count();
    }

    @Override
    public Optional<ReportDto> findById(String reportId) {
        if (reportId == null || reportId.isBlank()) {
            return Optional.empty();
        }
        try {
            ObjectId objectId = new ObjectId(reportId);
            ReportModel model = mongoService.getDatastore()
                    .find(ReportModel.class)
                    .filter("_id", objectId)
                    .first();
            return Optional.ofNullable(model).map(this::toDto);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<ReportDto> findOpenReports(int limit, int offset) {
        FindOptions options = new FindOptions();
        if (offset > 0) {
            options.skip(offset);
        }
        if (limit > 0) {
            options.limit(limit);
        }
        return mongoService.getDatastore()
                .find(ReportModel.class)
                .filter("status", ReportStatus.OPEN)
                .iterator(options)
                .toList()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void updateReportStatus(String reportId, ReportStatus status, UUID handledBy, String handledByName, String modNote) {
        findById(reportId).ifPresent(reportDto -> {
            ReportModel model = toModel(reportDto);
            model.setStatus(status);
            model.setHandledBy(handledBy);
            model.setHandledByName(handledByName);
            model.setModNote(modNote);
            model.setHandledAt(Instant.now());
            mongoService.getDatastore().save(model);
        });
    }

    private ReportModel toModel(ReportDto dto) {
        ReportModel model = new ReportModel();
        if (dto.id() != null && !dto.id().isBlank()) {
            model.setId(new ObjectId(dto.id()));
        }
        model.setReporter(dto.reporter());
        model.setReported(dto.reported());
        model.setReporterName(dto.reporterName());
        model.setReportedName(dto.reportedName());
        model.setReason(dto.reason());
        model.setStatus(dto.status());
        model.setHandledBy(dto.handledBy());
        model.setHandledByName(dto.handledByName());
        model.setModNote(dto.modNote());
        model.setServer(dto.server());
        model.setCreatedAt(dto.createdAt());
        model.setHandledAt(dto.handledAt());
        return model;
    }

    private ReportDto toDto(ReportModel model) {
        return new ReportDto(
                model.getId() != null ? model.getId().toHexString() : null,
                model.getReporter(),
                model.getReported(),
                model.getReporterName(),
                model.getReportedName(),
                model.getReason(),
                model.getStatus(),
                model.getHandledBy(),
                model.getHandledByName(),
                model.getModNote(),
                model.getServer(),
                model.getCreatedAt(),
                model.getHandledAt()
        );
    }
}
