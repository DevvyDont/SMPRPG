package xyz.devvydont.smprpg.enchantments;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.services.EntityService;

public abstract class CustomEnchantment implements Cloneable {

    public static final int UNAPPLIED = -1;
    private boolean bootstrapped = false;

    public boolean isBootstrapped() {
        return bootstrapped;
    }

    private final String id;
    private TypedKey<Enchantment> typedKey;
    private Key key = null;

    private int level = UNAPPLIED;

    public CustomEnchantment(String id) {
        this.id = id;
    }

    public CustomEnchantment build(int level) {

        CustomEnchantment copy;

        try {
             copy = (CustomEnchantment) clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Enchantment " + id + " cannot be cloned", e);
        }

        copy.setLevel(level);
        return copy;
    }

    public void bootstrapCompleted() {
//        System.out.println("Successfully bootstrapped " + getClass().getName() + " enchantment");
        bootstrapped = true;
    }

    public void bootstrap(BootstrapContext context) {

//        System.out.println("Attempting to bootstrap " + getClass().getName() + " enchantment");

        if (isBootstrapped())
            throw new IllegalStateException("Enchantment " + getClass().getName() + " is already bootstrapped!");

        setTypedKey(TypedKey.create(RegistryKey.ENCHANTMENT, getKey()));
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> {
            event.registry().register(
                    getTypedKey(),
                    b -> b.description(getDisplayName())
                            .primaryItems(getSupportedItems(event))
                            .supportedItems(getSupportedItems(event))
                            .anvilCost(getAnvilCost())
                            .maxLevel(getMaxLevel())
                            .weight(getWeight())
                            .minimumCost(getMinimumCost())
                            .maximumCost(getMaximumCost())
                            .activeSlots(getEquipmentSlotGroup())
                            .exclusiveWith(getConflictingEnchantments())
            );
        }));

        bootstrapCompleted();
    }

    /**
     * A set of enchantments that this enchantment conflicts with.
     * If there are none, this enchantment has no conflicts
     *
     * @return
     */
    @NotNull
    public RegistryKeySet<@NotNull Enchantment> getConflictingEnchantments() {
        return RegistrySet.keySet(RegistryKey.ENCHANTMENT);
    }

    public String getId() {
        return id;
    }

    public Key getKey() {

        if (key == null)
            key = Key.key("smprpg", id);

        return key;
    }

    public TypedKey<Enchantment> getTypedKey() {
        return typedKey;
    }

    public void setTypedKey(TypedKey<Enchantment> typedKey) {
        this.typedKey = typedKey;
    }

    public abstract @NotNull Component getDisplayName();

    public @NotNull TextColor getEnchantColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

    public abstract @NotNull Component getDescription();

    public @NotNull RegistryKeySet<@NotNull ItemType> getSupportedItems(RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.@NotNull Builder> event) {
        return event.getOrCreateTag(getItemTypeTag());
    }

    public abstract TagKey<ItemType> getItemTypeTag();

    public abstract int getAnvilCost();

    public void setKey(Key key) {
        this.key = key;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public abstract int getMaxLevel();

    public abstract int getWeight();

    public @NotNull EnchantmentRegistryEntry.EnchantmentCost getMinimumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
    }

    public @NotNull EnchantmentRegistryEntry.EnchantmentCost getMaximumCost() {
        return EnchantmentRegistryEntry.EnchantmentCost.of(1, 1);
    }

    public abstract EquipmentSlotGroup getEquipmentSlotGroup();

    public abstract int getSkillRequirement();

    /**
     * Checks the requirement of the enchantment for a specific level of the enchantment.
     * @param level
     * @return
     */
    public int getSkillRequirementForLevel(int level) {
        double percentage = (double) (level-1) / getMaxLevel();
        int enchantLevelRequirement = (int) (percentage * (100-getSkillRequirement()) + getSkillRequirement());
        return Math.min(100, enchantLevelRequirement);
    }

    /**
     * The skill level required to stop rolling this enchantment. This is mainly used so that players can stop
     * rolling curse enchantments on gear at a certain level
     *
     * @return
     */
    public int getSkillRequirementToAvoid() {
        return Integer.MAX_VALUE;
    }

    public Enchantment getEnchantment() {
        return SMPRPG.getService(EnchantmentService.class).getEnchantment(getTypedKey());
    }

    public int getMagicExperience() {
        // 100-1000 XP for level bonus
        int levelBonus = (int) (getLevel()/(double)getMaxLevel() * 900 + 100);
        // Rarity multiplier, rarer enchants provide better bonuses
        // can get 100-1000 xp for this as well
        int rarityBonus = (int) (1.0 / (getWeight() + 1.0)) * 900 + 100;

        return levelBonus + rarityBonus;
    }

    @Override
    public String toString() {
        return getKey().toString() + " " + getLevel();
    }
}
