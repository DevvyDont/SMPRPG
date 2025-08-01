package xyz.devvydont.smprpg.skills.listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.enchantments.CustomEnchantment;
import xyz.devvydont.smprpg.entity.player.LeveledPlayer;
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent;
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemEnchantedBook;
import xyz.devvydont.smprpg.items.interfaces.IAttributeItem;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;

public class MagicExperienceListener implements Listener {

    final SMPRPG plugin;

    public static final int BREWING_OUTPUT_LEFT = 0;
    public static final int BREWING_OUTPUT_MIDDLE = 1;
    public static final int BREWING_OUTPUT_RIGHT = 2;

    private NamespacedKey experienceStowKey = null;

    public MagicExperienceListener(SMPRPG plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private NamespacedKey getExperienceStowKey() {

        if (experienceStowKey == null)
            experienceStowKey = new NamespacedKey(plugin, "stowed_exp");

        return experienceStowKey;
    }

    private boolean isBrewingOutputSlot(int slot) {
        return slot <= BREWING_OUTPUT_RIGHT && slot >= BREWING_OUTPUT_LEFT;
    }

    private int getExperienceForPotionExtract(ItemStack item) {

        if (!(item.getItemMeta() instanceof PotionMeta meta))
            return 0;

        int exp = 0;


        // Find a potion type addition
        switch (item.getType()) {
            case LINGERING_POTION -> exp += 25;
            case SPLASH_POTION -> exp += 10;
        }

        // Find a potion effect addition
        if (meta.getBasePotionType() != null) {
            switch (meta.getBasePotionType()) {
                case WATER:
                    exp += 1;
                    break;

                case MUNDANE, AWKWARD, THICK:
                    exp += 2;
                    break;
            }
        }

        // Consider the base potion effect
        if (meta.getBasePotionType() != null)
            for (PotionEffect effect : meta.getBasePotionType().getPotionEffects())
                exp += ((effect.getAmplifier()+1) * effect.getDuration() / 25);

        // Consider the extra potion effects
        for (PotionEffect effect : meta.getCustomEffects())
            exp += ((effect.getAmplifier()+1) * effect.getDuration() / 50);

        return exp;
    }

    /**
     * Given any item stack, stows experience on it so that when someone picks it up from the brewing stand it will
     * award experience. Also supports stacking experience from previous brews
     *
     * @param item
     */
    private void stowExperience(ItemStack item, int addition) {
        item.editMeta(meta -> {
            int oldXp = meta.getPersistentDataContainer().getOrDefault(getExperienceStowKey(), PersistentDataType.INTEGER, 0);
            meta.getPersistentDataContainer().set(getExperienceStowKey(), PersistentDataType.INTEGER, oldXp + addition);
        });
    }

    private void stowPotionExperience(ItemStack item) {
        stowExperience(item, getExperienceForPotionExtract(item));
    }

    /**
     * Awards experience to a player from stowed experience on an itemstack and resets the stowed xp to 0
     *
     * @param player
     * @param item
     */
    private void awardExperience(LeveledPlayer player, ItemStack item, SkillExperienceGainEvent.ExperienceSource source) {

        // Extract the experience from the item
        int exp = item.getPersistentDataContainer().getOrDefault(getExperienceStowKey(), PersistentDataType.INTEGER, 0);

        // Remove the stored experience on the item
        item.editMeta(meta -> {
            meta.getPersistentDataContainer().remove(getExperienceStowKey());
        });

        // Award!
        if (exp > 0)
            player.getMagicSkill().addExperience(exp, source);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnchant(EnchantItemEvent event) {

        if (event.isCancelled())
            return;

        // Set a base experience earning for this enchant. At the start it is just the level of enchant we are performing.
        int exp = event.getExpLevelCost() + 10;

        // Loop through every enchant and see how much magic experience it gives.
        for (CustomEnchantment enchantment : SMPRPG.getService(EnchantmentService.class).getCustomEnchantments(event.getEnchantsToAdd()))
            exp += enchantment.getMagicExperience();

        // Magic multiplier if the item has a power rating
        double multiplier = 1.0;
        SMPItemBlueprint blueprint = SMPRPG.getService(ItemService.class).getBlueprint(event.getItem());
        if (blueprint instanceof IAttributeItem attributeable)
            multiplier += attributeable.getPowerRating() / 20.0;

        var player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getEnchanter());
        player.getMagicSkill().addExperience((int) (exp*multiplier), SkillExperienceGainEvent.ExperienceSource.ENCHANT);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBrewEvent(BrewEvent event) {

        if (event.isCancelled())
            return;

        for (ItemStack result : event.getResults())
            stowPotionExperience(result);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBrewExtract(InventoryClickEvent event) {

        if (event.getClickedInventory() == null)
            return;

        // Only listen to brewing inventories and anvil inventories
        if (!event.getClickedInventory().getType().equals(InventoryType.BREWING))
            return;

        BrewerInventory brewerInventory = (BrewerInventory) event.getClickedInventory();

        // Only listen to output slot clicks
        if (!isBrewingOutputSlot(event.getSlot()))
            return;

        // Evaluate the item we are extracting, if it isn't there then ignore
        ItemStack extracted = brewerInventory.getItem(event.getSlot());
        if (extracted == null || extracted.getType().equals(Material.AIR))
            return;

        // Determine experience
        LeveledPlayer player = SMPRPG.getService(EntityService.class).getPlayerInstance((Player) event.getWhoClicked());
        awardExperience(player, extracted, SkillExperienceGainEvent.ExperienceSource.BREW);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAnvilExtract(InventoryClickEvent event) {

        // If we aren't clicking in an anvil we don't care
        if (!(event.getClickedInventory() instanceof AnvilInventory anvil))
            return;

        // If the result is not real we don't care
        if (anvil.getResult() == null)
            return;

        // If we aren't clicking on the output slot we don't care
        if (!event.getSlotType().equals(InventoryType.SlotType.RESULT))
            return;

        // Evaluate the item we are extracting, if it isn't there then ignore
        ItemStack extracted = anvil.getResult();
        if (extracted == null || extracted.getType().equals(Material.AIR))
            return;

        // Determine experience
        LeveledPlayer player = SMPRPG.getService(EntityService.class).getPlayerInstance((Player) event.getWhoClicked());
        awardExperience(player, extracted, SkillExperienceGainEvent.ExperienceSource.FORGE);
    }
    /**
     * Add magic experience to items in the anvil.
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAnvilPrepare(PrepareAnvilEvent event) {

        if (event.getResult() == null)
            return;

        // If the repair cost is too high for the player to forge it don't stow exp
        if (event.getView().getRepairCost() > ((Player)event.getView().getPlayer()).getLevel())
            return;

        ItemStack result = event.getResult();

        SMPItemBlueprint blueprint = SMPRPG.getService(ItemService.class).getBlueprint(result);
        int multiplier = 1;
        if (blueprint instanceof IAttributeItem attributeable)
            multiplier = attributeable.getPowerRating();
        else if (blueprint instanceof ItemEnchantedBook book && book.getEnchantment(result) != null)
            multiplier = book.getRarity(result).ordinal() + 3;

        int exp = event.getView().getRepairCost() * multiplier;

        // Something isn't right if we have this much....
        if (exp > 5000)
            exp = 5000;

        stowExperience(result, exp);
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupExperience(PlayerPickupExperienceEvent event) {
        int exp = Math.max(1, event.getExperienceOrb().getExperience() / 10);
        LeveledPlayer player = SMPRPG.getService(EntityService.class).getPlayerInstance(event.getPlayer());
        player.getMagicSkill().addExperience(exp, SkillExperienceGainEvent.ExperienceSource.XP);
    }


}
