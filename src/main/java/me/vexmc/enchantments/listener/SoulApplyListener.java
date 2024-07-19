package me.vexmc.enchantments.listener;

import net.advancedplugins.ae.api.SoulApplyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SoulApplyListener implements Listener {
   @EventHandler
   public void onSoulApply(SoulApplyEvent event) {
      event.setCancelled(true);
   }
}
