package xyz.devvydont.smprpg.services;

import io.papermc.paper.persistence.PersistentDataViewHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.base.LeveledEntity;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.entity.interfaces.IDamageTrackable;
import xyz.devvydont.smprpg.entity.player.LeveledPlayer;
import xyz.devvydont.smprpg.events.CustomChancedItemDropSuccessEvent;
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint;
import xyz.devvydont.smprpg.util.crafting.ItemUtil;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.DropFireworkTask;
import xyz.devvydont.smprpg.util.persistence.PDCAdapters;
import xyz.devvydont.smprpg.util.tasks.VoidProtectionTask;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

/**
 * In charge of managing item drops in the world.
 * - When a player dies, their items need to be protected and can only be picked up by them
 * - When an entity drops items, they drop items for whoever helped kill it so everyone gets items
 */
public class DropsService implements IService, Listener {

    public enum DropFlag {
        NULL,
        DEATH,
        LOOT,
        TELEKINESIS_FAIL
        ;

        public static DropFlag fromInt(int flag) {
            if (flag < 0 || flag > values().length)
                return NULL;
            return DropFlag.values()[flag];
        }
    }

    // How long items will last on the ground in seconds when marked as a drop.
    public static int COMMON_EXPIRE_SECONDS = 60 * 20;    // 20min
    public static int UNCOMMON_EXPIRE_SECONDS = 60 * 60;  // 1hr
    public static int RARE_EXPIRE_SECONDS = 60 * 60 * 2;  // 2hr
    public static int EPIC_EXPIRE_SECONDS = 60 * 60 * 4; // 4hr
    public static int LEGENDARY_EXPIRE_SECONDS = 60 * 60 * 12; // 12hr

    /*
     * Helper method to determine how long an item should last based on its rarity
     */
    public static long getMillisecondsUntilExpiry(ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON_EXPIRE_SECONDS;
            case UNCOMMON -> UNCOMMON_EXPIRE_SECONDS;
            case RARE -> RARE_EXPIRE_SECONDS;
            case EPIC -> EPIC_EXPIRE_SECONDS;
            default -> LEGENDARY_EXPIRE_SECONDS;
        } * 1000L;
    }

    // A list of drop announcements. We don't want to announce all drops at once.
    private final List<Runnable> dropAnnouncementQueue = new ArrayList<>();
    private BukkitRunnable dropAnnouncementTask;

    // The owner tag for a drop, drops cannot be picked up by players unless they own it
    private final NamespacedKey OWNER_UUID_KEY;
    private final NamespacedKey OWNER_NAME_KEY;

    // The flag that an owner tag drop has. 1 = Death, 2 = Drop
    private final NamespacedKey DROP_FLAG_KEY;

    // A timestamp on when this drop should expire from the world and disappear. Varying rarity items have different times.
    private final NamespacedKey DROP_EXPIRE_KEY;

    private final CopyOnWriteArrayList<Item> expiredItemQueue = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<Item> itemsToUpdateQueue = new CopyOnWriteArrayList<>();

    // A task that cleans up items.
    private BukkitRunnable itemCleanupTask = null;

    // A task that runs every second to check if an item should expire or not.
    private BukkitRunnable itemTimerTask = null;

    public static final Map<ItemRarity, Team> rarityToTeam = new HashMap<>();

    public DropsService() {
        var plugin = SMPRPG.getInstance();
        this.OWNER_UUID_KEY = new NamespacedKey(plugin, "drop-owner-uuid");
        this.OWNER_NAME_KEY = new NamespacedKey(plugin, "drop-owner-name");
        this.DROP_FLAG_KEY = new NamespacedKey(plugin, "drop-flag");
        this.DROP_EXPIRE_KEY = new NamespacedKey(plugin, "expiry");

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        rarityToTeam.clear();
        for (ItemRarity rarity : ItemRarity.values()) {
            Team team;
            if (scoreboard.getTeam(rarity.name()) != null)
                team = scoreboard.getTeam(rarity.name());
            else
                team = scoreboard.registerNewTeam(rarity.name());

            assert team != null;
            team.color(rarity.color);
            rarityToTeam.put(rarity, team);
        }
    }

    public Team getTeam(ItemRarity rarity) {
        return rarityToTeam.get(rarity);
    }

    public NamespacedKey getItemOwnerKey() {
        return OWNER_UUID_KEY;
    }

    public NamespacedKey getDropFlagKey() {
        return DROP_FLAG_KEY;
    }

    /**
     * Adds the necessary flags to this item that makes it behave like standard loot, with delayed item deletion and
     * loot drop owning.
     * @param item The item to tag.
     * @param owner The owner of the item.
     */
    public void addDefaultLootFlags(ItemStack item, Player owner) {
        if (item == null || item.getType() == Material.AIR)
            return;
        var meta = item.getItemMeta();
        var blueprint = SMPRPG.getService(ItemService.class).getBlueprint(item);
        setOwner(meta, owner);
        setFlag(meta, DropFlag.LOOT);
        setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(blueprint.getRarity(item)));
        item.setItemMeta(meta);
    }

    /**
     * Transfers loot flags from an item to an item entity that holds the item.
     * This is useful as we want to hold loot data for as little time as possible on ItemStack instances, but it's fine
     * to keep them on Item entities. With it set up this way, we can read owner/loot data from item entities so
     * that we don't mess with any item stacking behavior on pickup.
     * @param item The item to transfer tags to. The item stack on the entity will be used.
     */
    public void transferLootFlags(Item item) {

        // Transfer owner.
        removeOwner(item);
        item.setOwner(null);
        var owner = getOwner(item.getItemStack());
        if (owner != null) {
            var player = Bukkit.getPlayer(owner);
            if (player != null) {
                setOwner(item, player);
                item.setOwner(owner);
                item.setCanMobPickup(false);
            }
        }

        var timestamp = getExpiryTimestamp(item.getItemStack());
        if (timestamp != 0) {
            setExpiryTimestamp(item, timestamp);
            item.setUnlimitedLifetime(true);  // Expiry set items expire when we tell them to.
        }

        var flag = getFlag(item.getItemStack());
        if (flag != DropFlag.NULL) {
            setFlag(item, flag);
            item.setInvulnerable(true);  // Loot tagged items cannot die.
        }

        // Wipe item stack data.
        removeAllTags(item.getItemStack());
    }

    /**
     * Adds the necessary flags to this item that makes it behave like standard death drops, with delayed item deletion and
     * loot drop owning.
     * @param item The item to tag.
     * @param player The owner of the item.
     */
    public void addDefaultDeathFlags(ItemStack item, Player player) {
        if (item == null || item.getType() == Material.AIR)
            return;
        var meta = item.getItemMeta();
        var blueprint = SMPRPG.getService(ItemService.class).getBlueprint(item);
        setOwner(meta, player);
        setFlag(meta, DropFlag.DEATH);
        setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(blueprint.getRarity(item)));
        item.setItemMeta(meta);
    }

    /**
     * Marks this PDC as owned by a player
     *
     * @param holder
     * @param player
     */
    public void setOwner(PersistentDataHolder holder, Player player) {
        holder.getPersistentDataContainer().set(OWNER_NAME_KEY, PersistentDataType.STRING, player.getName());
        holder.getPersistentDataContainer().set(getItemOwnerKey(), PDCAdapters.UUID, player.getUniqueId());
    }

    /**
     * Untags this item as being owned by someone
     *
     * @param holder
     */
    public void removeOwner(PersistentDataHolder holder) {
        holder.getPersistentDataContainer().remove(OWNER_NAME_KEY);
        holder.getPersistentDataContainer().remove(getItemOwnerKey());
    }

    /**
     * Gets the UUID of the owner of this PDC. If no owner, null is returned
     *
     * @param holder
     * @return
     */
    @Nullable
    public UUID getOwner(PersistentDataViewHolder holder) {
        return holder.getPersistentDataContainer().get(getItemOwnerKey(), PDCAdapters.UUID);
    }

    /**
     * Gets the String name of the owner of this PDC. If no owner, null is returned
     *
     * @param holder
     * @return
     */
    @Nullable
    public String getOwnerName(PersistentDataViewHolder holder) {
        return holder.getPersistentDataContainer().get(OWNER_NAME_KEY, PersistentDataType.STRING);
    }

    /**
     * Determines if this PDC contains the owner field
     *
     * @param holder
     * @return
     */
    public boolean hasOwner(PersistentDataViewHolder holder) {
        return holder.getPersistentDataContainer().has(getItemOwnerKey());
    }

    public DropFlag getFlag(PersistentDataViewHolder holder) {
        int rawFlag = holder.getPersistentDataContainer().getOrDefault(getDropFlagKey(), PersistentDataType.INTEGER, 0);
        return DropFlag.fromInt(rawFlag);
    }

    public void setFlag(PersistentDataHolder holder, DropFlag flag) {
        holder.getPersistentDataContainer().set(getDropFlagKey(), PersistentDataType.INTEGER, flag.ordinal());
    }

    /*
     * Checks if a PDC has an expiry timestamp set.
     */
    public boolean hasExpiryTimestamp(PersistentDataViewHolder holder) {
        return holder.getPersistentDataContainer().has(DROP_EXPIRE_KEY, PersistentDataType.LONG);
    }

    /*
     * Gets the expiry timestamp set on an item using System.currentTimeMillis()
     */
    public long getExpiryTimestamp(PersistentDataViewHolder holder) {
        return holder.getPersistentDataContainer().getOrDefault(DROP_EXPIRE_KEY, PersistentDataType.LONG, 0L);
    }

    /*
     * Flags a PDC holder to have an expiry timestamp at a certain timestamp using System.currentTimeMillis()
     */
    public void setExpiryTimestamp(PersistentDataHolder holder, long timestamp) {
        holder.getPersistentDataContainer().set(DROP_EXPIRE_KEY, PersistentDataType.LONG, timestamp);
    }

    /*
     * Removes the expiry field from an item. It is not needed anymore once it is picked up by a player.
     */
    public void removeExpiryTimestamp(PersistentDataHolder holder) {
        holder.getPersistentDataContainer().remove(DROP_EXPIRE_KEY);
    }

    public void removeFlag(PersistentDataHolder holder) {
        holder.getPersistentDataContainer().remove(getDropFlagKey());
    }

    public void removeAllTags(PersistentDataHolder holder) {
        removeOwner(holder);
        removeFlag(holder);
        removeExpiryTimestamp(holder);
    }

    public void removeAllTags(ItemStack itemStack) {
        itemStack.editMeta(this::removeAllTags);
    }

    private String stringifyTime(long seconds) {

        if (seconds < 60)
            return seconds + "s";

        if (seconds < 3600)
            return seconds / 60 + "m";

        return seconds / 3600 + "h";
    }

    public static List<Item> getAllLoadedItems() {
        var items = new ArrayList<Item>();
        for (var world : Bukkit.getWorlds())
            items.addAll(world.getEntitiesByClass(Item.class));
        return items;
    }

    @Override
    public void setup() throws RuntimeException {
        var plugin = SMPRPG.getInstance();

        // Make a task that will gradually pop off drop announcements in the drop queue.
        dropAnnouncementTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Is there an announcement in queue?
                if (dropAnnouncementQueue.isEmpty())
                    return;

                // Pop off the next item and run it.
                var task = dropAnnouncementQueue.getFirst();
                task.run();
                dropAnnouncementQueue.removeFirst();
            }
        };
        dropAnnouncementTask.runTaskTimerAsynchronously(plugin, TickTime.INSTANTANEOUSLY, TickTime.HALF_SECOND);

        // A synchronous cleanup job for items that are considered expired. Our async task is in charge of populating
        // the list of stale items for us. The reason for this is so we can async observe items without lagging the TPS.
        itemCleanupTask = new BukkitRunnable() {
            @Override
            public void run() {

                // Update any items that were flagged as needing it. We should only be allowed to do this ~1,000 times
                // on a single tick, so hard cap the amount of items we do.
                var toProcess = new ArrayList<>(itemsToUpdateQueue);
                if (toProcess.size() > 1000) {
                    toProcess = new ArrayList<>(toProcess.subList(0, 1000));
                    itemsToUpdateQueue = new CopyOnWriteArrayList<>(itemsToUpdateQueue.subList(1000, itemsToUpdateQueue.size()));
                } else {
                    itemsToUpdateQueue.clear();
                }

                for (var item : toProcess) {
                    // Common items don't display a tag.
                    if (ItemService.blueprint(item.getItemStack()).getRarity(item.getItemStack()).equals(ItemRarity.COMMON)) {
                        item.setCustomNameVisible(false);
                        continue;
                    }

                    var itemName = generateItemName(item);
                    item.customName(itemName);
                }

                // Simple. Delete any items in the queue.
                for (var item : expiredItemQueue.stream().toList()) {
                    // We need to try except this because it can fail. It's not a serious issue though.
                    try {
                        item.remove();
                        expiredItemQueue.remove(item);
                    } catch (Exception e) {
                        SMPRPG.getInstance().getLogger().warning(e.getMessage());
                    }
                }
            }
        };
        itemCleanupTask.runTaskTimer(SMPRPG.getInstance(), 0, TickTime.TICK);

        // Make a task that will slowly decrement items on the ground so they despawn eventually.
        itemTimerTask = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();

                // We are about to refresh the queue so clear it.
                expiredItemQueue.clear();
                itemsToUpdateQueue.clear();


                List<Item> entities = null;
                try {
                    entities = Bukkit.getScheduler().callSyncMethod(SMPRPG.getInstance(), DropsService::getAllLoadedItems).get();
                } catch (InterruptedException e) {
                    SMPRPG.getInstance().getLogger().warning("Item cleanup query task was interrupted. " + e.getMessage());
                } catch (ExecutionException e) {
                    SMPRPG.getInstance().getLogger().warning("Item cleanup query task ran into an error. " + e.getMessage());
                }
                if (entities == null)
                    return;

                // Loop through every item loaded on the server currently.
                for (var item : entities) {

                        // If this item doesn't have the expiry tag, we can't do anything with it
                        if (!hasExpiryTimestamp(item))
                            continue;

                        // This item has an expiry time, see if it has expired, if it has then removed it
                        long expiresAt = getExpiryTimestamp(item);
                        if (expiresAt < now) {
                            expiredItemQueue.add(item);
                            continue;
                        }
                        itemsToUpdateQueue.add(item);
                }

            }
        };

        itemTimerTask.runTaskTimerAsynchronously(plugin, 0, TickTime.seconds(1));

        new VoidProtectionTask().runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.TICK);
    }

    @Override
    public void cleanup() {
        if (itemTimerTask != null)
            itemTimerTask.cancel();

        if (dropAnnouncementTask != null)
            dropAnnouncementTask.cancel();

        if (itemCleanupTask != null)
            itemCleanupTask.cancel();

        itemTimerTask = null;
        dropAnnouncementTask = null;
        itemCleanupTask = null;
    }

    /**
     * Generates the name component for an item for how it should display as a nametag over an item entity.
     * The difference with this and normal item names, is that this includes stack size.
     * @param item The item.
     * @return A reusable component.
     */
    private Component generateItemName(Item item) {


        // Time left?
        var timeLeftComponent = ComponentUtils.EMPTY;
        if (hasExpiryTimestamp(item)) {
            var now = System.currentTimeMillis();
            long expiresAt = getExpiryTimestamp(item);
            var secLeft = (expiresAt - now) / 1000;
            var timeleft = stringifyTime(secLeft);
            timeLeftComponent = ComponentUtils.create(" (" + timeleft + ") ", secLeft <= 300 ? NamedTextColor.RED : NamedTextColor.DARK_GRAY);
        }

        var blueprint = ItemService.blueprint(item.getItemStack());

        // Multiple items?
        var stack = ComponentUtils.create(item.getItemStack().getAmount() + "x", NamedTextColor.GRAY);
        if (item.getItemStack().getAmount() == 1)
            stack = ComponentUtils.EMPTY;

        // Owner?
        var owner = getOwnerName(item);
        var ownerComponent = owner != null ? ComponentUtils.create(String.format(" (%s)", owner), NamedTextColor.DARK_GRAY) : ComponentUtils.EMPTY;

        return ComponentUtils.merge(
                timeLeftComponent,
                stack,
                blueprint.getNameComponent(item.getItemStack()),
                ownerComponent
        );
    }

    /**
     * When players roll for a drop, consider their luck stat as a factor for an item
     *
     * @param event
     */
    @EventHandler
    private void __onConsiderLuckRollForGear(CustomItemDropRollEvent event) {

        var luck = AttributeService.getInstance().getAttribute(event.getPlayer(), AttributeWrapper.LUCK);

        if (luck == null)
            return;

        // Divide the luck in such a way that 100 luck means there's no effect.
        var multiplier = luck.getValue() / 100.0;
        event.setChance(event.getChance() * multiplier);
    }

    /**
     * When a player dies, mark all their items as being owned by them
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void __onPlayerDeath(PlayerDeathEvent event) {

        // Don't lose any levels.
        event.setKeepLevel(true);
        event.setDroppedExp(0);

        // Go through all the drops on the player and tag it as being owned
        for (ItemStack drop : event.getDrops()) {
            drop.editMeta(meta -> {
                ItemRarity rarity = SMPRPG.getService(ItemService.class).getBlueprint(drop).getRarity(drop);
                setOwner(meta, event.getPlayer());
                setFlag(meta, DropFlag.DEATH);
                setExpiryTimestamp(meta, System.currentTimeMillis() + getMillisecondsUntilExpiry(rarity));
            });
        }
    }

    /**
     * When an item spawns into the world, check if it is owned by someone
     * and transfer the PDC value over to the item entity
     * Also add rarity glow to the item
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void __onItemSpawn(ItemSpawnEvent event) {

        // Right away, transfer loot flags to the item entity.
        transferLootFlags(event.getEntity());

        // Set the rarity glow of the item
        event.getEntity().setGlowing(true);
        var item = event.getEntity().getItemStack();
        var rarity = SMPRPG.getService(ItemService.class).getBlueprint(item).getRarity(item);
        getTeam(rarity).addEntity(event.getEntity());

        // Items with enchantments cannot die.
        if (!item.getEnchantments().isEmpty())
            event.getEntity().setInvulnerable(true);

        // Rare+ items cannot die.
        if (rarity.ordinal() >= ItemRarity.RARE.ordinal())
            event.getEntity().setInvulnerable(true);

        var name = generateItemName(event.getEntity());

        var nameVisible = rarity.ordinal() >= ItemRarity.UNCOMMON.ordinal();
        if (nameVisible)
            event.getEntity().customName(name);
        event.getEntity().setCustomNameVisible(nameVisible);

        // If this is a drop and the rarity is above rare, add the firework task
        if (getFlag(event.getEntity()).equals(DropFlag.LOOT) && rarity.ordinal() >= ItemRarity.RARE.ordinal())
            DropFireworkTask.start(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void __onFireworkDamageFromDrop(EntityDamageEvent event) {

        // We only care about fireworks
        if (!event.getDamageSource().getDamageType().equals(DamageType.FIREWORKS))
            return;

        Firework firework = (Firework) event.getDamageSource().getDirectEntity();
        if (firework == null)
            return;

        // Custom fireworks don't do damage
        if (firework.getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
            return;

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, 0);
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void __onEntityHasDrops(EntityDeathEvent event) {

        LeveledEntity<?> entity = SMPRPG.getService(EntityService.class).getEntityInstance(event.getEntity());

        // Clear the drops from the vanilla roll if desired
        if (!entity.hasVanillaDrops())
            event.getDrops().clear();

        // Set experience dropped to the level of the entity if it is not a player
        if (!(entity instanceof LeveledPlayer))
            event.setDroppedExp(entity.getMinecraftExperienceDropped());

        // Drop override?
        if (entity.getItemDrops() == null)
            return;

        // Is there a killer involved?
        Player killer = event.getEntity().getKiller();
        if (killer == null)
            return;

        // Loop through all players that helped kill this entity and did at least some meaningful damage
        Map<Player, Double> involvedPlayers = new HashMap<>();
        involvedPlayers.put(killer, 1.0);  // Ensure killer at least gets credit for the kill

        // If this is a sea creature, the person who spawned it in should get credit no matter what.
        if (entity instanceof SeaCreature<?> seaCreature && seaCreature.getSpawnedBy() != null) {
            var spawner = Bukkit.getPlayer(seaCreature.getSpawnedBy());
            if (spawner != null)
                involvedPlayers.put(spawner, 1.0);
        }

        // If this entity has a damage map go through all participants and add them to the involved players
        if (entity instanceof IDamageTrackable trackable)
            for (var entry : trackable.getDamageTracker().getPlayerDamageTracker().entrySet())
                // Add this player damage to max hp ratio
                involvedPlayers.put(entry.getKey(), Math.min(entry.getValue() / entity.getMaxHp(), 1.0));

        // Loop through every involved player
        for (Map.Entry<Player, Double> entry : involvedPlayers.entrySet()) {

            Player player = entry.getKey();
            Double damageRatio = entry.getValue();

            // If an entity does at least some % damage to an entity, they should get full credit for drops
            damageRatio /= entity.getDamageRatioRequirement();
            damageRatio = Math.min(damageRatio, 1.0);

            // Now test for coins
            // Some chance to add more money
            if (Math.random() < .2) {
                var moneyItem = ItemUtil.getOptimalCoinStacks(SMPRPG.getService(ItemService.class), (int) (entity.getLevel() * 3 * (Math.random() * 3)));
                for (var money : moneyItem)
                    addDefaultLootFlags(money, player);
                event.getDrops().addAll(moneyItem);
            }

            // Loop through all the droppable items from the entity
            for (var drop : entity.getItemDrops()) {

                List<ItemStack> allInvolvedPlayersDrops = new ArrayList<>();

                // Test for items to drop
                Collection<ItemStack> roll = drop.roll(player, player.getInventory().getItemInMainHand(), damageRatio);
                if (roll != null)
                    allInvolvedPlayersDrops.addAll(roll);

                // If we didn't roll anything skip
                if (allInvolvedPlayersDrops.isEmpty())
                    continue;

                // Tag all the drops as loot drops
                for (ItemStack item : allInvolvedPlayersDrops)
                    addDefaultLootFlags(item, player);

                // Extend the list of items
                event.getDrops().addAll(allInvolvedPlayersDrops);
            }


        }

    }

    /*
     * When a player breaks a block and causes items to drop, mark it as loot for the player so they own it.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void __onBlockDroppedItemEvent(BlockDropItemEvent event) {

        // Tag all the drops as loot drops. Since we are given item entities, we need to .
        for (Item itemEntity : event.getItems()) {
            ItemStack item = itemEntity.getItemStack();
            SMPRPG.getService(ItemService.class).ensureItemStackUpdated(item);
            addDefaultLootFlags(item, event.getPlayer());
            transferLootFlags(itemEntity);
            itemEntity.customName(generateItemName(itemEntity));
        }
    }

    @EventHandler
    private void __onRareDropObtained(CustomChancedItemDropSuccessEvent event) {

        // Find out information about the item.
        SMPItemBlueprint blueprint = SMPRPG.getService(ItemService.class).getBlueprint(event.getItem());
        ItemRarity rarityOfDrop = blueprint.getRarity(event.getItem());

        // Start construction of the message.
        Component prefix = ComponentUtils.alert(
                ComponentUtils.create(rarityOfDrop.name() + " DROP!!! ", rarityOfDrop.color, TextDecoration.BOLD),
                NamedTextColor.YELLOW
        );
        Component player = SMPRPG.getService(ChatService.class).getPlayerDisplay(event.getPlayer());
        Component item = event.getItem().displayName().hoverEvent(event.getItem().asHoverEvent());
        Component suffix = ComponentUtils.create(" found ").append(item).append(ComponentUtils.create(" from ")).append(event.getSource().getAsComponent()).append(ComponentUtils.create("!"));
        Component chance = ComponentUtils.create(" (" + event.getFormattedChance() + ")", NamedTextColor.DARK_GRAY);

        // Should we tell the entire server this drop happened? Legendary always gets announced, epic only if under 5%.
        boolean broadcastServer = rarityOfDrop.ordinal() >= ItemRarity.LEGENDARY.ordinal();
        if (event.getChance() <= .05 && rarityOfDrop.equals(ItemRarity.EPIC))
            broadcastServer = true;

        // We have 3 levels of "rare drop" obtaining based on the chance.
        // If the drop is worth broadcasting to the entire server...
        if (broadcastServer) {
            Component message = prefix.append(player).append(suffix).append(chance);

            // Queue the announcement.
            dropAnnouncementQueue.add(() -> {
                for (Player p : Bukkit.getOnlinePlayers())
                    p.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
                Bukkit.broadcast(message);
            });
            return;
        }

        boolean tellPlayer = rarityOfDrop.ordinal() >= ItemRarity.RARE.ordinal();
        if (event.getChance() < rarityOfDrop.ordinal() * rarityOfDrop.ordinal() / 25.0)
            tellPlayer = true;

        if (!tellPlayer)
            return;

        // Just show the message to the player since it's not THAT crazy. We should do this a bit later tho...
        Component message = prefix.append(ComponentUtils.create("You")).append(suffix).append(chance);
        Bukkit.getScheduler().runTaskLater(SMPRPG.getInstance(), () -> {
            event.getPlayer().sendMessage(message);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
        }, TickTime.TICK * 5);
    }

    /**
     * When something picks up a drop, and it is marked as owned by someone, don't let it be picked up. Unless of course
     * they own the drop.
     */
    @EventHandler
    private void __onEntityPickupItem(EntityPickupItemEvent event) {

        // Never allow enemies to pickup items.
        if (event.getEntity() instanceof Enemy) {
            event.setCancelled(true);
            return;
        }

        var owner = getOwner(event.getEntity());
        // No owner? don't do anything.
        if (owner == null)
            return;

        // Entity owns the item? Don't do anything.
        if (owner.equals(event.getEntity().getUniqueId()))
            return;

        // Trying to pick up an item we don't own.
        event.setCancelled(true);
    }

    /**
     * Prevent hopper like blocks from picking up loot tagged items.
     * This is to prevent players being able to pick up other players drops by using redstone blocks.
     */
    @EventHandler
    private void __onHopperTaggedItem(InventoryPickupItemEvent event) {

        if (getOwner(event.getItem()) != null)
            event.setCancelled(true);

        if (event.getItem().getOwner() != null)
            event.setCancelled(true);

    }

    /**
     * Don't allow items to move through dimensions if tagged.
     */
    @EventHandler
    public void __onItemAttemptDimensionTransition(EntityPortalEnterEvent event) {

        if (!(event.getEntity() instanceof Item item))
            return;

        if (getOwner(item) != null || item.getOwner() != null)
            event.setCancelled(true);
    }

}
