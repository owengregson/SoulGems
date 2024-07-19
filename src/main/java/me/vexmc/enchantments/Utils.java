package me.vexmc.enchantments;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Random RANDOM = new Random();
    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");

    public static void spawnParticle(Player player, Plugin plugin, String configPath, boolean visibleToAll) {
        Particle particle = Particle.valueOf(plugin.getConfig().getString(configPath + ".type"));
        int amount = plugin.getConfig().getInt(configPath + ".amount");
        double spread = plugin.getConfig().getDouble(configPath + ".spread");
        double yOffset = plugin.getConfig().getDouble("particles.y-offset");
        Location location = player.getLocation();
        World world = player.getWorld();
        if (particle == Particle.REDSTONE) {
            int r = plugin.getConfig().getInt(configPath + ".colorR");
            int g = plugin.getConfig().getInt(configPath + ".colorG");
            int b = plugin.getConfig().getInt(configPath + ".colorB");
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1.0F);

            for (int i = 0; i < amount; i++) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                double offsetY = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                double offsetZ = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                if (visibleToAll) {
                    world.spawnParticle(particle, location.getX() + offsetX, location.getY() + yOffset + offsetY, location.getZ() + offsetZ, 0, dustOptions);
                } else {
                    player.spawnParticle(particle, location.getX() + offsetX, location.getY() + yOffset + offsetY, location.getZ() + offsetZ, 0, dustOptions);
                }
            }
        } else {
            for (int i = 0; i < amount; i++) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                double offsetY = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                double offsetZ = (RANDOM.nextDouble() - 0.5) * 2 * spread;
                if (visibleToAll) {
                    world.spawnParticle(particle, location.getX() + offsetX, location.getY() + yOffset + offsetY, location.getZ() + offsetZ, 0);
                } else {
                    player.spawnParticle(particle, location.getX() + offsetX, location.getY() + yOffset + offsetY, location.getZ() + offsetZ, 0);
                }
            }
        }
    }

    public static void sendMessages(Player player, List<String> messages) {
        for (String line : messages) {
            Component message = translateColorCodes(line);
            player.sendMessage(message);
        }
    }

    public static void playSounds(Player player, Plugin plugin, String key) {
        List<String> soundList = plugin.getConfig().getStringList("sounds." + key);
        for (String soundValue : soundList) {
            String[] split = soundValue.split(":");
            org.bukkit.Sound sound = org.bukkit.Sound.valueOf(split[0]);
            float volume = Float.parseFloat(split[1]);
            float pitch = Float.parseFloat(split[2]);
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static Component translateColorCodes(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String hexColor = matcher.group(1);
            String replacement = "&x&" + hexColor.charAt(0) + "&" + hexColor.charAt(1) +
                    "&" + hexColor.charAt(2) + "&" + hexColor.charAt(3) +
                    "&" + hexColor.charAt(4) + "&" + hexColor.charAt(5);
            matcher.appendReplacement(builder, replacement);
        }
        matcher.appendTail(builder);
        String processedMessage = builder.toString();
        return LegacyComponentSerializer.legacyAmpersand().deserialize(processedMessage);
    }

    public static void debug(@NotNull Player player, Plugin plugin, @NotNull String msg) {
        if (plugin.getConfig().getBoolean("settings.debug")) {
            player.sendMessage(Component.text().content("§c§lSoulGems DEBUG: §7" + msg).build());
        }
    }
}