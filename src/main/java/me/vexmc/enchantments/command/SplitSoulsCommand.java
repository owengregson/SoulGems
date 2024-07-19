package me.vexmc.enchantments.command;

import me.vexmc.enchantments.SoulGemsPlugin;
import me.vexmc.enchantments.Utils;
import net.advancedplugins.ae.api.AEAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SplitSoulsCommand implements CommandExecutor {
   private final SoulGemsPlugin plugin;

   public SplitSoulsCommand(SoulGemsPlugin plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(Component.text("This command can only be used by players.").color(NamedTextColor.RED));
         return true;
      }

      Player player = (Player) sender;
      ItemStack item = player.getInventory().getItemInMainHand();

      if (!AEAPI.isASoulGem(item)) {
         Utils.sendMessages(player, plugin.getConfig().getStringList("settings.splitsouls.not-a-soulgem"));
         return true;
      }

      if (args.length == 0) {
         Utils.sendMessages(player, plugin.getConfig().getStringList("settings.splitsouls.usage"));
         return true;
      }

      int take;
      try {
         take = Math.abs(Integer.parseInt(args[0]));
      } catch (NumberFormatException e) {
         player.sendMessage(Component.text("Invalid number format.").color(NamedTextColor.RED));
         return true;
      }

      int souls = AEAPI.getSoulsOnGem(item);
      if (souls - take <= 0 || take == 0) {
         Utils.sendMessages(player, plugin.getConfig().getStringList("settings.splitsouls.cannot-split"));
         return true;
      }

      int splitInto = souls - take;
      player.getInventory().setItemInMainHand(AEAPI.getSoulGem(splitInto));
      player.getInventory().addItem(AEAPI.getSoulGem(take));
      Utils.sendMessages(player, plugin.getConfig().getStringList("settings.splitsouls.split"));
      Utils.playSounds(player, plugin, "split-soul-gems");
      return true;
   }
}