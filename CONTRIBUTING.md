# Contributing To SoulGems

## Local Setup

Requirements:

- Java 17 or newer
- The official AdvancedEnchantments API jar

Maven is handled by the project wrapper, so you do not need to install Maven globally.

## Build

1. Download the AdvancedEnchantments API jar from the official vendor resource page.
2. Save it as `libs/ae-api-9.9.14.jar`.
3. Run:

```cmd
scripts\build.cmd
```

The plugin jar will be written to `target/`.

## Direct Maven Commands

After the AE API jar has been installed locally once, you can also use:

```cmd
mvnw.cmd clean package
```

## Runtime Testing

SoulGems declares `depend: [AdvancedEnchantments]`, so a local test server needs both jars in its `plugins` folder:

- AdvancedEnchantments
- SoulGems from `target/`
