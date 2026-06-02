package me.vexmc.enchantments;

import net.advancedplugins.ae.api.AEAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SoulGemFormatter {
   private static final String NAME_FORMAT = "&r&c&lSoul Gem &r&c&l[%s&r&c&l]";

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
      Component displayName = Utils.translateColorCodes(String.format(NAME_FORMAT, formatSoulCount(souls)))
              .decoration(TextDecoration.ITALIC, false);
      meta.displayName(displayName);
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

      if (souls >= 10000) {
         return "&r&4&l&n" + count;
      }

      if (souls >= 5000) {
         return "&r&4&l" + count;
      }

      if (souls >= 1000) {
         return "&r&6&l" + count;
      }

      if (souls >= 500) {
         return "&r&b" + count;
      }

      if (souls >= 100) {
         return "&r&a" + count;
      }

      return "&r&f" + count;
   }
}
