package net.iwmedia.report.bukkit.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.iwmedia.report.api.service.ReportPublisher;
import net.iwmedia.report.api.service.ReportRepository;
import net.iwmedia.report.api.service.UuidLookupService;
import net.iwmedia.report.bukkit.ReportBukkitPlugin;
import net.iwmedia.report.bukkit.command.ReportCommand;
import net.iwmedia.report.bukkit.command.ReportsCommand;
import net.iwmedia.report.bukkit.listener.ReportInventoryListener;
import net.iwmedia.report.bukkit.listener.ReportPlayerJoinListener;
import net.iwmedia.report.bukkit.listener.ReportRedisSubscriber;
import net.iwmedia.report.bukkit.service.BukkitReportService;
import net.iwmedia.report.common.service.NotificationService;
import net.iwmedia.report.common.service.TemplateService;
import org.bukkit.plugin.Plugin;

public class BukkitModule extends AbstractModule {
    private final ReportBukkitPlugin plugin;

    public BukkitModule(ReportBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(ReportCommand.class);
        bind(ReportsCommand.class);
        bind(BukkitReportService.class).asEagerSingleton();
        bind(ReportInventoryListener.class).asEagerSingleton();
        bind(ReportPlayerJoinListener.class).asEagerSingleton();
        bind(ReportRedisSubscriber.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public Plugin providePlugin() {
        return plugin;
    }

    @Provides
    @Singleton
    public BukkitReportService provideReportService(
            ReportRepository repository,
            ReportPublisher publisher,
            UuidLookupService lookupService,
            TemplateService templateService,
            NotificationService notificationService) {
        return new BukkitReportService(plugin, repository, publisher, lookupService, templateService, notificationService);
    }
}
