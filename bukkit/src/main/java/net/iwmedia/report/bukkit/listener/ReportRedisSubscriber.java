package net.iwmedia.report.bukkit.listener;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.iwmedia.report.common.service.RedisService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;

public class ReportRedisSubscriber {
    private static final String NEW_CHANNEL = "reports:new";
    private static final String STATUS_CHANNEL = "reports:status_update";
    private final Plugin plugin;
    private final RedisService redisService;
    private final Gson gson = new Gson();

    @Inject
    public ReportRedisSubscriber(Plugin plugin, RedisService redisService) {
        this.plugin = plugin;
        this.redisService = redisService;
    }

    public void start() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = redisService.getPool().getResource()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        if (NEW_CHANNEL.equals(channel)) {
                            handleNewReport(message);
                        } else if (STATUS_CHANNEL.equals(channel)) {
                            handleStatusUpdate(message);
                        }
                    }
                }, NEW_CHANNEL, STATUS_CHANNEL);
            }
        });
    }

    private void handleNewReport(String payload) {
        Map<?, ?> data = gson.fromJson(payload, Map.class);
        String reported = String.valueOf(data.get("reportedName"));
        String reporter = String.valueOf(data.get("reporterName"));
        String reason = String.valueOf(data.get("reason"));
        String message = String.format("[REPORT] %s wurde gemeldet von %s (Grund: %s)", reported, reporter, reason);
        Bukkit.getServer().getScheduler().runTask(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.hasPermission("report.admin")) {
                    player.sendMessage(message);
                    player.sendMessage("[REPORT] Verwende /reports, um offene Reports zu verwalten.");
                }
            }
        });
    }

    private void handleStatusUpdate(String payload) {
        Map<?, ?> data = gson.fromJson(payload, Map.class);
        String reporterUuid = String.valueOf(data.get("reporterUuid"));
        String reportedName = String.valueOf(data.get("reportedName"));
        String status = String.valueOf(data.get("status"));
        String modNote = String.valueOf(data.get("modNote"));
        String message;
        if ("RESOLVED".equalsIgnoreCase(status)) {
            message = String.format("[REPORT] Dein Report gegen %s wurde bearbeitet und als berechtigt eingestuft.", reportedName);
        } else {
            message = String.format("[REPORT] Dein Report gegen %s wurde geprüft und abgelehnt. Grund: %s", reportedName, modNote);
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            try {
                java.util.UUID uuid = java.util.UUID.fromString(reporterUuid);
                Player player = plugin.getServer().getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    player.sendMessage(message);
                }
            } catch (IllegalArgumentException ignored) {
            }
        });
    }
}
