package xyz.devvydont.smprpg.items.tools;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment;
import xyz.devvydont.smprpg.items.interfaces.IModularToolComponent;
import xyz.devvydont.smprpg.services.ItemService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemDrill extends CustomAttributeItem implements IFueledEquipment, Listener {

    public final String BREAK_SOUND = "smprpg:tools.drill.break";
    public final String DRILL_NAME_EXTENSTION = " Drill";

    public static final NamespacedKey drillBaseKey = new NamespacedKey(SMPRPG.getPlugin(), "drill_base");
    public static final NamespacedKey drillHeadKey = new NamespacedKey(SMPRPG.getPlugin(), "drill_head");
    public static final NamespacedKey drillTankKey = new NamespacedKey(SMPRPG.getPlugin(), "drill_tank");

    public final int DRILL_BASE_SLOT_INDEX = 0;
    public final int DRILL_HEAD_SLOT_INDEX = 1;
    public final int DRILL_TANK_SLOT_INDEX = 2;

    public ItemStack getDrillPartItem(ItemStack item, int index) {
        var data = item.getData(DataComponentTypes.CONTAINER);
        if (data != null) {
            List<ItemStack> toolContainer = data.contents();
            return toolContainer.get(index);
        }
        return null;
    }

    public Collection<AttributeEntry> getDrillHeadStats(ItemStack item) {
        ItemStack drillHead = getDrillPartItem(item, DRILL_HEAD_SLOT_INDEX);
        if (drillHead != null) {
            var head = (IModularToolComponent) ItemService.blueprint(drillHead);
            return head.getAttributes();
        }
        return List.of();
    }
//
    public Collection<AttributeEntry> getDrillBaseStats(ItemStack item) {
        ItemStack drillBase = getDrillPartItem(item, DRILL_BASE_SLOT_INDEX);
        if (drillBase != null) {
            var base = (IModularToolComponent) ItemService.blueprint(drillBase);
            return base.getAttributes();
        }
        return List.of();
    }

    public int getPowerRating() {
        return 0;
    }

    public static double DRILL_ATTACK_SPEED_DEBUFF = -0.5;

    public ItemDrill(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.DRILL;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        var headStats = getDrillHeadStats(item);
        var baseStats = getDrillBaseStats(item);

        Collection<AttributeEntry> retList = new java.util.ArrayList<>(List.of());
        retList.add(new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, DRILL_ATTACK_SPEED_DEBUFF));
        retList.addAll(headStats);
        retList.addAll(baseStats);
        return retList;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxFuel(ItemStack item) {
        var tank = getDrillPartItem(item, DRILL_TANK_SLOT_INDEX);
        return tank.getData(DataComponentTypes.MAX_DAMAGE);
    }

    @Override
    public int getFuelUsed(ItemStack item) {
        var tank = getDrillPartItem(item, DRILL_TANK_SLOT_INDEX);
        return tank.getData(DataComponentTypes.DAMAGE);
    }

    @Override
    public void setFuelUsed(ItemStack item, int fuel) {
        var tank = getDrillPartItem(item, DRILL_TANK_SLOT_INDEX);
        tank.setData(DataComponentTypes.DAMAGE, fuel);
        ItemService.blueprint(tank).updateItemData(tank);
        var containerData = item.getData(DataComponentTypes.CONTAINER);
        ArrayList<ItemStack> items = new ArrayList<>(containerData.contents());
        items.set(DRILL_TANK_SLOT_INDEX, tank);
        item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(items));
        ItemService.blueprint(tank).updateItemData(tank);
        item.setData(DataComponentTypes.DAMAGE, fuel);
    }

    public int getModularSlots() {
        return 3;
    }

    public void initialize(ItemStack drill) {

        // // Default to steel components if for whatever reason this drill has no components.
        // var items = new ArrayList<ItemStack>();
        // CustomItemType[] comps = {CustomItemType.STEEL_DRILL_BASE, CustomItemType.STEEL_DRILL_HEAD, CustomItemType.SMALL_FUEL_TANK};
        // IModularToolComponent bp;
        // final String[] keys = new String[3];
        // for (var i = 0; i < getModularSlots(); i++) {
        //     items.add(ItemService.generate(comps[i]));
        //     bp = (IModularToolComponent) ItemService.blueprint(items.get(i));
        //     keys[i] = bp.getAttrKey();
        // }
//
        // // Set the stored contents.
        // drill.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(items));
        // IFueledEquipment drillBlueprint = new ItemDrill(itemService, CustomItemType.DRILL);  // We need to manually create a drill blueprint class here, as the item type key has not been set yet.
        // drill.setData(DataComponentTypes.MAX_DAMAGE, items.get(2).getData(DataComponentTypes.MAX_DAMAGE));
        // drill.setData(DataComponentTypes.SWING_ANIMATION, SwingAnimation.swingAnimation().type(SwingAnimation.Animation.STAB).duration(1));
        // drillBlueprint.setFuelUsed(drill, 0);
        // drill.editPersistentDataContainer(pdc -> pdc.set(drillBaseKey, PersistentDataType.STRING, keys[0]));
        // drill.editPersistentDataContainer(pdc -> pdc.set(drillHeadKey, PersistentDataType.STRING, keys[1]));
        // drill.editPersistentDataContainer(pdc -> pdc.set(drillTankKey, PersistentDataType.STRING, keys[2]));
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        var data = itemStack.getData(DataComponentTypes.CONTAINER);
        if (data == null || data.contents().isEmpty()) {
            initialize(itemStack);
        }
        itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        super.updateItemData(itemStack);
    }

    @Override
    public String getBreakSound() {
        return BREAK_SOUND;
    }

    @Override
    public ItemRarity getRarity(ItemStack item) {
        ItemStack drillHead = getDrillPartItem(item, DRILL_HEAD_SLOT_INDEX);
        if (drillHead != null)
            return ItemService.blueprint(drillHead).getRarity(drillHead);
        return ItemRarity.COMMON;
    }
}
