package me.vexmc.enchantments.command;

import me.vexmc.enchantments.SoulGemsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
   private final SoulGemsPlugin plugin;

   public ReloadCommand(SoulGemsPlugin plugin) {
      this.plugin = plugin;
   }

   @Override
   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (!sender.hasPermission("soulgems.command.reload")) {
         sender.sendMessage("§cYou do not have permission to use this command.");
         return true;
      }

      plugin.reloadConfig();
      sender.sendMessage("§aReloaded configuration successfully!");
      return true;
   }
}
