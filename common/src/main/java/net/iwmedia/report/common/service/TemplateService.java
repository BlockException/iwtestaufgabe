package net.iwmedia.report.common.service;

import net.iwmedia.report.api.model.ReportTemplate;

import java.util.List;

public interface TemplateService {
    List<ReportTemplate> loadTemplates();
    void ensureDefaultTemplates();
}
