package net.iwmedia.report.bukkit.command;

import com.google.inject.Inject;
import net.iwmedia.report.bukkit.service.BukkitReportService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand implements CommandExecutor {
    private final BukkitReportService reportService;

    @Inject
    public ReportCommand(BukkitReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cNur Spieler können Reports einreichen.");
            return true;
        }

        if (args.length == 0) {
            reportService.openReportSelection(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("resolve")) {
            if (!player.hasPermission("report.admin")) {
                player.sendMessage("§cKeine Berechtigung.");
                return true;
            }
            if (args.length < 2) {
                player.sendMessage("§cVerwendung: /report resolve <reportId>");
                return true;
            }
            reportService.resolveReport(player, args[1]);
            return true;
        }

        if (args[0].equalsIgnoreCase("reject")) {
            if (!player.hasPermission("report.admin")) {
                player.sendMessage("§cKeine Berechtigung.");
                return true;
            }
            if (args.length < 3) {
                player.sendMessage("§cVerwendung: /report reject <reportId> <grund>");
                return true;
            }
            String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
            reportService.rejectReport(player, args[1], reason);
            return true;
        }

        reportService.startReportFlow(player, args[0]);
        return true;
    }
}
