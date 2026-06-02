package net.iwmedia.report.api.service;

import net.iwmedia.report.api.dto.ReportDto;
import net.iwmedia.report.api.model.ReportStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {
    void saveReport(ReportDto reportDto);
    long countOpenReports();
    Optional<ReportDto> findById(String reportId);
    List<ReportDto> findOpenReports(int limit, int offset);
    void updateReportStatus(String reportId, ReportStatus status, UUID handledBy, String handledByName, String modNote);
}
