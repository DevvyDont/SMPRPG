package xyz.devvydont.smprpg.entity.creatures;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class FierySylph<T extends LivingEntity> extends CustomEntityInstance<T> {

    public FierySylph(Entity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public FierySylph(T entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.CHILI_PEPPER), 3, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.CYRAX_HELMET), 425, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.CYRAX_CHESTPLATE), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.CYRAX_LEGGINGS), 450, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.CYRAX_BOOTS), 425, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.CYRAX_BOW), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.BOILING_PICKAXE), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.BOILING_INGOT), 250, this),

                new ChancedItemDrop(ItemService.generate(Material.BLAZE_ROD), 2, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_BLAZE_ROD), 25, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_BLAZE_ROD), 350, this),

                new ChancedItemDrop(ItemService.generate(CustomItemType.INFERNO_ARROW), 150, this)
        );
    }

}
