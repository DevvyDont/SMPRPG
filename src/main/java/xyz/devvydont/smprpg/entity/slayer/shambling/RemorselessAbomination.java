package xyz.devvydont.smprpg.entity.slayer.shambling;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EquipmentSlot;
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

public class RemorselessAbomination extends CustomEntityInstance<Zombie> {

    public RemorselessAbomination(Entity entity, CustomEntityType type) {
        this((Zombie) entity, type);
    }

    public RemorselessAbomination(Zombie entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public static String HEAD_TEXTURE = "http://textures.minecraft.net/texture/8b515ecbc03fd763ccbb4a187a941417473fae4425b6071e13cfe6aefcebfee8";

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
        meta.setPlayerProfile(ICustomTextured.getProfile(HEAD_TEXTURE));
        head.setItemMeta(meta);

        _entity.getEquipment().setHelmet(head);

        // Instead of using the actual Abominable armor, we are going to spoof it using asset IDs
        // We do this so that we don't have listener interference with damage reduction.
        var assetKey = Key.key("abomination");
        ItemStack chest = getAttributelessItem(Material.IRON_CHESTPLATE);
        chest.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.CHEST).assetId(assetKey).build());
        ItemStack legs = getAttributelessItem(Material.IRON_LEGGINGS);
        legs.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.LEGS).assetId(assetKey).build());
        ItemStack boots = getAttributelessItem(Material.IRON_CHESTPLATE);
        boots.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.FEET).assetId(assetKey).build());
        _entity.getEquipment().setChestplate(chest);
        _entity.getEquipment().setLeggings(legs);
        _entity.getEquipment().setBoots(boots);

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
                new QuantityLootDrop(ItemService.generate(CustomItemType.NECROTIC_FLESH), 2, 3, this)
        );
    }

    @Override
    public SkillExperienceReward generateSkillExperienceReward() {
        return SkillExperienceReward.of(SkillType.COMBAT, 1570);
    }
}
