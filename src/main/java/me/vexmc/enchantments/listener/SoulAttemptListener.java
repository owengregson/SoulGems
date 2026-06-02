package me.vexmc.enchantments.listener;

import me.vexmc.enchantments.SoulGemFormatter;
import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import net.advancedplugins.ae.api.AEAPI;
import net.advancedplugins.ae.impl.effects.armorutils.ArmorEquipEvent;
import net.advancedplugins.ae.impl.effects.api.AbilityPreactivateEvent;
import net.advancedplugins.ae.impl.effects.effects.actions.ActionExecutionBuilder;
import net.advancedplugins.ae.impl.effects.effects.abilities.AdvancedAbility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class SoulAttemptListener implements Listener {
   private static final SoulGemsPlugin PLUGIN = SoulGemsPlugin.getInstance();
   private static final long REPEATING_DUPLICATE_WINDOW_MILLIS = 250L;
   private static final long COMMAND_REFRESH_WINDOW_MILLIS = 1500L;
   private static final Map<String, Long> RECENT_REPEATING_ACTIVATIONS = new HashMap<>();
   private static final Map<UUID, Long> HEAL_COMMAND_REFRESHES = new HashMap<>();

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
      String command = event.getMessage().trim();
      if (!command.toLowerCase(Locale.ROOT).startsWith("/heal")) {
         return;
      }

      Player target = event.getPlayer();
      String[] split = command.split("\\s+");
      if (split.length > 1) {
         Player commandTarget = Bukkit.getPlayer(split[1]);
         if (commandTarget != null) {
            target = commandTarget;
         }
      }

      HEAL_COMMAND_REFRESHES.put(target.getUniqueId(), System.currentTimeMillis() + COMMAND_REFRESH_WINDOW_MILLIS);
   }

   @EventHandler
   public void onAbilityPreactivate(AbilityPreactivateEvent event) {
      if (event.getMainEntity() instanceof Player) {
         Player player = (Player) event.getMainEntity();
         AdvancedAbility ability = event.getEffect();
         int soulCost = ability.getSouls();

         Utils.debug(player, PLUGIN, "Enchant Attempt Event!");

         if (soulCost <= 0) {
            Utils.debug(player, PLUGIN, "Non-soul enchant detected, so SoulGems will not modify it.");
            return;
         }

         if (isCommandReapply(event)) {
            Utils.debug(player, PLUGIN, "Ignoring soul enchant during /apply or /reapply.");
            event.setCancelled(true);
            return;
         }

         if (isRemovalActivation(event)) {
            Utils.debug(player, PLUGIN, "Ignoring soul cost during enchant removal.");
            return;
         }

         if (isHealCommandRefresh(player, event)) {
            Utils.debug(player, PLUGIN, "Ignoring soul enchant during /heal refresh.");
            event.setCancelled(true);
            return;
         }

         if (isRepeatingArmorUnequip(event, ability)) {
            Utils.debug(player, PLUGIN, "Ignoring repeating soul enchant during armor unequip.");
            event.setCancelled(true);
            return;
         }

         if (!SoulActivateListener.ACTIVE_PLAYERS.contains(player.getUniqueId())) {
            Utils.debug(player, PLUGIN, "SM disabled, so the event was cancelled.");
            event.setCancelled(true);
            return;
         }

         if (shouldCancelEmptyCureAbility(player, ability)) {
            Utils.debug(player, PLUGIN, "Soul cure enchant has nothing to cure, so the event was cancelled.");
            event.setCancelled(true);
            return;
         }

         if (isDuplicateRepeatingActivation(player, ability, event)) {
            Utils.debug(player, PLUGIN, "Duplicate repeating soul enchant detected, so the event was cancelled.");
            event.setCancelled(true);
            return;
         }

         if (cantUse(player, soulCost)) {
            event.setCancelled(true);
            Utils.debug(player, PLUGIN, "Not enough souls, so the event was cancelled.");
         } else {
            useSouls(player, soulCost);
         }
      }
   }

   private static boolean isCommandReapply(@NotNull AbilityPreactivateEvent event) {
      ActionExecutionBuilder builder = getBuilder(event);
      if (builder == null) {
         return false;
      }

      Event sourceEvent = builder.getEvent();
      if (!(sourceEvent instanceof ArmorEquipEvent)) {
         return false;
      }

      ArmorEquipEvent armorEquipEvent = (ArmorEquipEvent) sourceEvent;
      return armorEquipEvent.getMethod() == ArmorEquipEvent.EquipMethod.COMMAND;
   }

   private static boolean isHealCommandRefresh(@NotNull Player player, @NotNull AbilityPreactivateEvent event) {
      long now = System.currentTimeMillis();
      HEAL_COMMAND_REFRESHES.entrySet().removeIf(entry -> entry.getValue() <= now);

      Long expiresAt = HEAL_COMMAND_REFRESHES.get(player.getUniqueId());
      if (expiresAt == null || expiresAt <= now) {
         return false;
      }

      ActionExecutionBuilder builder = getBuilder(event);
      if (builder == null) {
         return true;
      }

      Event sourceEvent = builder.getEvent();
      if (sourceEvent == null) {
         return true;
      }

      if (!(sourceEvent instanceof ArmorEquipEvent)) {
         return false;
      }

      ArmorEquipEvent armorEquipEvent = (ArmorEquipEvent) sourceEvent;
      return armorEquipEvent.getMethod() == ArmorEquipEvent.EquipMethod.HOTBAR;
   }

   private static boolean isRemovalActivation(@NotNull AbilityPreactivateEvent event) {
      ActionExecutionBuilder builder = getBuilder(event);
      return builder != null && builder.isRemoved();
   }

   private static boolean isRepeatingArmorUnequip(@NotNull AbilityPreactivateEvent event, @NotNull AdvancedAbility ability) {
      if (!isRepeatingAbility(event, ability)) {
         return false;
      }

      ArmorEquipEvent armorEquipEvent = getArmorEquipEvent(event);
      if (armorEquipEvent == null) {
         return false;
      }

      ItemStack oldArmor = armorEquipEvent.getOldArmorPiece();
      ItemStack newArmor = armorEquipEvent.getNewArmorPiece();
      return isRealItem(oldArmor) && !isRealItem(newArmor);
   }

   private static boolean isDuplicateRepeatingActivation(@NotNull Player player, @NotNull AdvancedAbility ability,
                                                         @NotNull AbilityPreactivateEvent event) {
      if (!isRepeatingAbility(event, ability)) {
         return false;
      }

      long now = System.currentTimeMillis();
      RECENT_REPEATING_ACTIVATIONS.entrySet().removeIf(entry -> now - entry.getValue() > 5000L);

      String key = player.getUniqueId() + ":" + ability.getNameNoLevel();
      Long lastActivation = RECENT_REPEATING_ACTIVATIONS.get(key);
      if (lastActivation != null && now - lastActivation < REPEATING_DUPLICATE_WINDOW_MILLIS) {
         return true;
      }

      RECENT_REPEATING_ACTIVATIONS.put(key, now);
      return false;
   }

   private static boolean isRepeatingAbility(@NotNull AbilityPreactivateEvent event, @NotNull AdvancedAbility ability) {
      ActionExecutionBuilder builder = getBuilder(event);
      if (builder != null && builder.isRepeating()) {
         return true;
      }

      List<String> types = ability.getTypes();
      if (types == null) {
         return false;
      }

      return types.stream().anyMatch(type -> "REPEATING".equalsIgnoreCase(type));
   }

   private static boolean shouldCancelEmptyCureAbility(@NotNull Player player, @NotNull AdvancedAbility ability) {
      List<String> effects = ability.getEffects();
      if (effects == null || effects.isEmpty()) {
         return false;
      }

      boolean hasCureEffect = false;
      boolean hasUnknownCureTarget = false;
      boolean hasActivePotionToCure = false;
      boolean hasNonCureAction = false;

      for (String effect : effects) {
         String normalizedEffect = normalizeEffect(effect);
         if (normalizedEffect.startsWith("CURE:") || normalizedEffect.startsWith("CURE_PERMANENT:")) {
            hasCureEffect = true;
            PotionEffectType potionEffectType = getCurePotionEffect(normalizedEffect);
            if (potionEffectType == null) {
               hasUnknownCureTarget = true;
               continue;
            }

            if (player.hasPotionEffect(potionEffectType)) {
               hasActivePotionToCure = true;
            }
            continue;
         }

         if (!isCompanionEffect(normalizedEffect)) {
            hasNonCureAction = true;
         }
      }

      return hasCureEffect && !hasUnknownCureTarget && !hasActivePotionToCure && !hasNonCureAction;
   }

   private static String normalizeEffect(@NotNull String effect) {
      int targetStart = effect.indexOf(" @");
      String effectWithoutTarget = targetStart >= 0 ? effect.substring(0, targetStart) : effect;
      return effectWithoutTarget.trim().toUpperCase(Locale.ROOT);
   }

   private static PotionEffectType getCurePotionEffect(@NotNull String effect) {
      String[] split = effect.split(":");
      if (split.length < 2) {
         return null;
      }

      return PotionEffectType.getByName(split[1].trim());
   }

   private static boolean isCompanionEffect(@NotNull String effect) {
      return effect.startsWith("MESSAGE:")
              || effect.startsWith("ACTION_BAR:")
              || effect.startsWith("ACTIONBAR:")
              || effect.startsWith("PARTICLE:")
              || effect.startsWith("PLAY_SOUND:")
              || effect.startsWith("SOUND:")
              || effect.startsWith("WAIT:");
   }

   private static ArmorEquipEvent getArmorEquipEvent(@NotNull AbilityPreactivateEvent event) {
      ActionExecutionBuilder builder = getBuilder(event);
      if (builder == null) {
         return null;
      }

      Event sourceEvent = builder.getEvent();
      if (!(sourceEvent instanceof ArmorEquipEvent)) {
         return null;
      }

      return (ArmorEquipEvent) sourceEvent;
   }

   private static ActionExecutionBuilder getBuilder(@NotNull AbilityPreactivateEvent event) {
      if (event.getActionExecution() == null || event.getActionExecution().getBuilder() == null) {
         return null;
      }

      return event.getActionExecution().getBuilder();
   }

   private static boolean isRealItem(ItemStack item) {
      return item != null && item.getType() != Material.AIR;
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
      if (useSouls(player, player.getInventory().getItemInOffHand(), soulCount, true, -1)) {
         return;
      }

      ItemStack[] contents = player.getInventory().getContents();

      for (int i = 0; i < contents.length; i++) {
         if (useSouls(player, contents[i], soulCount, false, i)) {
            return;
         }
      }
   }

   private static boolean useSouls(@NotNull Player player, ItemStack item, int soulCount, boolean offHand, int slot) {
      if (!AEAPI.isASoulGem(item)) {
         return false;
      }

      int souls = AEAPI.getSoulsOnGem(item);
      if (souls < soulCount) {
         return false;
      }

      int remainingSouls = souls - soulCount;
      ItemStack replacement = remainingSouls > 0 ? SoulGemFormatter.create(remainingSouls) : null;
      if (offHand) {
         player.getInventory().setItemInOffHand(replacement);
      } else {
         player.getInventory().setItem(slot, replacement);
      }

      Utils.spawnParticle(player, PLUGIN, "particles.soul-use", false);
      Utils.playSounds(player, PLUGIN, "soul-use");
      Utils.debug(player, PLUGIN, "Used " + soulCount + " souls.");
      return true;
   }
}
