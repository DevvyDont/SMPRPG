package xyz.devvydont.smprpg.items.blueprints.sets.neptune;

import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.items.interfaces.ISellable;
import xyz.devvydont.smprpg.services.ItemService;

public class JupiterArtifact extends CustomItemBlueprint implements ISellable, ICustomTextured {

    public JupiterArtifact(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public String getTextureUrl() {
        return "4f4ff19c367e442bbba37b5159cce4f22596e6673c8e913a11659c4b723577ad";
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.MATERIAL;
    }

    @Override
    public int getWorth(ItemStack itemStack) {
        return 6000 * itemStack.getAmount();
    }
}
