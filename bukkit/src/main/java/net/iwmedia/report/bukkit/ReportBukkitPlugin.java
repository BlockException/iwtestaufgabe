package net.iwmedia.report.bukkit;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.iwmedia.report.bukkit.command.ReportCommand;
import net.iwmedia.report.bukkit.command.ReportsCommand;
import net.iwmedia.report.bukkit.guice.BukkitModule;
import net.iwmedia.report.bukkit.listener.ReportInventoryListener;
import net.iwmedia.report.bukkit.listener.ReportPlayerJoinListener;
import net.iwmedia.report.bukkit.listener.ReportRedisSubscriber;
import net.iwmedia.report.common.service.CommonModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReportBukkitPlugin extends JavaPlugin {
    private Injector injector;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        injector = Guice.createInjector(new CommonModule(), new BukkitModule(this));
        getCommand("report").setExecutor(injector.getInstance(ReportCommand.class));
        getCommand("reports").setExecutor(injector.getInstance(ReportsCommand.class));
        getServer().getPluginManager().registerEvents(injector.getInstance(ReportInventoryListener.class), this);
        getServer().getPluginManager().registerEvents(injector.getInstance(ReportPlayerJoinListener.class), this);
        injector.getInstance(ReportRedisSubscriber.class).start();
    }

    @Override
    public void onDisable() {
        // No-op, Guice-managed services will be garbage collected.
    }
}
