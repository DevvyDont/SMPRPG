package xyz.devvydont.smprpg.entity.slayer.shambling;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;
import xyz.devvydont.smprpg.util.persistence.KeyStore;

import java.util.Collection;
import java.util.List;

public class ShamblingMalfeasant extends CustomEntityInstance<Zombie> {

    public ShamblingMalfeasant(Entity entity, CustomEntityType type) {
        this((Zombie) entity, type);
    }

    public ShamblingMalfeasant(Zombie entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public static String HEAD_TEXTURE = "http://textures.minecraft.net/texture/2b0aad2f4f06d2e0ca83c5462460065fc4a0d3093ab67c564a5ae5d89dbf02b4";

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);

        super.setup();

        _entity.getEquipment().setHelmet(null);
        _entity.getEquipment().setChestplate(null);
        _entity.getEquipment().setLeggings(null);
        _entity.getEquipment().setBoots(null);

        var head = new ItemStack(Material.PLAYER_HEAD);
        var meta = (SkullMeta) head.getItemMeta();
        meta.setPlayerProfile(ICustomTextured.getProfile(ShamblingAbominationParent.HEAD_TEXTURE));
        head.setItemMeta(meta);

        _entity.getEquipment().setHelmet(head);
        _entity.getEquipment().setChestplate(getAttributelessItem(Material.NETHERITE_CHESTPLATE));
        _entity.getEquipment().setLeggings(getAttributelessItem(Material.NETHERITE_LEGGINGS));
        _entity.getEquipment().setBoots(getAttributelessItem(Material.NETHERITE_BOOTS));

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
                new QuantityLootDrop(ItemService.generate(CustomItemType.NECROTIC_FLESH), 3, 5, this)
        );
    }

    @Override
    public SkillExperienceReward generateSkillExperienceReward() {
        return SkillExperienceReward.of(SkillType.COMBAT, 3920);
    }
}
