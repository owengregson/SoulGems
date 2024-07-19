package me.vexmc.enchantments.listener;

import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.impl.effects.api.AbilityPreactivateEvent;
import net.advancedplugins.ae.impl.effects.effects.abilities.AdvancedAbility;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

public class SoulAttemptListener implements Listener {
   private static final SoulGemsPlugin PLUGIN = SoulGemsPlugin.getInstance();

   @EventHandler
   public void onAbilityPreactivate(AbilityPreactivateEvent event) {
      if (event.getMainEntity() instanceof Player) {
         Player player = (Player) event.getMainEntity();
         AdvancedAbility ability = event.getEffect();

         Utils.debug(player, PLUGIN, "Enchant Attempt Event!");

         if (!SoulActivateListener.ACTIVE_PLAYERS.contains(player.getUniqueId())) {
            Utils.debug(player, PLUGIN, "SM disabled, so the event was cancelled.");
            event.setCancelled(true);
            return;
         }

         if (cantUse(player, ability.getSouls())) {
            event.setCancelled(true);
            Utils.debug(player, PLUGIN, "Not enough souls, so the event was cancelled.");
         } else {
            useSouls(player, ability.getSouls());
         }
      }
   }

   public static boolean cantUse(@NotNull Player player, int soulCount) {
      return Stream.of(player.getInventory().getContents())
              .filter(Objects::nonNull)
              .filter(AEAPI::isASoulGem)
              .noneMatch(item -> {
                 int souls = AEAPI.getSoulsOnGem(item);
                 Utils.debug(player, PLUGIN, "Found Soul Gem with " + souls + " souls in your inventory.");
                 return souls >= soulCount;
              });
   }

   public static void useSouls(@NotNull Player player, int soulCount) {
      ItemStack[] contents = player.getInventory().getContents();

      for (int i = 0; i < contents.length; i++) {
         ItemStack item = contents[i];
         if (AEAPI.isASoulGem(item)) {
            int souls = AEAPI.getSoulsOnGem(item);
            if (souls < soulCount) {
               continue;
            }

            Utils.spawnParticle(player, PLUGIN, "particles.soul-use", false);

            boolean offHandIsItem = false;
            if (AEAPI.isASoulGem(player.getInventory().getItemInOffHand())) {
               player.getInventory().setItemInOffHand(null);
               offHandIsItem = true;
            } else {
               player.getInventory().remove(item);
            }

            if (souls - soulCount > 0) {
               DecimalFormat formatter = new DecimalFormat("#,###");
               ItemStack soulGem = AEAPI.getSoulGem(souls - soulCount);
               if (offHandIsItem) {
                  player.getInventory().setItemInOffHand(soulGem);
               } else {
                  player.getInventory().setItem(i, soulGem);
               }

               Utils.playSounds(player, PLUGIN, "soul-use");
               Utils.sendMessages(player, PLUGIN.getConfig().getStringList("settings.soul-use.messages")
                       .stream()
                       .map(message -> message.replace("%amount%", formatter.format(souls - soulCount)))
                       .toList());
               Utils.debug(player, PLUGIN, "Used " + soulCount + " souls.");
            }
         }
      }
   }
}