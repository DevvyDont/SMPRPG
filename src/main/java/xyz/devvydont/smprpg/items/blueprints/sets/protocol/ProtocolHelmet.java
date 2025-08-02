package xyz.devvydont.smprpg.items.blueprints.sets.protocol;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;

public class ProtocolHelmet extends ProtocolArmorSet {

    public static final NamespacedKey overlay = new NamespacedKey("armor", "overlay/protocol");

    public ProtocolHelmet(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        var equippable = itemStack.getData(DataComponentTypes.EQUIPPABLE);
        if (equippable != null)
            itemStack.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(equippable.slot())
                    .damageOnHurt(equippable.damageOnHurt())
                    .dispensable(equippable.dispensable())
                    .swappable(equippable.swappable())
                    .equipSound(equippable.equipSound())
                    .allowedEntities(equippable.allowedEntities())
                    .assetId(equippable.assetId())
                    .shearSound(equippable.shearSound())
                    .cameraOverlay(overlay)
                    .equipOnInteract(equippable.equipOnInteract())
                    .canBeSheared(equippable.canBeSheared())
                    .build());
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
    }
}
