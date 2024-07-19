# SoulGems
A Spigot Plugin which adds right-clickable Soul Gems to the game.
The plugin is an addon for the popular custom enchantments plugin, [AdvancedEnchantments](https://www.spigotmc.org/resources/1-17-1-21-%E2%AD%95-advancedenchantments-%E2%AD%90-450-custom-enchants-%E2%AD%90create-custom-enchantments-%E2%9C%85.43058/).

This plugin was made for Spigot 1.20.4 but should be forward compatible with 1.21 and backward-compatible with 1.20.x.
It may also be compatible with earlier versions of the game, but I have not tested it.

## Download
A prebuilt version of the plugin is available in the [Releases](https://github.com/owengregson/SoulGems/releases) tab.

## Current Features
* Combine Soul Gems
Simply drag them on top of each other.
* Split Soul Gems apart
Split into smaller pieces using /splitsouls [amount] while holding a Soul Gem.
* Send Messages
You can send messages on various events with hex color code support. Send messages on: Enable, Disable, Use, Split
* Spawn Particles
Particles can be spawned on various events with configurable colors, amount, and spread. Spawn particles on: Enable, Disable, Use, Idle
* Play Sound Effects
The plugin can play sounds on various events with configurable volume and pitch. Play sounds on: Enable, Disable, Use, Split, Combine


# Guide
I might make a guide on how to set up the development environment yourself at some point, but I am too busy right now.
The process is pretty standard for a MC plugin development environment, just set it up like you usually would and use maven to build.
The build process is dependent on the AdvancedEnchantments jar, so make sure you have that in your maven dependencies and build with it.
