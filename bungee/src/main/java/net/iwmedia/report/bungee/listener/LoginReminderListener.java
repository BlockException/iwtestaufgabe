package net.iwmedia.report.bungee.listener;

import com.google.inject.Inject;
import net.iwmedia.report.api.service.ReportRepository;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

public class LoginReminderListener implements Listener {
    private final Plugin plugin;
    private final ReportRepository reportRepository;

    @Inject
    public LoginReminderListener(Plugin plugin, ReportRepository reportRepository) {
        this.plugin = plugin;
        this.reportRepository = reportRepository;
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        InitialHandler connection = event.getConnection();
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            long openReports = reportRepository.countOpenReports();
            connection.sendMessage(new TextComponent("[REPORT] Du hast aktuell " + openReports + " offene Reports. Nutze /reports."));
        });
    }
}
