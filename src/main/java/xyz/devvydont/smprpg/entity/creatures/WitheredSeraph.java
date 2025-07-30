package xyz.devvydont.smprpg.entity.creatures;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class WitheredSeraph<T extends LivingEntity> extends CustomEntityInstance<T> implements Listener {

    public WitheredSeraph(T entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    public WitheredSeraph(Entity entity, CustomEntityType type) {
        super(entity, type);
    }

    @Override
    public void setup() {
        super.setup();

        if (_entity.getEquipment() == null)
            return;

        _entity.getEquipment().setItemInMainHand(getAttributelessItem(Material.NETHERITE_HOE));
        _entity.getEquipment().setChestplate(getAttributelessItem(Material.NETHERITE_CHESTPLATE));
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        this.updateBaseAttribute(AttributeWrapper.SCALE, 1.25);
        this.updateBaseAttribute(AttributeWrapper.MOVEMENT_SPEED, .15);
        this.updateBaseAttribute(AttributeWrapper.FOLLOW_RANGE, 3);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.CHARRED_CRISP), 4, this),
                new ChancedItemDrop(SMPRPG.getService(ItemService.class).getCustomItem(Material.OBSIDIAN), 5, this),
                new QuantityLootDrop(SMPRPG.getService(ItemService.class).getCustomItem(Material.BONE), 1, 2, this)
        );
    }

    /**
     * These enemies will only target players.
     */
    @EventHandler
    public void onTargetNonPlayer(EntityTargetLivingEntityEvent event) {

        if (!event.getEntity().equals(_entity))
            return;

        if (!(event.getTarget() instanceof Player))
            event.setCancelled(true);
    }
}
