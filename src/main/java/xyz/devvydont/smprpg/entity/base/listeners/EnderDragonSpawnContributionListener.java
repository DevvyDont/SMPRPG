package xyz.devvydont.smprpg.entity.base.listeners;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.DragonBattle;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.SpecialEffectService;
import xyz.devvydont.smprpg.effects.tasks.DisintegratingEffect;
import xyz.devvydont.smprpg.entity.bosses.LeveledDragon;
import xyz.devvydont.smprpg.events.LeveledEntitySpawnEvent;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.services.ChatService;
import xyz.devvydont.smprpg.services.DropsService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * In charge of listening to game events in order to add boss spawn contributions for the ender dragon.
 * Bosses are pretty tough to track spawn conditions for if we aren't in charge of it, so this class is responsible
 * for all the gross logic that comes with it.
 */
public class EnderDragonSpawnContributionListener extends ToggleableListener {

    /**
     * How far should we broadcast messages?
     */
    public static final int MESSAGE_RANGE = 200;

    /**
     * ENDER DRAGON.
     * We can only have one fight per world at a time, so we need to listen to raw block place events on the end island
     * for crystals. They can also be destroyed, so we need to handle that too.
     */

    private final Multimap<UUID, EnderCrystal> crystalPlacers = HashMultimap.create();

    /**
     * Gets the corner locations relative to a portal location. Using these locations, you can then do a short
     * distanced entity check to get nearby crystals. The corners need to be placed first, so we can prevent
     * the vanilla spawn check from happening.
     * @param portalLocation The location of the portal.
     * @return A list of blocks of length 4 that can be checked.
     */
    private Collection<Block> getPortalCornerCrystalSlots(Location portalLocation) {

        // The corners relative to the portal location are 1 block up, and 2 blocks offset in both x and z directions.
        var corners = new ArrayList<Block>();
        corners.add(portalLocation.clone().add(2, 1, 2).getBlock());
        corners.add(portalLocation.clone().add(-2, 1, 2).getBlock());
        corners.add(portalLocation.clone().add(2, 1, -2).getBlock());
        corners.add(portalLocation.clone().add(-2, 1, -2).getBlock());
        return corners;
    }

    /**
     * Checks if a given block is a corner spot for a portal.
     * @param portalLocation The location of the portal.
     * @param block The block to check.
     * @return true if the block is a corner, false for any other block.
     */
    private boolean isCorner(final Location portalLocation, Block block) {
        for (var spot : getPortalCornerCrystalSlots(portalLocation))
            if (spot.equals(block)) return true;
        return false;
    }

    /**
     * Checks if a given block is a normal spot for a portal.
     * @param portalLocation The location of the portal.
     * @param block The block to check.
     * @return true if the block is a normal spot, false for any other block.
     */
    private boolean isEdge(final Location portalLocation, Block block) {
        for (var spot : getPortalNormalSlots(portalLocation))
            if (spot.equals(block)) return true;
        return false;
    }

    /**
     * Does a location check to find if a crystal is currently residing in a block slot.
     * @param block The block to check.
     * @return The {@link EnderCrystal} instance, if it exists.
     */
    private @Nullable EnderCrystal getCrystalOnBlock(Block block) {
        var crystals = block.getLocation().getNearbyEntitiesByType(EnderCrystal.class, .25);

        if (crystals.size() > 1){
            SMPRPG.broadcastToOperators(ComponentUtils.create("Found more than 1 end crystal! Searching logic is too lenient!!!"));
            return null;
        }

        if (crystals.isEmpty())
            return null;

        return crystals.stream().findFirst().get();
    }

    /**
     * Checks if crystals are present on all corners of the portal.
     * Returns true if so, false otherwise.
     * @param portalLocation The portal location.
     * @return true if the corners are filled.
     */
    private boolean areCornersFilledWithCrystals(Location portalLocation) {
        for (var corner : getPortalCornerCrystalSlots(portalLocation))
            if (getCrystalOnBlock(corner) == null)
                return false;
        return true;
    }

    /**
     * Gets the corner locations relative to a portal location. Using these locations, you can then do a short
     * distanced entity check to get nearby crystals. The normal slots need to be spawned last, so we can allow
     * the vanilla spawning method to work.
     * @param portalLocation The location of the portal.
     * @return A list of blocks of length 4 that can be checked.
     */
    private Collection<Block> getPortalNormalSlots(Location portalLocation) {

        // The normal spots are 1 block up, and offset by 3 blocks in all 4 directions.
        var corners = new ArrayList<Block>();
        corners.add(portalLocation.clone().add(3, 1, 0).getBlock());
        corners.add(portalLocation.clone().add(-3, 1, 0).getBlock());
        corners.add(portalLocation.clone().add(0, 1, 3).getBlock());
        corners.add(portalLocation.clone().add(0, 1, -3).getBlock());
        return corners;
    }

    /**
     * When a dragon spawns, use the context of the currently placed crystals to luck boost!
     */
    @EventHandler
    public void __onDragonRespawn(LeveledEntitySpawnEvent event) {
        if (!(event.entity instanceof LeveledDragon dragon))
            return;

        dragon.setSummoned(true);
        dragon.setConfiguration(dragon.getDefaultConfiguration());
        dragon.heal();

        // Contribute some chance boosting for the crystals we have. Clear out the crystals once we are done!
        // We are also using a formula here that discourages solo spawning the dragon. You lose out on drop odds w/ mass crystal placing.
        for (var entry : crystalPlacers.asMap().entrySet())
            dragon.addSpawnContribution(entry.getKey(), (1-Math.pow((1-LeveledDragon.BASE_CRYSTAL_LUCK), entry.getValue().size())) * LeveledDragon.CRYSTAL_LUCK_SOFT_CAP);

        // Announcement message for the players responsible.
        List<Player> spawners = new ArrayList<>();
        for (var id : crystalPlacers.keys())
            if (Bukkit.getPlayer(id) != null && !spawners.contains(Bukkit.getPlayer(id)))
                spawners.add(Bukkit.getPlayer(id));
        dragon.broadcastSpawnedByPlayers(spawners);
        crystalPlacers.clear();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void __onCrystalDied(EntityRemoveFromWorldEvent event) {

        if (!(event.getEntity() instanceof EnderCrystal crystal))
            return;

        // Find out if a key value pair exists for this crystal.
        for (var entry : crystalPlacers.entries()) {
            if (entry.getValue().equals(event.getEntity())) {

                // We found a crystal, it's tied to a player, and it's going to die. Itemize it.
                var itemStack = ItemService.generate(CustomItemType.SUMMONING_CRYSTAL);
                var player = Bukkit.getPlayer(entry.getKey());
                if (player != null)
                    SMPRPG.getService(DropsService.class).addDefaultDeathFlags(itemStack, player);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), org.bukkit.Sound.BLOCK_TRIAL_SPAWNER_OPEN_SHUTTER, 1f, 1f);
                crystalPlacers.remove(entry.getKey(), crystal);

                // Spawn the item later or else the explosion will kill it...
                Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), () -> event.getEntity().getWorld().dropItem(event.getEntity().getLocation().add(0, 1, 0), itemStack, i ->{
                    i.setInvulnerable(true);
                    i.setGravity(false);
                }), TickTime.INSTANTANEOUSLY);
                return;
            }
        }
    }

    /**
     * When a player damages a crystal that is being tracked, announce that they canceled the spawning ritual.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void __onPlayerDamagePlacedCrystal(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof EnderCrystal crystal))
            return;

        if (!crystalPlacers.containsValue(crystal))
            return;

        // One of our crystals is about to explode... Check if a player did this.
        if (!(event.getDamageSource().getCausingEntity() instanceof Player player))
            return;

        if (event.getDamageSource().getDirectEntity() instanceof EnderCrystal)
            return;

        var nearbyPlayers = Audience.audience(event.getEntity().getLocation().getNearbyPlayers(MESSAGE_RANGE));
        nearbyPlayers.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                SMPRPG.getService(ChatService.class).getPlayerDisplay(player),
                ComponentUtils.SPACE,
                ComponentUtils.create("destroyed the crystals!", NamedTextColor.RED)
        ), NamedTextColor.DARK_RED));
        nearbyPlayers.playSound(Sound.sound().type(SoundEventKeys.BLOCK_BEACON_DEACTIVATE).volume(.5f).pitch(.5f).build());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void __onCrystal(PlayerInteractEvent event) {

        // Only listen if we are placing a crystal.
        if (!event.getMaterial().equals(Material.END_CRYSTAL))
            return;

        if (event.getClickedBlock() == null)
            return;

        if (!event.getClickedBlock().getType().equals(Material.BEDROCK))
            return;

        // Only listen if we are in a world that supports dragon battles.
        var battle = event.getPlayer().getWorld().getEnderDragonBattle();
        if (battle == null)
            return;

        var portal = battle.getEndPortalLocation();
        if (portal == null)
            return;

        // We need a summoning crystal. Not a normal one!
        var item = ItemService.blueprint(event.getItem());
        if (!(item instanceof CustomItemBlueprint custom)) {
            event.getPlayer().sendMessage(ComponentUtils.error("This isn't the right crystal! Try killing some endermen!"));
            event.setCancelled(true);
            return;
        }

        if (!custom.getCustomItemType().equals(CustomItemType.SUMMONING_CRYSTAL)) {
            event.getPlayer().sendMessage(ComponentUtils.error("This isn't the right crystal! Try killing some endermen!"));
            event.setCancelled(true);
            return;
        }

        // Don't listen unless we are able to place crystals.
        // In fact, cancel the event too just to prevent any weird behavior.
        if (battle.getRespawnPhase() != DragonBattle.RespawnPhase.NONE || battle.getEnderDragon() != null) {
            event.getPlayer().sendMessage(ComponentUtils.error("You cannot place this yet!"));
            event.setCancelled(true);
            return;
        }

        // Only check if this is a valid portal spot.
        var spot = event.getClickedBlock().getRelative(BlockFace.UP);
        if (!isCorner(portal, spot) && !isEdge(portal, spot)) {
            event.getPlayer().sendMessage(ComponentUtils.error("That's not where this goes!"));
            event.setCancelled(true);
            return;
        }

        // Make sure corners are filled first!
        if (!isCorner(portal, spot) && !areCornersFilledWithCrystals(portal)) {
            event.getPlayer().sendMessage(ComponentUtils.error("You need to fill in the corners first!"));
            event.setCancelled(true);
            return;
        }

        // Run a task on the next tick to find the crystal. If we don't find it, we can assume we haven't placed a crystal.
        Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), () -> {

            var targetBlock = event.getClickedBlock().getRelative(BlockFace.UP);
            var crystal = getCrystalOnBlock(targetBlock);
            if (crystal == null)
                return;

            // Already tracking this crystal?
            if (crystalPlacers.containsEntry(event.getPlayer().getUniqueId(), crystal))
                return;

            var playerName = SMPRPG.getService(ChatService.class).getPlayerDisplay(event.getPlayer());
            crystalPlacers.put(event.getPlayer().getUniqueId(), crystal);
            crystal.customName(playerName);
            crystal.setCustomNameVisible(true);
            crystal.addScoreboardTag("summoning_crystal");
            var total = crystalPlacers.values().size();
            var nearbyPlayers = Audience.audience(event.getClickedBlock().getLocation().getNearbyPlayers(MESSAGE_RANGE));

            nearbyPlayers.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                    playerName,
                    ComponentUtils.SPACE,
                    ComponentUtils.create("has placed a "),
                    ComponentUtils.create("Summoning Crystal", NamedTextColor.LIGHT_PURPLE),
                    ComponentUtils.create("! "),
                    ComponentUtils.create("[" + total + "/8]", NamedTextColor.GOLD)
            ), NamedTextColor.LIGHT_PURPLE));
            nearbyPlayers.playSound(Sound.sound()
                    .type(SoundEventKeys.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE)
                    .pitch(.5f)
                    .build());

            if (total != 8)
                return;

            // If this was the 8th crystal, the dragon is probably going to spawn...
            Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), () -> {
                nearbyPlayers.playSound(Sound.sound().type(SoundEventKeys.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM).pitch(.5f).build());
                nearbyPlayers.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                        ComponentUtils.create("ALL CRYSTALS PLACED!!!").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD),
                        ComponentUtils.SPACE,
                        ComponentUtils.encrypt(" Something is approaching.....", .5)
                ), NamedTextColor.LIGHT_PURPLE));
            }, TickTime.seconds(1));

        }, TickTime.INSTANTANEOUSLY);
    }

    /**
     * When a crystal does damage to a player, give them the special dragon effect as a little introduction to the mechanic.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onCrystalDamagePlayer(EntityDamageByEntityEvent event) {

        // Check if the entity causing damage is an end crystal.
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            return;

        if (!(event.getDamager() instanceof EnderCrystal crystal))
            return;

        // We use the scoreboard tag system only to know if the exploding entity is a summoning crystal.
        if (!crystal.getScoreboardTags().contains("summoning_crystal"))
            return;

        // Summoning crystal is dealing damage, let's turn it up a bit. Defense is applicable, so we can do quite a bit.
        event.setDamage(1000);

        // If it's a player, they disintegrate.
        if (event.getEntity() instanceof Player player)
            SMPRPG.getService(SpecialEffectService.class).giveEffect(player, new DisintegratingEffect(SMPRPG.getService(SpecialEffectService.class), player, DisintegratingEffect.SECONDS));
    }
}
