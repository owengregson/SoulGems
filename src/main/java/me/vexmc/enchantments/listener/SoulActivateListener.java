package me.vexmc.enchantments.listener;

import com.google.common.collect.Lists;
import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class SoulActivateListener implements Listener {
   private static final SoulGemsPlugin PLUGIN = SoulGemsPlugin.getInstance();
   public static final List<UUID> ACTIVE_PLAYERS = Lists.newArrayList();

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         ItemStack item = event.getItem();
         if (AEAPI.isASoulGem(item)) {
            Player player = event.getPlayer();
            if (ACTIVE_PLAYERS.contains(player.getUniqueId())) {
               deactivateSoulMode(player);
            } else {
               activateSoulMode(player);
            }
         }
      }
   }

   private void deactivateSoulMode(Player player) {
      ACTIVE_PLAYERS.remove(player.getUniqueId());
      Utils.sendMessages(player, PLUGIN.getConfig().getStringList("settings.soul-mode.messages.disabled"));
      Utils.spawnParticle(player, PLUGIN, "particles.soul-disable", false);
      Utils.playSounds(player, PLUGIN, "toggle-soul-mode-off");
   }

   private void activateSoulMode(Player player) {
      ACTIVE_PLAYERS.add(player.getUniqueId());
      Utils.sendMessages(player, PLUGIN.getConfig().getStringList("settings.soul-mode.messages.enabled"));
      Utils.spawnParticle(player, PLUGIN, "particles.soul-enable", false);
      Utils.playSounds(player, PLUGIN, "toggle-soul-mode-on");
      Utils.playSounds(player, PLUGIN, "toggle-soul-mode-on-layer");
   }
}
