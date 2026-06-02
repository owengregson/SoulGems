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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("soulgems.command.authors")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.")
                    .color(NamedTextColor.RED));
            return true;
        }

        Component emptyLine = Component.text("");
        Component title = Component.text("SoulGems Credits")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        Component originalHeader = Component.text("Original plugin created with ")
                .color(NamedTextColor.GREEN)
                .append(Component.text("\u2665")
                        .color(NamedTextColor.RED))
                .append(Component.text(" by:")
                        .color(NamedTextColor.GREEN));
        Component originalAuthor = Component.text("owengregson")
                .color(NamedTextColor.YELLOW)
                .hoverEvent(HoverEvent.showText(Component.text("Click to view the GitHub profile.")))
                .clickEvent(ClickEvent.openUrl("https://github.com/owengregson"))
                .append(Component.text(" on GitHub.")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE));
        Component modifiedBy = Component.text(" Modified by ")
                .color(NamedTextColor.GREEN)
                .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)
                .append(Component.text("nickdoa")
                        .color(NamedTextColor.YELLOW)
                        .decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(Component.text("Click to view the GitHub profile.")))
                        .clickEvent(ClickEvent.openUrl("https://github.com/nickdoa")))
                .append(Component.text(".")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE));

        sender.sendMessage(emptyLine);
        sender.sendMessage(title);
        sender.sendMessage(originalHeader);
        sender.sendMessage(originalAuthor.append(modifiedBy));
        sender.sendMessage(emptyLine);
        return true;
    }
}
