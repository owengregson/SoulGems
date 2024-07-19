package me.vexmc.enchantments.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AuthorsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("soulgems.command.authors")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        Component emptyLine = Component.text("");
        Component line1 = Component.text("This plugin was created with ")
                .color(NamedTextColor.GREEN)
                .append(Component.text("♥")
                        .color(NamedTextColor.RED))
                .append(Component.text(" by:")
                        .color(NamedTextColor.GREEN));
        Component line2 = Component.text("owengregson")
                .color(NamedTextColor.YELLOW)
                .decorate(TextDecoration.UNDERLINED)
                .hoverEvent(HoverEvent.showText(Component.text("Click to view my GitHub profile!")))
                .clickEvent(ClickEvent.openUrl("https://github.com/owengregson"))
                .append(Component.text(" on GitHub.")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)
                );

        sender.sendMessage(emptyLine);
        sender.sendMessage(line1);
        sender.sendMessage(line2);
        sender.sendMessage(emptyLine);
        return true;
    }
}
