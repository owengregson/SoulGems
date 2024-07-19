package me.vexmc.enchantments.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnectListener implements Listener {
   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      SoulActivateListener.ACTIVE_PLAYERS.remove(player.getUniqueId());
   }

   @EventHandler
   public void onPlayerKick(PlayerKickEvent event) {
      Player player = event.getPlayer();
      SoulActivateListener.ACTIVE_PLAYERS.remove(player.getUniqueId());
   }
}
