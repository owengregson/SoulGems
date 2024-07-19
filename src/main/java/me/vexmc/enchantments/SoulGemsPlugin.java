package me.vexmc.enchantments;

import com.google.common.collect.ImmutableMap;
import me.vexmc.enchantments.command.AuthorsCommand;
import me.vexmc.enchantments.command.ReloadCommand;
import me.vexmc.enchantments.command.SplitSoulsCommand;
import me.vexmc.enchantments.listener.PlayerDisconnectListener;
import me.vexmc.enchantments.listener.SoulActivateListener;
import me.vexmc.enchantments.listener.SoulApplyListener;
import me.vexmc.enchantments.listener.SoulAttemptListener;
import me.vexmc.enchantments.listener.SoulCombineListener;
import me.vexmc.enchantments.runnable.SoulDisableRunnable;
import me.vexmc.enchantments.runnable.SoulParticleRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class SoulGemsPlugin extends JavaPlugin {

   @Override
   public void onLoad() {
      String version = "?";
      InputStream stream = getResource("plugin.yml");
      if (stream != null) {
         YamlConfiguration yaml = YamlConfiguration.loadConfiguration(new InputStreamReader(stream));
         version = yaml.getString("version");
         try {
            stream.close();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
      getLogger().info("=====================");
      getLogger().info("SoulGems v" + version + " by owengregson");
      getLogger().info("Loading plugin...");
      getLogger().info("=====================");
      saveDefaultConfig();
   }

   @Override
   public void onEnable() {
      BukkitScheduler scheduler = Bukkit.getScheduler();
      scheduler.runTaskTimer(this, new SoulParticleRunnable(this), 0L, 20L);
      scheduler.runTaskTimer(this, new SoulDisableRunnable(this), 0L, 2L);

      registerCommands(ImmutableMap.of(
              "reload", new ReloadCommand(this),
              "splitsouls", new SplitSoulsCommand(this),
              "authors", new AuthorsCommand()
      ));

      registerListeners(
              new PlayerDisconnectListener(),
              new SoulActivateListener(),
              new SoulApplyListener(),
              new SoulAttemptListener(),
              new SoulCombineListener()
      );

      getLogger().info("SoulGems enabled successfully!");
   }

   private void registerCommands(@NotNull Map<String, CommandExecutor> commands) {
      commands.forEach((name, executor) -> {
         PluginCommand command = getCommand(name);
         if (command != null) {
            command.setExecutor(executor);
         }
      });
   }

   private void registerListeners(@NotNull Listener... listeners) {
      for (Listener listener : listeners) {
         getServer().getPluginManager().registerEvents(listener, this);
      }
   }

   @NotNull
   public static SoulGemsPlugin getInstance() {
      return getPlugin(SoulGemsPlugin.class);
   }
}