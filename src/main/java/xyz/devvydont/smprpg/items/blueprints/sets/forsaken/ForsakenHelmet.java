package xyz.devvydont.smprpg.items.blueprints.sets.forsaken;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.interfaces.ITrimmable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.HelmetRecipe;

public class ForsakenHelmet extends ForsakenArmorSet implements ITrimmable {

    public static final int DEFENSE = 110;
    public static final int HEALTH = 15;
    public static final double STRENGTH = .3;

    public ForsakenHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getDefense() {
        return DEFENSE;
    }

    @Override
    public int getHealth() {
        return HEALTH;
    }

    @Override
    public double getStrength() {
        return STRENGTH;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new HelmetRecipe(this, ItemService.generate(ForsakenHelmet.CRAFTING_COMPONENT), generate()).build();
    }

    @Override
    public TrimMaterial getTrimMaterial() {
        return TrimMaterial.EMERALD;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.SPIRE;
    }
}
