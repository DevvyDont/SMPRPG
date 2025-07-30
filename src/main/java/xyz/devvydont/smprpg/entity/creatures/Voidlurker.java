package xyz.devvydont.smprpg.entity.creatures;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class Voidlurker extends CustomEntityInstance<Shulker> {


    public Voidlurker(Entity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_SHULKER_SHELL), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_SHULKER_SHELL), 100, this),
                new ChancedItemDrop(ItemService.generate(Material.SHULKER_SHELL), 2, this)
        );
    }

}
