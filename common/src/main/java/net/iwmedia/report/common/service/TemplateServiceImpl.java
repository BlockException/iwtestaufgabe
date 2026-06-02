package net.iwmedia.report.common.service;

import dev.morphia.Datastore;
import net.iwmedia.report.api.model.ReportTemplate;
import net.iwmedia.report.common.model.ReportTemplateModel;

import java.util.ArrayList;
import java.util.List;

public class TemplateServiceImpl implements TemplateService {
    private final MongoService mongoService;

    public TemplateServiceImpl(MongoService mongoService) {
        this.mongoService = mongoService;
    }

    @Override
    public List<ReportTemplate> loadTemplates() {
        Datastore datastore = mongoService.getDatastore();
        List<ReportTemplateModel> templates = datastore.find(ReportTemplateModel.class).iterator().toList();
        if (templates.isEmpty()) {
            ensureDefaultTemplates();
            templates = datastore.find(ReportTemplateModel.class).iterator().toList();
        }
        List<ReportTemplate> result = new ArrayList<>();
        for (ReportTemplateModel model : templates) {
            result.add(ReportTemplate.fromKey(model.getKey()));
        }
        return result;
    }

    @Override
    public void ensureDefaultTemplates() {
        Datastore datastore = mongoService.getDatastore();
        for (ReportTemplate template : ReportTemplate.values()) {
            if (datastore.find(ReportTemplateModel.class).filter("key", template.getKey()).first() == null) {
                ReportTemplateModel model = new ReportTemplateModel();
                model.setKey(template.getKey());
                model.setName(template.name());
                model.setDescription(template.getDescription());
                datastore.save(model);
            }
        }
    }
}
