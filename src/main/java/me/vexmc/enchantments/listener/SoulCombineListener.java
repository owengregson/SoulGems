package me.vexmc.enchantments.listener;

import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class SoulCombineListener implements Listener {

   private static final SoulGemsPlugin plugin = SoulGemsPlugin.getInstance();

   @EventHandler
   public void onClick(InventoryClickEvent event) {
      ItemStack currentItem = event.getCurrentItem();
      ItemStack cursor = event.getCursor();

      if (AEAPI.isASoulGem(currentItem) && AEAPI.isASoulGem(cursor)) {
         int currentItemSouls = AEAPI.getSoulsOnGem(currentItem) * currentItem.getAmount();
         int cursorSouls = AEAPI.getSoulsOnGem(cursor) * cursor.getAmount();
         Utils.playSounds((Player) event.getWhoClicked(), plugin, "combine-soul-gems");
         event.getView().setCursor(null);
         event.setCurrentItem(AEAPI.getSoulGem(currentItemSouls + cursorSouls));
         event.setCancelled(true);
      }
   }
}
