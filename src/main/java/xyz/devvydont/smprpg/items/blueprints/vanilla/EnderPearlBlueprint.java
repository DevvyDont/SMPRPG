package xyz.devvydont.smprpg.items.blueprints.vanilla;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.base.VanillaItemBlueprint;
import xyz.devvydont.smprpg.services.ItemService;

public class EnderPearlBlueprint extends VanillaItemBlueprint {

    public EnderPearlBlueprint(ItemService itemService, Material material) {
        super(itemService, material);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 64);
    }

}
