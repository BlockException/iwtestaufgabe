package net.iwmedia.report.bukkit.listener;

import com.google.inject.Inject;
import net.iwmedia.report.api.model.ReportTemplate;
import net.iwmedia.report.bukkit.service.BukkitReportService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ReportInventoryListener implements Listener {
    private final BukkitReportService reportService;

    @Inject
    public ReportInventoryListener(BukkitReportService reportService) {
        this.reportService = reportService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String title = event.getView().getTitle();
        if (title == null) {
            return;
        }
        if (reportService.isPlayerSelectionMenu(title)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            if (displayName == null) {
                return;
            }
            reportService.onPlayerSelected(player, displayName.replace("§a", ""));
            player.closeInventory();
        } else if (reportService.isTemplateSelectionMenu(title)) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
                return;
            }
            String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
            if (displayName == null) {
                return;
            }
            reportService.onTemplateSelected(player, ReportTemplate.fromKey(displayName.replace("§e", "")));
            player.closeInventory();
        } else if (reportService.isReportMenu(title)) {
            event.setCancelled(true);
            // Future expansion: inspect report details if clicked.
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (reportService.handleChatReport(player, event.getMessage())) {
            event.setCancelled(true);
        }
    }
}
