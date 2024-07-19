package me.vexmc.enchantments.runnable;

import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import me.vexmc.enchantments.listener.SoulActivateListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SoulParticleRunnable implements Runnable {
   private final SoulGemsPlugin plugin;

   public SoulParticleRunnable(SoulGemsPlugin plugin) {
      this.plugin = plugin;
   }

   @Override
   public void run() {
      CompletableFuture.runAsync(() -> {
         for (UUID playerId : SoulActivateListener.ACTIVE_PLAYERS) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
               continue;
            }
            Bukkit.getScheduler().runTask(plugin, () -> Utils.spawnParticle(player, plugin, "particles.soul-idle", true));
         }
      });
   }
}