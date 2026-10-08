### SMPRPG

This plugin aims to completely transform the progression of an SMP, but still stay true
to what makes vanilla Minecraft special. The core gameplay is still the same, but
the progression is completely reworked to add more depth to all aspects of the game
in the form of skills and additional resources, loot, and gear. This allows more aspects
of the game to have a reason to be played, so that players are encouraged to put in more
time into things such as farming Elder Guardians, hypermaxing a fishing setup, and even having
a reason to use expensive maxed out armor to give them an edge in lategame content.

## Setup

This plugin is quite intrusive to core Minecraft, and as such, requires a few settings in 
server configs to be set to work correctly. Before continuing, **double check that you are
running a Paper server.** This plugin is specifically designed to only work on Paper.

### Dependencies

The following plugins are required to run SMPRPG (failure to have any of these plugins will cause the server to not start or the plugin to not load):

- CraftEngine: This plugin is for many of the plugin's custom mechanics, such as custom blocks and block behaviors.
- BetterModel: This plugin is used for custom entity models and animations.
- ProtocolLib: This plugin is used for various packet manipulations, such as preventing particle spam when attacking with high attack damage.
- VaultUnlocked: This plugin is the middleman for economy, permission, and chat related operations.
- Any economy plugin (LiteEco recommended): This plugin is used to manage the in-game economy, which is used for things such as buying and selling items. Any economy plugin that is compatible with Vault should work, but I have only tested with LiteEco.

Recommended plugins (not required, but highly recommended for a better experience):
- LuckPerms: This plugin is used to manage permissions, which is used for things such as giving players access to certain commands and features. Any permissions plugin that is compatible with Vault should work, but I have only tested with LuckPerms.
- WorldEdit/WorldGuard: WorldGuard is directly supported by the plugin for things like block breaking prevention in protected regions.

### plugins/CraftEngine/config.yml
There are some settings you need to tweak in CraftEngine's config.yml to make sure the plugin works correctly.
Run the server once to generate the config, then change the following settings:
`merge-external-folders`: add the path where the /resourcepack directory lives, relative to the plugin path. For example, if your resource pack is in `plugins/SMPRPG/resourcepack`, you would add `../SMPRPG/resourcepack` to the list of merged folders. This allows the plugin to automatically merge the resource pack with any other resource packs on the server, so that you don't have to worry about it.
`delay-configuration-load`: set to `false` to make sure the plugin loads the config immediately. This is important for datapack-generated spawn chunks.

### spigot.yml
- Change the `settings.attribute` options (excluding movement speed) to allow values of precisely `2.0E9`.
By default, vanilla Minecraft only allows entities to have a max of 2048 HP and deal 2048 max damage. This isn't high enough.

### paper-world-defaults.yml
- Change `entities.behavior.disable-player-crits` to `true`. Critical hits are managed manually by the plugin since crit
behavior cannot be modified via either the Paper, Spigot, or Bukkit API.
- I would recommend turning on `anticheat.anti-xray`. I have never run a server where nobody has attempted to xray, and the
free built in anti-cheat with paper for this comes in handy.

### In-Game
Once the world has generated and you have logged in to the server, it is recommended to delete the world and regenerate it. 
This is so that the server properly starts up with all properly configured settings and desired configs.
Once you have done that, you should join the server and run `/ce reload pack` to regenerate the resource pack.

### Misc notes
It is also important to note that any player data is stored using persistent data containers. This means that
any progression data such as skills are stored in your `world/playerdata/` folder. 
