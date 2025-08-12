package xyz.devvydont.smprpg.items.blueprints.sets.neptune;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.Passive;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomShortbow;
import xyz.devvydont.smprpg.items.blueprints.sets.netherite.NetheriteBow;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.IPassiveProvider;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.crafting.builders.BowRecipe;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NeptuneBow extends CustomShortbow implements IBreakableEquipment, ICraftable, IPassiveProvider {

    @Override
    public boolean wantNerfedSellPrice() {
        return false;
    }

    public NeptuneBow(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, NetheriteBow.DAMAGE-10),
                AttributeEntry.multiplicative(AttributeWrapper.ATTACK_SPEED, -.5),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 25),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, 35)
        );
    }

    @Override
    public int getPowerRating() {
        return NeptuneArmorSet.POWER_LEVEL;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new BowRecipe(
                this,
                itemService.getCustomItem(CustomItemType.DIAMOND_TOOL_ROD),
                itemService.getCustomItem(CustomItemType.PLUTOS_ARTIFACT),
                generate()
        ).build();
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                itemService.getCustomItem(CustomItemType.PLUTO_FRAGMENT)
        );
    }

    /**
     * Retrieve the passives this item has.
     *
     * @return A set of passives.
     */
    @Override
    public Set<Passive> getPassives() {
        return Set.of(
                Passive.ABYSSAL_ANNIHILATION
        );
    }

    @Override
    public int getMaxDurability() {
        return NeptuneArmorSet.DURABILITY;
    }
}
