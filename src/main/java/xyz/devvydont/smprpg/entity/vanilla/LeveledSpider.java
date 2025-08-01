package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Spider;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class LeveledSpider extends VanillaEntity<Spider> {

    public LeveledSpider(Spider entity) {
        super(entity);
    }

    public LeveledSpider(CaveSpider entity) {
        super(entity);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_STRING), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_STRING), 4_000, this)
        );
    }

}
