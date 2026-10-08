package xyz.devvydont.smprpg.items.blueprints.sets.neptune;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class PlutosArtifact extends CustomItemBlueprint implements ISellable, ICustomTextured {

    public PlutosArtifact(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "d919c3488fe9f4934429856fb43f860b8dab6096482e8632050810f44ef7cb17";
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.MATERIAL;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 20000 * itemStack.getAmount();
    }

}
