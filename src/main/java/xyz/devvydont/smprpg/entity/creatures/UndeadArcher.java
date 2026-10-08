package xyz.devvydont.smprpg.entity.creatures;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

import java.util.Collection;
import java.util.List;

public class UndeadArcher<T extends LivingEntity> extends CustomEntityInstance<T> {

    public UndeadArcher(Entity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public UndeadArcher(T entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.HUMANOID);

        super.setup();

        if (!(_entity instanceof LivingEntity living))
            return;

        living.getEquipment().setLeggings(null);
        living.getEquipment().setBoots(null);

        living.getEquipment().setHelmet(getAttributelessItem(Material.CHAINMAIL_HELMET));
        living.getEquipment().setChestplate(getAttributelessItem(Material.CHAINMAIL_CHESTPLATE));

        living.getEquipment().setItemInMainHand(getAttributelessItem(Material.BOW));

        _entity.getPersistentDataContainer().set(KeyStore.SLAYER_SPAWN_TYPE, PersistentDataType.STRING, ShamblingAbominationParent.SPAWN_MOB_FLAG);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.MYSTBLOOM_HELMET), 390, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.MYSTBLOOM_CHESTPLATE), 415, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.MYSTBLOOM_LEGGINGS), 390, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.MYSTBLOOM_BOOTS), 380, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.MYSTBLOOM_KUNAI), 420, this),
                new ChancedItemDrop(ItemService.generate(Material.BONE), 2, this),
                new ChancedItemDrop(ItemService.generate(Material.COOKIE), 2, this),
                new ChancedItemDrop(ItemService.generate(Material.ARROW), 2, this)
        );
    }
}
