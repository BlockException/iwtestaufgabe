package net.iwmedia.report.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public final class ReportBungeePlugin extends Plugin {
    @Override
    public void onEnable() {
        getLogger().info("IWTestAufgabe Plugin aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("IWTestAufgabe Plugin deaktiviert.");
    }
}
