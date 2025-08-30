package kz.ifihtich.acapture;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class MainZone {
    public static class Zone {
        private boolean dropEnabled = false;
        private int dropDelay = 0;
        public int dropMin = 0;
        public int dropMax = 0;
        private int dropTaskId = -1;

        private final BossBar bossBar = Bukkit.createBossBar(" ", BarColor.BLUE, BarStyle.SOLID);
        private final String name;
        private double visualProgress = 0.0;
        private final Location centre;
        private final int captureTime;
        private final double radius;

        private String capturingTeam = null;
        private String lastCaptureTeam = null;

        private final HologramManager hologramManager = new HologramManager();
        private List<String> hologramLines;
        private Set<Player> lastViewers = new HashSet<>();
        private int currentTicks = 0;
        private int totalTicks;

        public Zone(String name, Location centre, double radius, int captureTime) {
            this.name = Utils.color(name);
            this.centre = centre;
            this.captureTime = captureTime;
            this.radius = radius;
        }

        private void clearDroppedItemsInZone() {
            World world = centre.getWorld();
            if (world == null) return;

            double radiusSquared = radius * radius;

            for (Item item : world.getEntitiesByClass(Item.class)) {
                Location loc = item.getLocation();

                if (!loc.getWorld().equals(centre.getWorld())) continue;
                if (Math.abs(loc.getY() - centre.getY()) > 3) continue;

                double dx = loc.getX() - centre.getX();
                double dz = loc.getZ() - centre.getZ();
                if ((dx * dx + dz * dz) > radiusSquared) continue;

                if (item.hasMetadata("zone_owner_team")) {
                    item.remove();
                }
            }
        }

        public void setupDropFromConfig(ConfigurationSection dropSection) {
            if (dropSection == null) return;
            this.dropEnabled = dropSection.getBoolean("enable", false);

            if (!dropEnabled) return;

            String range = dropSection.getString("diapason", "0-0");
            String[] parts = range.split("-");
            if (parts.length == 2) {
                try {
                    dropMin = Integer.parseInt(parts[0]);
                    dropMax = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {}
            }

            this.dropDelay = dropSection.getInt("delay", 5) * 20;
        }
        private void startDrop() {
            if (centre.getWorld() == null) {
                Bukkit.getLogger().warning("❗ Мир для зоны " + name + " не найден!");
                return;
            }
            if (!dropEnabled || dropTaskId != -1 || lastCaptureTeam == null) return;

            dropTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(ACapture.getInstance(), () -> {

                List<Player> playersInZone = centre.getWorld().getPlayers().stream()
                        .filter(this::isInside)
                        .collect(Collectors.toList());

                if (playersInZone.isEmpty()) return;

                Set<String> teamsInZone = playersInZone.stream()
                        .map(p -> {
                            Team team = p.getScoreboard().getEntryTeam(p.getName());
                            return team != null ? team.getName() : null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                if (teamsInZone.size() != 1 || !teamsInZone.contains(lastCaptureTeam)) return;

                ItemStack dropItem = new ItemStack(Material.GOLD_INGOT);

                ItemMeta meta = dropItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(" ");

                    List<String> lore = new ArrayList<>();
                    lore.add("§7Время: " + System.currentTimeMillis());
                    meta.setLore(lore);

                    dropItem.setItemMeta(meta);
                }

                Random random = new Random();
                double angle = 2 * Math.PI * random.nextDouble();
                double distance = radius * Math.sqrt(random.nextDouble());

                double dx = distance * Math.cos(angle);
                double dz = distance * Math.sin(angle);

                Location location = centre.clone().add(dx, 0, dz);
                location.setY(centre.getWorld().getHighestBlockYAt(location) + 1.2);

                Item item = centre.getWorld().dropItem(location, dropItem);
                item.setVelocity(new Vector(0, 0, 0));

                int points = dropMin + new Random().nextInt(dropMax - dropMin + 1);
                item.setCustomName(Utils.color("&a+" + points + " оч."));
                item.setMetadata("zone_points", new FixedMetadataValue(ACapture.getInstance(), points));
                item.setMetadata("zone_owner_team", new FixedMetadataValue(ACapture.getInstance(), lastCaptureTeam));
                item.setCustomNameVisible(true);
                item.setPickupDelay(0);

                item.setMetadata("zone_owner_team", new FixedMetadataValue(ACapture.getInstance(), lastCaptureTeam));

            }, 0L, dropDelay);
        }
        private void stopDrop() {
            if (dropTaskId != -1) {
                Bukkit.getScheduler().cancelTask(dropTaskId);
                dropTaskId = -1;
            }
        }
        public void shutdown() {
            stopDrop();
            removeHologram();
            bossBar.removeAll();
            bossBar.setVisible(false);
            lastViewers.clear();
        }
        public void updateVisualProgress() {
            double targetProgress = totalTicks == 0 ? 0.0 : (double) currentTicks / totalTicks;

            double speed = 0.3;
            visualProgress += (targetProgress - visualProgress) * speed;

            if (currentTicks >= totalTicks || visualProgress >= 0.99) {
                visualProgress = 1.0;
            }

            bossBar.setProgress(Math.max(0.0, Math.min(visualProgress, 1.0)));
        }

        private void updateBossBarViewers(Set<Player> players) {
            for (Player prev : lastViewers) {
                if (!players.contains(prev)) {
                    bossBar.removePlayer(prev);
                }
            }

            for (Player now : players) {
                if (!lastViewers.contains(now)) {
                    bossBar.addPlayer(now);
                }
            }

            lastViewers = players;
        }

        public void showHologram(List<String> lines) {
            this.hologramLines = lines;
            hologramManager.createHologram(centre.clone().add(0, 1.8, 0), lines);
            updateHologram(lastCaptureTeam);
        }

        public void removeHologram() {
            hologramManager.remove();
        }

        public void updateHologram(String teamName) {
            if (hologramLines == null || hologramLines.isEmpty()) return;

            List<String> replaced = hologramLines.stream()
                    .map(line -> {
                        String replacedLine = line.replace("%team_name%", teamName != null ? teamName : "-");
                        return replacedLine.isEmpty() ? " " : replacedLine;
                    })
                    .collect(Collectors.toList());;

            hologramManager.updateLines(replaced);
        }

        public boolean isInside(Player player) {
            if (!player.getWorld().equals(centre.getWorld())) return false;

            double playerY = player.getLocation().getY();
            double minY = centre.getY();
            double maxY = centre.getY() + 2.0;

            if (playerY < minY || playerY > maxY) return false;

            double dx = player.getLocation().getX() - centre.getX();
            double dz = player.getLocation().getZ() - centre.getZ();
            double distanceSquared = dx * dx + dz * dz;

            return distanceSquared <= radius * radius;
        }
        public boolean isInside(Location location) {
            if (!location.getWorld().equals(centre.getWorld())) return false;

            double y = location.getY();
            double minY = centre.getY();
            double maxY = centre.getY() + 2.0;

            if (y < minY || y > maxY) return false;

            double dx = location.getX() - centre.getX();
            double dz = location.getZ() - centre.getZ();
            double distanceSquared = dx * dx + dz * dz;

            return distanceSquared <= radius * radius;
        }
        public void tickCapture(Set<Player> playersInZone){
            if (playersInZone.isEmpty()){
                if (lastCaptureTeam == null) {
                    resetProgress(true);
                } else {
                    resetProgress(false);
                }
                return;
            }
            Set<String> teamsInZone = playersInZone.stream()
                    .map(p -> {
                        Team team = p.getScoreboard().getEntryTeam(p.getName());
                        return team != null ? team.getName() : null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());

            if (teamsInZone.size() != 1){
                if (lastCaptureTeam == null) {
                    resetProgress(true);
                } else {
                    resetProgress(false);
                }
                return;
            }
            String team = teamsInZone.iterator().next();

            if (lastCaptureTeam != null && lastCaptureTeam.equals(team)){
                bossBar.setVisible(false);
                bossBar.removeAll();
                return;
            }

            if (!team.equals(capturingTeam)){
                capturingTeam = team;
                currentTicks = 1;
                totalTicks = captureTime * 20;
                visualProgress = 0.0;
                bossBar.setProgress(0.0);
                bossBar.setVisible(true);
            } else {
                currentTicks++;
            }
            bossBar.setTitle(Utils.color(ACapture.getInstance().getConfig().getString("Bossbar.name").replace("%point_name%", name)));
            bossBar.setColor(BarColor.BLUE);

            updateBossBarViewers(playersInZone);
            updateVisualProgress();

            if (currentTicks >= totalTicks) {
                clearDroppedItemsInZone();
                lastCaptureTeam = team;
                updateHologram(lastCaptureTeam);
                startDrop();
                resetProgress(false);
            }
        }
        private void resetProgress(boolean stopDropFlag) {
            if (stopDropFlag) stopDrop();
            capturingTeam = null;
            currentTicks = 0;
            totalTicks = 0;
            visualProgress = 0.0;
            bossBar.setVisible(false);
            bossBar.removeAll();
            lastViewers.clear();
        }
        public void resetCaptureStatus() {
            capturingTeam = null;
            lastCaptureTeam = null;
            currentTicks = 0;
            totalTicks = 0;
            updateHologram(null);
            bossBar.setVisible(false);
            bossBar.removeAll();
            lastViewers.clear();
        }
        public Location getCentre() {
            return centre;
        }

        public double getRadius() {
            return radius;
        }
    }
    private final List<Zone> zones = new ArrayList<>();

    public void addZone(Zone zone) {
        zones.add(zone);
    }
    public List<Zone> getZones() {
        return zones;
    }
    public Zone getZonePlayerIsIn(Player player) {
        for (Zone zone : zones) {
            if (zone.isInside(player)) return zone;
        }
        return null;
    }

    public void loadFromConfig(ACapture plugin) {
        ConfigurationSection pointsSection = plugin.getConfig().getConfigurationSection("Points");
        if (pointsSection == null) return;

        for (String key : pointsSection.getKeys(false)) {
            ConfigurationSection point = pointsSection.getConfigurationSection(key);
            if (point == null) continue;

            String worldName = point.getString("World");
            double x = point.getDouble("x");
            double y = point.getDouble("y");
            double z = point.getDouble("z");
            double radius = point.getDouble("radius");
            int captureTime = point.getInt("captureTime", 10);
            String name = point.getString("name");

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                Bukkit.getLogger().warning("Мир \"" + worldName + "\" не найден для точки " + key);
                continue;
            }

            Location center = new Location(world, x, y, z);
            Zone zone = new Zone(name, center, radius, captureTime);
            if (point.isConfigurationSection("drop")) {
                ConfigurationSection drop = point.getConfigurationSection("drop");
                zone.setupDropFromConfig(drop);
            }
            addZone(zone);
            List<String> hologramLines = point.getStringList("hologram");
            if (!hologramLines.isEmpty()) {
                zone.showHologram(hologramLines);
            }
        }
    }

    public void showZoneOutline() {
        for (Zone zone : zones) {
            Location center = zone.getCentre();
            World world = center.getWorld();
            double y = center.getY();
            double radius = zone.getRadius();

            String teamName = zone.lastCaptureTeam;
            Color particleColor = Color.fromBGR(158,233,228);

            if (teamName != null) {
                Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
                if (team != null && team.getColor() != null) {
                    ChatColor teamColor = team.getColor();
                    particleColor = chatColorToBukkitColor(teamColor);
                }
            }

            Particle.DustOptions dustOptions = new Particle.DustOptions(particleColor, 1.0F);

            int points = 60;
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);

                Location particleLoc = new Location(world, x, y, z);
                world.spawnParticle(Particle.REDSTONE, particleLoc, 1, dustOptions);
            }
        }

    }

    private Color chatColorToBukkitColor(ChatColor color) {
        switch (color) {
            case RED: return Color.RED;
            case BLUE: return Color.BLUE;
            case GREEN: return Color.LIME;
            case YELLOW: return Color.YELLOW;
            case AQUA: return Color.AQUA;
            case GOLD: return Color.ORANGE;
            case DARK_RED: return Color.MAROON;
            case DARK_BLUE: return Color.NAVY;
            case DARK_GREEN: return Color.GREEN;
            case DARK_PURPLE: return Color.PURPLE;
            case LIGHT_PURPLE: return Color.fromRGB(255, 105, 255);
            case DARK_AQUA: return Color.fromRGB(0, 170, 170);
            case WHITE: return Color.WHITE;
            case BLACK: return Color.BLACK;
            case GRAY: return Color.GRAY;
            case DARK_GRAY: return Color.fromRGB(50, 50, 50);
            default: return Color.fromBGR(158,233,228);
        }
    }


    public void resetCaptures() {
        for (Zone zone : zones) {
            zone.shutdown();
            zone.resetCaptureStatus();
        }
        zones.clear();
    }

}
