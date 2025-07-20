package xyz.devvydont.smprpg.items.base;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.List;

public abstract class ChargedItemBlueprint extends CustomItemBlueprint implements IFooterDescribable {

    public ChargedItemBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    public abstract int getMaxCharges(ItemStack item);

    public Sound getBreakSound() {
        return Sound.ENTITY_ITEM_BREAK;
    }

    public void breakItem(Player player, ItemStack item) {
        item.setAmount(0);
        player.playSound(player.getLocation(), getBreakSound(), 1, 1);
    }

    public void useCharge(Player player, ItemStack item) {

        var itemDamage = item.getData(DataComponentTypes.DAMAGE);
        if (itemDamage != null)
            itemDamage++;
        setChargesUsed(item, itemDamage != null ? itemDamage : 0);

        if (getChargesLeft(item) <= 0)
            breakItem(player, item);
    }

    public void setChargesUsed(ItemStack item, int charges) {
        item.setData(DataComponentTypes.DAMAGE, charges);
        updateItemData(item);
    }

    public int getChargesUsed(ItemStack item) {
        var damage = item.getData(DataComponentTypes.DAMAGE);
        return damage != null ? damage : 0;
    }

    public int getChargesLeft(ItemStack item) {
        var damage = item.getData(DataComponentTypes.DAMAGE);
        var maxDamage = item.getData(DataComponentTypes.MAX_DAMAGE);
        if (damage == null || maxDamage == null)
            return 0;
        return maxDamage - damage;
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.MAX_DAMAGE, getMaxCharges(itemStack));
        var damage = itemStack.getData(DataComponentTypes.DAMAGE);
        if (damage == null)
            itemStack.setData(DataComponentTypes.DAMAGE, 0);
    }

    @Override
    public void updateItemData(ItemMeta meta) {
        super.updateItemData(meta);
        meta.setUnbreakable(false);
    }

    @Override
    public List<Component> getFooter(ItemStack itemStack) {
        return List.of(
                ComponentUtils.EMPTY,
                ComponentUtils.create("Charges left: ").append(ComponentUtils.create(getChargesLeft(itemStack) + "", NamedTextColor.GREEN))
        );
    }
}
