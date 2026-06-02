package net.iwmedia.report.bungee.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.iwmedia.report.bungee.ReportBungeePlugin;
import net.iwmedia.report.bungee.listener.LoginReminderListener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeModule extends AbstractModule {
    private final ReportBungeePlugin plugin;

    public BungeeModule(ReportBungeePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(LoginReminderListener.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    public Plugin providePlugin() {
        return plugin;
    }
}
