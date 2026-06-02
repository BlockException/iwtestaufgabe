package net.iwmedia.report.api.model;

public enum ReportTemplate {
    CHEATING("CHEATING", "Verdacht auf Cheats/Hacks"),
    INSULT("INSULT", "Beleidigung/Toxisches Verhalten"),
    BUGUSING("BUGUSING", "Ausnutzen von Bugs"),
    GRIEFING("GRIEFING", "Zerstörung von Bauwerken"),
    SPAM("SPAM", "Chat-Spam/Werbung"),
    OTHER("OTHER", "Sonstiges");

    private final String key;
    private final String description;

    ReportTemplate(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public static ReportTemplate fromKey(String key) {
        for (ReportTemplate template : values()) {
            if (template.key.equalsIgnoreCase(key)) {
                return template;
            }
        }
        return OTHER;
    }
}
