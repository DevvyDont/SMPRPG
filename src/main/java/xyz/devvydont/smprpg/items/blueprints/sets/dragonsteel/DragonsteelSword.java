package xyz.devvydont.smprpg.items.blueprints.sets.dragonsteel;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.BlockTypeKeys;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.base.CustomAttributeItem;
import xyz.devvydont.smprpg.items.base.SMPItemBlueprint;
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword;
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment;
import xyz.devvydont.smprpg.items.interfaces.ICraftable;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.crafting.builders.SwordRecipe;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.items.AbilityUtil;
import xyz.devvydont.smprpg.util.items.ToolGlobals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DragonsteelSword extends CustomAttributeItem implements ICraftable, IHeaderDescribable, IBreakableEquipment, Listener {

    public static final Tool TOOL_COMP = Tool.tool()
            .defaultMiningSpeed(1.0f)
            .canDestroyBlocksInCreative(false)
            .addRule(Tool.rule(RegistrySet.keySet(RegistryKey.BLOCK, BlockTypeKeys.COBWEB), 15.0f, TriState.TRUE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.SWORD_INSTANTLY_MINES), Float.MAX_VALUE, TriState.FALSE))
            .addRule(Tool.rule(ToolGlobals.blockRegistry.getTag(BlockTypeTagKeys.SWORD_EFFICIENT), 1.5f, TriState.FALSE))
            .build();

    public static final int SUMMONING_CRYSTAL_BOOST = 2;

    public DragonsteelSword(ItemService itemService, CustomItemType type) { super(itemService, type); }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        List<Component> components = new ArrayList<>();
        components.add(AbilityUtil.getAbilityComponent("Draconic Summoner (Passive)"));
        components.add(ComponentUtils.create("Summoning Crystal drops are ").append(ComponentUtils.create(SUMMONING_CRYSTAL_BOOST + "x", NamedTextColor.GREEN)).append(ComponentUtils.create(" more common")));
        components.add(ComponentUtils.create("when killing ").append(ComponentUtils.create("Ender", NamedTextColor.DARK_PURPLE)).append(ComponentUtils.create(" type mobs.")));
        return components;
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiers(ItemStack item) {
        return List.of(
                new AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 120),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemSword.SWORD_ATTACK_SPEED_DEBUFF),
                new AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 10)
        );
    }

    @Override
    public int getPowerRating() { return ToolGlobals.DRAGONSTEEL_TOOL_POWER; }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.SWORD;
    }

    @Override
    public EquipmentSlotGroup getActiveSlot() {
        return EquipmentSlotGroup.MAINHAND;
    }

    @Override
    public int getMaxDurability() {
        return ToolGlobals.DRAGONSTEEL_TOOL_DURABILITY;
    }

    @Override
    public NamespacedKey getRecipeKey() {
        return new NamespacedKey(SMPRPG.getPlugin(), getCustomItemType().getKey() + "-recipe");
    }

    @Override
    public void updateItemData(ItemStack itemStack) {
        super.updateItemData(itemStack);
        itemStack.setData(DataComponentTypes.TOOL, TOOL_COMP);
    }

    @Override
    public CraftingRecipe getCustomRecipe() {
        return new SwordRecipe(this,
                itemService.getCustomItem(CustomItemType.DRAGONSTEEL_INGOT),
                itemService.getCustomItem(CustomItemType.OBSIDIAN_TOOL_ROD),
                generate()
        ).build();
    }

    @Override
    public Collection<ItemStack> unlockedBy() {
        return List.of(
                itemService.getCustomItem(CustomItemType.DRAGONSTEEL_INGOT)
        );
    }

    @EventHandler
    public void onRollWitherSkull(CustomItemDropRollEvent event) {

        if (event.tool == null || event.tool.getType().equals(Material.AIR))
            return;

        if (!isItemOfType(event.tool))
            return;

        SMPItemBlueprint drop = itemService.getBlueprint(event.getDrop());
        if (drop.equals(itemService.getBlueprint(CustomItemType.SUMMONING_CRYSTAL)))
            event.chance = event.chance * SUMMONING_CRYSTAL_BOOST;;
    }

}
