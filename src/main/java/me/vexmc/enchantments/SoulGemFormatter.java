package me.vexmc.enchantments;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoulGemFormatter {
   private static final String NAME_FORMAT = "&c&lSoul Gem [%s&c&l]";

   private SoulGemFormatter() {
   }

   @NotNull
   public static ItemStack create(int souls) {
      return format(AEAPI.getSoulGem(souls));
   }

   @Nullable
   public static ItemStack format(@Nullable ItemStack item) {
      if (item == null || item.getType() == Material.AIR || !AEAPI.isASoulGem(item)) {
         return item;
      }

      ItemMeta meta = item.getItemMeta();
      if (meta == null) {
         return item;
      }

      int souls = AEAPI.getSoulsOnGem(item);
      meta.displayName(Utils.translateColorCodes(String.format(NAME_FORMAT, formatSoulCount(souls))));
      item.setItemMeta(meta);
      return item;
   }

   public static void formatInventory(@NotNull Player player) {
      PlayerInventory inventory = player.getInventory();
      for (ItemStack item : inventory.getContents()) {
         format(item);
      }

      format(inventory.getItemInOffHand());
      ItemStack cursor = player.getItemOnCursor();
      if (cursor != null && cursor.getType() != Material.AIR) {
         player.setItemOnCursor(format(cursor));
      }
   }

   private static String formatSoulCount(int souls) {
      String count = Integer.toString(souls);

      if (souls >= 5000) {
         return "&4" + count;
      }

      if (souls >= 1000) {
         return "&6&l" + count;
      }

      if (souls > 100) {
         return "&b" + count;
      }

      return "&a" + count;
   }
}
