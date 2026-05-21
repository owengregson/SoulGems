# Local AdvancedEnchantments API Jar

SoulGems depends on the AdvancedEnchantments API, but `net.advancedplugins:ae-api:9.9.14` is not available from Maven Central, PaperMC, or Sonatype.

Download the official AdvancedEnchantments API jar from the vendor resource page and place it here as:

```text
libs/ae-api-9.9.14.jar
```

Then run:

```cmd
scripts\install-ae-api.cmd
scripts\build.cmd
```

The install script adds the jar to your local Maven cache using these coordinates:

```text
net.advancedplugins:ae-api:9.9.14
```
