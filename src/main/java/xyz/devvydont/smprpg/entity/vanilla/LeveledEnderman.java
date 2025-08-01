package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Enderman;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.EntityGlobals;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.entity.components.EntityConfiguration;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class LeveledEnderman extends VanillaEntity<Enderman> implements Listener {

    public static final int MINIMUM_LEVEL = 35;
    public static final int END_MINIMUM_LEVEL = 45;

    public LeveledEnderman(Enderman entity) {
        super(entity);
    }

    private int determineLevel() {
        int level = MINIMUM_LEVEL;

        if (_entity.getWorld().getEnvironment().equals(World.Environment.THE_END))
            level = END_MINIMUM_LEVEL;

        level += EntityGlobals.getLevelDistanceBoost(_entity.getLocation());
        return Math.min(level, EntityGlobals.NATURAL_ENTITY_LEVEL_CAP);
    }

    @Override
    public EntityConfiguration getDefaultConfiguration() {
        var _default = EntityConfiguration.builder()
                .withLevel(MINIMUM_LEVEL)
                .withDamage(350)
                .withHealth(4000)
                .build();

        return EntityConfiguration.scale(_default, determineLevel());
    }

    public boolean canDropCrystal() {
        return getEntity().getWorld().getBiome(getEntity().getLocation()).equals(Biome.THE_END) &&
                Math.abs(getEntity().getLocation().getX()) <= 156 &&
                Math.abs(getEntity().getLocation().getZ()) <= 156;
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        List<LootDrop> drops = new java.util.ArrayList<>(List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_ENDER_PEARL), 50, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_ENDER_PEARL), 250, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENDERIOS), 4, this)
        ));

        if (canDropCrystal()) {
            drops.add(new ChancedItemDrop(ItemService.generate(CustomItemType.SUMMONING_CRYSTAL), 100, this));
            drops.add(new ChancedItemDrop(ItemService.generate(CustomItemType.UNSTABLE_HELMET), 500, this));
            drops.add(new ChancedItemDrop(ItemService.generate(CustomItemType.UNSTABLE_CHESTPLATE), 500, this));
            drops.add(new ChancedItemDrop(ItemService.generate(CustomItemType.UNSTABLE_LEGGINGS), 500, this));
            drops.add(new ChancedItemDrop(ItemService.generate(CustomItemType.UNSTABLE_BOOTS), 500, this));
        }

        return drops;
    }
}
