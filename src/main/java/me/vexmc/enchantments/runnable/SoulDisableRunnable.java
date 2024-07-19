package me.vexmc.enchantments.runnable;

import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import me.vexmc.enchantments.listener.SoulActivateListener;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SoulDisableRunnable implements Runnable {
   private final SoulGemsPlugin plugin;

   public SoulDisableRunnable(SoulGemsPlugin plugin) {
      this.plugin = plugin;
   }

   @Override
   public void run() {
      CompletableFuture.runAsync(() -> {
         List<UUID> removal = new ArrayList<>();

         for (UUID playerId : SoulActivateListener.ACTIVE_PLAYERS) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
               boolean hasSoulGem = Arrays.stream(player.getInventory().getContents()).anyMatch(AEAPI::isASoulGem);
               if (!hasSoulGem) {
                  removal.add(playerId);
               }
            }
         }

         Bukkit.getScheduler().runTask(plugin, () -> {
            for (UUID playerId : removal) {
               Player player = Bukkit.getPlayer(playerId);
               if (player != null) {
                  SoulActivateListener.ACTIVE_PLAYERS.remove(playerId);
                  Utils.sendMessages(player, plugin.getConfig().getStringList("settings.soul-mode.messages.empty"));
                  Utils.spawnParticle(player, plugin, "particles.soul-disable", false);
                  Utils.playSounds(player, plugin, "toggle-soul-mode-off");
               }
            }
         });
      });
   }
}
