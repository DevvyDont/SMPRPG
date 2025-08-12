package xyz.devvydont.smprpg.entity.fishing;

import org.bukkit.Material;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.LivingEntity;
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

public class ChorusSlug extends SeaCreature<Endermite> {
    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public ChorusSlug(LivingEntity entity, CustomEntityType entityType) {
        super((Endermite) entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        updateBaseAttribute(AttributeWrapper.SCALE, 8);
    }

    @Override
    public void setup() {
        super.setup();
        var boots = ItemService.generate(Material.LEATHER_BOOTS);
        boots.addUnsafeEnchantment(EnchantmentService.VOIDSTRIDING_BLESSING.getEnchantment(), 1);
        _entity.getEquipment().setBoots(boots);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.ERRATIC_SLIME), 1, this)
        );
    }
}
