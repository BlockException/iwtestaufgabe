# Minecraft Report System – Anleitung

Dieses Projekt stellt ein vollständig funktionsfähiges, serverübergreifendes Report-System für Minecraft-Netzwerke bereit. Mit Paper, BungeeCord, MongoDB und Redis kannst du Reports verwalten, speichern und über Systeme hinweg synchronisieren.

## 📦 Projektstruktur

Das Projekt folgt einer modernen Maven-Multi-Modul-Architektur:

- **`api`** – Zentrale Schnittstellen, DTOs und Enums für alle Module
- **`common`** – Services für MongoDB-Persistierung, Redis Pub/Sub und asynchrone UUID-Auflösung
- **`bukkit`** – Paper-Server-Plugin mit Report-Interface und Report-Erstellungs-Workflow
- **`bungee`** – BungeeCord-Proxy-Plugin für Admin-Benachrichtigungen bei Login

## 🚀 Schnellstart

### Voraussetzungen
- Java 21
- Maven 3.9+
- Docker & Docker Compose (für MongoDB und Redis)

### Schritt 1: Infrastruktur starten

Starten Sie MongoDB und Redis lokal:

```bash
docker compose up -d
```

### Schritt 2: Projekt bauen

```bash
mvn clean install
```

Die gebauten JAR-Dateien finden Sie hier:
- **Bukkit-Plugin**: `bukkit/target/report-bukkit-1.0.0-SNAPSHOT.jar`
- **Bungee-Plugin**: `bungee/target/report-bungee-1.0.0-SNAPSHOT.jar`

### Schritt 3: Plugins installieren

Kopieren Sie die JAR-Dateien in die `plugins`-Ordner Ihrer Minecraft-Servern:

```bash
cp bukkit/target/report-bukkit-1.0.0-SNAPSHOT.jar /path/to/paper/plugins/
cp bungee/target/report-bungee-1.0.0-SNAPSHOT.jar /path/to/bungee/plugins/
```

Starten Sie die Server neu.

## ⚙️ Konfiguration

Die Verbindungsdaten zu MongoDB und Redis sind vorkonfiguriert für lokale Instanzen (`localhost`):

- **MongoDB**: `localhost:27017` (Datenbank: `reports`)
- **Redis**: `localhost:6379`

Falls nötig, können diese in `common/src/main/java/net/iwmedia/report/common/service/CommonModule.java` angepasst werden.

Plugin-Metadaten (Name, Version, Author):
- `bukkit/src/main/resources/plugin.yml`
- `bungee/src/main/resources/plugin.yml`

## ✨ Features

- **Asynchrone UUID-Auflösung** über `mc-api.io` – keine Blockade des Spieler-Login
- **Report-Vorlagen** – vordefinierte Report-Gründe mit optionalem Freitext
- **Persistente Speicherung** in MongoDB – alle Reports bleiben erhalten
- **Echtzeit-Synchronisation** via Redis Pub/Sub – Reports sind sofort über alle Server sichtbar
- **Admin-Benachrichtigungen** beim Proxy-Login – aktuelle Übersicht offener Reports
- **Offline-Zustellung** – Benachrichtigungen für offline Admins werden gespeichert

## 📋 Plugin-Details

| Parameter | Wert |
|-----------|------|
| Plugin-Name | `IWTestAufgabe` |
| Autor | `blockexception_` |
| Version | `1.0.0-SNAPSHOT` |

## 🔧 Troubleshooting

**MongoDB/Redis nicht erreichbar?**
- Überprüfen Sie, dass die Container laufen: `docker ps`
- Falls nicht: `docker compose up -d`

**Build fehlgeschlagen?**
- Löschen Sie den Maven-Cache: `rm -rf ~/.m2/repository` (Linux/Mac) oder `rmdir %USERPROFILE%\.m2\repository` (Windows)
- Versuchen Sie erneut: `mvn clean install`

**Plugins laden nicht?**
- Überprüfen Sie die Server-Logs auf Fehler
- Stellen Sie sicher, dass die JAR-Dateien im richtigen `plugins`-Ordner sind

---

**Support & Kontakt**: Weitere Fragen? Siehe die detaillierte Entwickler-Dokumentation in den Quellcode-Kommentaren.
