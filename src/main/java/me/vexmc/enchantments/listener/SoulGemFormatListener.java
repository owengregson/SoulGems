package me.vexmc.enchantments.listener;

import me.vexmc.enchantments.SoulGemFormatter;
import me.vexmc.enchantments.SoulGemsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class SoulGemFormatListener implements Listener {
   private final SoulGemsPlugin plugin;

   public SoulGemFormatListener(SoulGemsPlugin plugin) {
      this.plugin = plugin;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      formatSoon(event.getPlayer());
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      SoulGemFormatter.format(event.getItem());
      formatSoon(event.getPlayer());
   }

   @EventHandler
   public void onPlayerItemHeld(PlayerItemHeldEvent event) {
      formatSoon(event.getPlayer());
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         formatSoon((Player) event.getWhoClicked());
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         formatSoon((Player) event.getWhoClicked());
      }
   }

   @EventHandler
   public void onPlayerDropItem(PlayerDropItemEvent event) {
      SoulGemFormatter.format(event.getItemDrop().getItemStack());
   }

   @EventHandler
   public void onEntityPickupItem(EntityPickupItemEvent event) {
      if (event.getEntity() instanceof Player) {
         SoulGemFormatter.format(event.getItem().getItemStack());
         formatSoon((Player) event.getEntity());
      }
   }

   private void formatSoon(@NotNull Player player) {
      Bukkit.getScheduler().runTask(plugin, () -> SoulGemFormatter.formatInventory(player));
   }
}
