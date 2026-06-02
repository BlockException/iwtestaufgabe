package net.iwmedia.report.bukkit.command;

import com.google.inject.Inject;
import net.iwmedia.report.bukkit.service.BukkitReportService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportsCommand implements CommandExecutor {
    private final BukkitReportService reportService;

    @Inject
    public ReportsCommand(BukkitReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können die Report-Verwaltung öffnen.");
            return true;
        }
        if (!player.hasPermission("report.admin")) {
            player.sendMessage("§cDu hast keine Berechtigung.");
            return true;
        }
        reportService.openReportManagement(player);
        return true;
    }
}
