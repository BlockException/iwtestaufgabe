package net.iwmedia.report.bukkit.listener;

import com.google.inject.Inject;
import net.iwmedia.report.common.service.NotificationService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ReportPlayerJoinListener implements Listener {
    private final Plugin plugin;
    private final NotificationService notificationService;

    @Inject
    public ReportPlayerJoinListener(Plugin plugin, NotificationService notificationService) {
        this.plugin = plugin;
        this.notificationService = notificationService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> messages = notificationService.fetchPendingNotifications(player.getUniqueId());
            if (messages.isEmpty()) {
                return;
            }
            notificationService.removeNotifications(player.getUniqueId());
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                for (String message : messages) {
                    player.sendMessage(message);
                }
            });
        });
    }
}
