package xyz.devvydont.smprpg.entity.slayer.shambling;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SinfulShambler extends CustomEntityInstance<Zombie> {

    public SinfulShambler(Entity entity, CustomEntityType type) {
        this((Zombie) entity, type);
    }

    public SinfulShambler(Zombie entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);

        super.setup();

        _entity.getEquipment().setHelmet(null);
        _entity.getEquipment().setChestplate(null);
        _entity.getEquipment().setLeggings(null);
        _entity.getEquipment().setBoots(null);

        _entity.getEquipment().setHelmet(getAttributelessItem(Material.IRON_HELMET));
        _entity.getEquipment().setChestplate(getAttributelessItem(CustomItemType.MITHRIL_CHESTPLATE));
        _entity.getEquipment().setLeggings(getAttributelessItem(Material.IRON_LEGGINGS));
        _entity.getEquipment().setBoots(getAttributelessItem(Material.IRON_BOOTS));

        // Patch to remove the chance of spawning in w/ the "zombie leader bonus" modifier. This is a vanilla mechanic.
        var hp = AttributeService.getInstance().getAttribute(_entity, AttributeWrapper.HEALTH);
        if (hp != null)
            hp.clearModifiers();

        _entity.setAdult();

        _entity.getPersistentDataContainer().set(KeyStore.SLAYER_SPAWN_TYPE, PersistentDataType.STRING, ShamblingAbominationParent.SPAWN_MOB_FLAG);
        hp.save(_entity, AttributeWrapper.HEALTH);
    }


    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new QuantityLootDrop(ItemService.generate(CustomItemType.NECROTIC_FLESH), 1, 3, this)
        );
    }

    @Override
    public SkillExperienceReward generateSkillExperienceReward() {
        return SkillExperienceReward.of(SkillType.COMBAT, 625);
    }
}
