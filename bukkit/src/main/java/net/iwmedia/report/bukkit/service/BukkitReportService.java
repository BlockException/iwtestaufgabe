package net.iwmedia.report.bukkit.service;

import net.iwmedia.report.api.dto.ReportDto;
import net.iwmedia.report.api.model.ReportStatus;
import net.iwmedia.report.api.model.ReportTemplate;
import net.iwmedia.report.api.service.ReportPublisher;
import net.iwmedia.report.api.service.ReportRepository;
import net.iwmedia.report.api.service.UuidLookupService;
import net.iwmedia.report.common.service.NotificationService;
import net.iwmedia.report.common.service.TemplateService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitReportService {
    private static final String PLAYER_MENU_TITLE = "Report: Spieler wählen";
    private static final String TEMPLATE_MENU_TITLE = "Report Grunde für %s";
    private static final String REPORTS_MENU_TITLE = "Reports: Offen";

    private final Plugin plugin;
    private final ReportRepository repository;
    private final ReportPublisher publisher;
    private final UuidLookupService lookupService;
    private final TemplateService templateService;
    private final NotificationService notificationService;
    private final Map<UUID, PendingTarget> pendingTargets = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> pendingTemplateTarget = new ConcurrentHashMap<>();

    public BukkitReportService(Plugin plugin,
                               ReportRepository repository,
                               ReportPublisher publisher,
                               UuidLookupService lookupService,
                               TemplateService templateService,
                               NotificationService notificationService) {
        this.plugin = plugin;
        this.repository = repository;
        this.publisher = publisher;
        this.lookupService = lookupService;
        this.templateService = templateService;
        this.notificationService = notificationService;
    }

    public void openReportSelection(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, PLAYER_MENU_TITLE);
        int slot = 0;
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.equals(player)) {
                continue;
            }
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = skull.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a" + online.getName());
                meta.setLore(List.of("§7Klicke um zu melden", "§7Online auf: " + online.getServer().getName()));
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                skull.setItemMeta(meta);
            }
            inventory.setItem(slot++, skull);
            if (slot >= inventory.getSize()) {
                break;
            }
        }
        player.openInventory(inventory);
    }

    public void startReportFlow(Player reporter, String targetName) {
        if (targetName.equalsIgnoreCase(reporter.getName())) {
            reporter.sendMessage("§cDu kannst dich nicht selbst melden.");
            return;
        }
        String lookupName = targetName.startsWith(".") ? targetName.substring(1) : targetName;
        boolean bedrockCandidate = targetName.startsWith(".");
        lookupService.lookupUuid(lookupName, bedrockCandidate).thenAccept(optionalUuid -> {
            if (optionalUuid.isEmpty()) {
                reporter.sendMessage("§cSpielername konnte nicht aufgelöst werden.");
                return;
            }
            UUID reportedUuid = optionalUuid.get();
            plugin.getServer().getScheduler().runTask(plugin, () -> openTemplateSelection(reporter, reportedUuid, lookupName));
        });
    }

    public void openTemplateSelection(Player player, UUID targetUuid, String targetName) {
        pendingTargets.put(player.getUniqueId(), new PendingTarget(targetUuid, targetName));
        Inventory inventory = Bukkit.createInventory(null, 27, String.format(TEMPLATE_MENU_TITLE, targetName));
        int slot = 0;
        for (ReportTemplate template : templateService.loadTemplates()) {
            ItemStack item = switch (template) {
                case OTHER -> new ItemStack(Material.WRITABLE_BOOK);
                case CHEATING -> new ItemStack(Material.IRON_SWORD);
                case INSULT -> new ItemStack(Material.NAME_TAG);
                case BUGUSING -> new ItemStack(Material.ENDER_PEARL);
                case GRIEFING -> new ItemStack(Material.SHEARS);
                case SPAM -> new ItemStack(Material.PAPER);
            };
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e" + template.name());
                meta.setLore(List.of("§7" + template.getDescription()));
                item.setItemMeta(meta);
            }
            inventory.setItem(slot++, item);
            if (slot >= inventory.getSize()) {
                break;
            }
        }
        player.openInventory(inventory);
    }

    public void submitReport(Player reporter, ReportTemplate template, String freeText) {
        PendingTarget pendingTarget = pendingTargets.remove(reporter.getUniqueId());
        if (pendingTarget == null) {
            reporter.sendMessage("§cKein aktiver Report vorhanden.");
            return;
        }
        String reason = template == ReportTemplate.OTHER ? freeText : template.name();
        if (template == ReportTemplate.OTHER && (freeText == null || freeText.isBlank())) {
            reporter.sendMessage("§cFür 'OTHER' muss eine Beschreibung angegeben werden.");
            return;
        }
        String reportId = UUID.randomUUID().toString().replace("-", "");
        ReportDto reportDto = new ReportDto(
                reportId,
                reporter.getUniqueId(),
                pendingTarget.targetUuid(),
                reporter.getName(),
                pendingTarget.targetName(),
                reason,
                ReportStatus.OPEN,
                null,
                null,
                null,
                reporter.getServer().getName(),
                Instant.now(),
                null
        );
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            repository.saveReport(reportDto);
            publisher.publishNewReport(reportDto);
        });
        reporter.sendMessage("§aDein Report wurde aufgenommen.");
    }

    public void resolveReport(Player moderator, String reportId) {
        repository.findById(reportId).ifPresentOrElse(report -> {
            repository.updateReportStatus(reportId, ReportStatus.RESOLVED, moderator.getUniqueId(), moderator.getName(), null);
            String message = String.format("[REPORT] Dein Report gegen %s wurde bearbeitet und als berechtigt eingestuft.", report.reportedName());
            notificationService.savePendingNotification(report.reporter(), message);
            publisher.publishStatusUpdate(reportId, report.reporter().toString(), report.reporterName(), report.reportedName(), ReportStatus.RESOLVED.name(), moderator.getName(), null);
            moderator.sendMessage("§aReport wurde als berechtigt markiert.");
        }, () -> moderator.sendMessage("§cReport nicht gefunden."));
    }

    public void rejectReport(Player moderator, String reportId, String reason) {
        repository.findById(reportId).ifPresentOrElse(report -> {
            repository.updateReportStatus(reportId, ReportStatus.REJECTED, moderator.getUniqueId(), moderator.getName(), reason);
            String message = String.format("[REPORT] Dein Report gegen %s wurde geprüft und abgelehnt. Grund: %s", report.reportedName(), reason);
            notificationService.savePendingNotification(report.reporter(), message);
            publisher.publishStatusUpdate(reportId, report.reporter().toString(), report.reporterName(), report.reportedName(), ReportStatus.REJECTED.name(), moderator.getName(), reason);
            moderator.sendMessage("§aReport wurde abgelehnt.");
        }, () -> moderator.sendMessage("§cReport nicht gefunden."));
    }

    public void openReportManagement(Player player) {
        List<ReportDto> reports = repository.findOpenReports(45, 0);
        Inventory inventory = Bukkit.createInventory(null, 54, REPORTS_MENU_TITLE);
        int slot = 0;
        for (ReportDto report : reports) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e#" + report.id() + " - " + report.reportedName());
                List<String> lore = new ArrayList<>();
                lore.add("§7Reporter: " + report.reporterName());
                lore.add("§7Grund: " + report.reason());
                lore.add("§7Server: " + report.server());
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(slot++, item);
            if (slot >= inventory.getSize()) {
                break;
            }
        }
        player.openInventory(inventory);
    }

    public boolean isReportMenu(String title) {
        return title != null && REPORTS_MENU_TITLE.equals(title);
    }

    public boolean isPlayerSelectionMenu(String title) {
        return title != null && PLAYER_MENU_TITLE.equals(title);
    }

    public boolean isTemplateSelectionMenu(String title) {
        return title != null && title.startsWith("Report Grunde für ");
    }

    public void onPlayerSelected(Player player, String targetName) {
        lookupService.lookupUuid(targetName, targetName.startsWith(".")).thenAccept(optionalUuid -> {
            optionalUuid.ifPresent(uuid -> plugin.getServer().getScheduler().runTask(plugin, () -> openTemplateSelection(player, uuid, targetName)));
        });
    }

    public void onTemplateSelected(Player player, ReportTemplate template) {
        PendingTarget pendingTarget = pendingTargets.get(player.getUniqueId());
        if (pendingTarget == null) {
            player.sendMessage("§cKein Ziel zum Melden gefunden.");
            return;
        }
        if (template == ReportTemplate.OTHER) {
            player.closeInventory();
            player.sendMessage("§eBitte gib nun den Grund für den Report im Chat ein.");
            pendingTemplateTarget.put(player.getUniqueId(), pendingTarget.targetUuid());
            return;
        }
        submitReport(player, template, template.getDescription());
    }

    public boolean handleChatReport(Player player, String message) {
        if (!pendingTemplateTarget.containsKey(player.getUniqueId())) {
            return false;
        }
        pendingTemplateTarget.remove(player.getUniqueId());
        submitReport(player, ReportTemplate.OTHER, message);
        return true;
    }

    private record PendingTarget(UUID targetUuid, String targetName) {
    }
}
