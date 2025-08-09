package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class SpacePig extends SeaCreature<Pig> {

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public SpacePig(LivingEntity entity, CustomEntityType entityType) {
        super((Pig) entity, entityType);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.SPACE_HELMET), 1000, this)
        );
    }

    @Override
    public void setup() {
        super.setup();

        // Add space helmet and void striding boots to the pig.
        _entity.getEquipment().setHelmet(ItemService.generate(CustomItemType.SPACE_HELMET));
        var boots = ItemService.generate(Material.LEATHER_BOOTS);
        boots.addUnsafeEnchantment(EnchantmentService.VOIDSTRIDING_BLESSING.getEnchantment(), 1);
        _entity.getEquipment().setBoots(boots);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.GRAVITY, .05);
    }
}
