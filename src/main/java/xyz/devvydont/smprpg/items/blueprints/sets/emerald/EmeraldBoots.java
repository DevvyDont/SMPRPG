package xyz.devvydont.smprpg.items.blueprints.sets.emerald;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingRecipe;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemArmor;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.BootsRecipe;

public class EmeraldBoots extends EmeraldArmorSet {

    public EmeraldBoots(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new BootsRecipe(this, itemService.getCustomItem(EmeraldArmorSet.INGREDIENT), generate()).build();
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.BOOTS;
    }

    @Override
    public double getDefense() {
        return ItemArmor.getDefenseFromMaterial(Material.DIAMOND_BOOTS)+5;
    }

    @Override
    public double getHealth() {
        return ItemArmor.getHealthFromMaterial(Material.DIAMOND_BOOTS)+5;
    }

    @Override
    public double getStrength() {
        return ItemArmor.getDamageFromMaterial(Material.NETHERITE_BOOTS);
    }
}
