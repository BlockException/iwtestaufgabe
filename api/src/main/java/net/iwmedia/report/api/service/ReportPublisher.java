package net.iwmedia.report.api.service;

import net.iwmedia.report.api.dto.ReportDto;

public interface ReportPublisher {
    void publishNewReport(ReportDto reportDto);
    void publishStatusUpdate(String reportId, String reporterUuid, String reporterName, String reportedName, String status, String handledBy, String modNote);
}
