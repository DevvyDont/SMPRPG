package xyz.devvydont.smprpg.items.blueprints.sets.radiant;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.meta.trim.TrimPattern;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.HelmetRecipe;

public class RadiantHelmet extends RadiantArmorSet {

    public RadiantHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public double getHealth() {
        return 15;
    }

    @Override
    public double getDefense() {
        return ItemArmor.getDefenseFromMaterial(Material.NETHERITE_HELMET)-10;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.HELMET;
    }

    @Override
    public TrimPattern getTrimPattern() {
        return TrimPattern.RAISER;
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new HelmetRecipe(this, itemService.getCustomItem(CustomItemType.ENCHANTED_GLOWSTONE), generate()).build();
    }
}
