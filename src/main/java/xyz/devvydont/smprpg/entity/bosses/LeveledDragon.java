package xyz.devvydont.smprpg.entity.bosses;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.base.BossInstance;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.entity.components.EntityConfiguration;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class LeveledDragon extends BossInstance<EnderDragon> {

    /**
     * A number from 0-.9999 that determines how intense crystal placement luck is at first.
     */
    public static final double BASE_CRYSTAL_LUCK = .5;

    /**
     * How much luck multiplier you "cap out at".
     */
    public static final double CRYSTAL_LUCK_SOFT_CAP = 4;

    private boolean wasSummoned = false;

    public LeveledDragon(EnderDragon entity) {
        super(entity);
    }

    @Override
    public void setup() {
        super.setup();
        this.updateBaseAttribute(AttributeWrapper.ARMOR, 0);
    }

    @Override
    public @Nullable BossBar createBossBar() {
        return null;
    }

    @Override
    public long getTimeLimit() {
        return wasSummoned ? 60*5 : INFINITE_TIME_LIMIT;
    }

    @Override
    public String getClassKey() {
        return VanillaEntity.VANILLA_CLASS_KEY;
    }

    @Override
    public EntityType getDefaultEntityType() {
        return EntityType.ENDER_DRAGON;
    }

    @Override
    public String getEntityName() {
        return "Ender Dragon";
    }

    public void setSummoned(boolean summoned) {
        this.wasSummoned = summoned;
    }

    @Override
    public EntityConfiguration getDefaultConfiguration() {
        return EntityConfiguration.builder()
                .withLevel(wasSummoned ? 50 : 40)
                .withHealth(wasSummoned ? 3_000_000 : 1_000_000)
                .withDamage(wasSummoned ? 1250 : 500)
                .build();
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_HELMET), 850, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_CHESTPLATE), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_LEGGINGS), 900, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_BOOTS), 850, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ELDERFLAME_DAGGER), 1000, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.TRANSMISSION_WAND), 1500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.DRACONIC_CRYSTAL), 500, this),
                new QuantityLootDrop(ItemService.generate(CustomItemType.DRAGON_SCALES), 1, 2, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ENCHANTED_ENDER_PEARL), 50, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.PREMIUM_ENDER_PEARL), 7, this),
                new QuantityLootDrop(ItemService.generate(Material.ENDER_PEARL), 3, 7, this)
        );
    }

}
