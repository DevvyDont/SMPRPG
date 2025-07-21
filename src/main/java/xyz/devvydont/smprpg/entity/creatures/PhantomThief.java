package xyz.devvydont.smprpg.entity.creatures;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Stray;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class PhantomThief extends CustomEntityInstance<Stray> {

    public PhantomThief(Entity entity, CustomEntityType type) {
        this((Stray) entity, type);
    }

    public PhantomThief(Stray entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void setup() {
        super.setup();
        _entity.getEquipment().setItemInMainHand(ItemService.generate(Material.BOW));
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.LUCKY_CHARM), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.SPEED_CHARM), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.STRENGTH_CHARM), 500, this),

                new ChancedItemDrop(ItemService.generate(CustomItemType.SHADOW_BREW), 10, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PHANTOM_CURRY), 3, this),

                new ChancedItemDrop(ItemService.generate(Material.BONE), 2, this)
        );
    }
}
