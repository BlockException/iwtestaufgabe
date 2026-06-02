package net.iwmedia.report.common.service;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.iwmedia.report.api.service.ReportPublisher;
import net.iwmedia.report.api.service.ReportRepository;
import net.iwmedia.report.api.service.UuidLookupService;
import net.iwmedia.report.common.service.NotificationService;
import net.iwmedia.report.common.service.TemplateService;

public class CommonModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ReportRepository.class).to(ReportRepositoryImpl.class).asEagerSingleton();
        bind(UuidLookupService.class).to(DefaultUuidLookupService.class).asEagerSingleton();
        bind(ReportPublisher.class).to(RedisReportPublisher.class).asEagerSingleton();
        bind(TemplateService.class).to(TemplateServiceImpl.class).asEagerSingleton();
        bind(NotificationService.class).to(NotificationServiceImpl.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public MongoService provideMongoService() {
        return new MongoServiceImpl("mongodb://localhost:27017", "reportdb");
    }

    @Provides
    @Singleton
    public RedisService provideRedisService() {
        return new RedisServiceImpl("localhost", 6379);
    }
}
