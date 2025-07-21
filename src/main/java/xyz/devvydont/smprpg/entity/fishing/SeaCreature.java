package xyz.devvydont.smprpg.entity.fishing;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward;

import java.util.UUID;

public class SeaCreature<T extends LivingEntity> extends CustomEntityInstance<T> implements Listener {

    public static final TextColor NAME_COLOR = TextColor.color(0x3FD6FF);

    private @Nullable UUID spawnedBy = null;

    /**
     * An unsafe constructor to use to allow dynamic creation of custom entities.
     * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
     * entities.
     *
     * @param entity     The entity that should map the T type parameter.
     * @param entityType The entity type.
     */
    public SeaCreature(T entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public TextColor getNameColor() {
        return NAME_COLOR;
    }

    @Override
    public double getSkillExperienceMultiplier() {
        return 1.0;
    }

    @Override
    public SkillExperienceReward generateSkillExperienceReward() {
        return SkillExperienceReward.of(SkillType.FISHING, (int) (getLevel() * 250 * getSkillExperienceMultiplier()));
    }

    /**
     * Get the entity ID responsible for spawning this sea creature. Can be null.
     * @return The entity ID.
     */
    public @Nullable UUID getSpawnedBy() {
        return spawnedBy;
    }

    /**
     * Set the entity ID responsible for spawning this sea creature. Can pass in null to clear.
     * @param spawnedBy Who spawned the entity.
     */
    public void setSpawnedBy(@Nullable UUID spawnedBy) {
        this.spawnedBy = spawnedBy;
    }

    /**
     * When a sea creature takes damage from a fishing rod, 3x the damage.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTakeDamage(CustomEntityDamageByEntityEvent event) {

        if (!event.getDamaged().equals(this._entity))
            return;

        if (!(event.getDealer() instanceof LivingEntity dealer))
            return;

        var equipment = dealer.getEquipment();
        if (equipment == null)
            return;

        var mainItem = equipment.getItemInMainHand();
        if (mainItem.getType() == Material.AIR)
            return;

        if (ItemService.blueprint(mainItem) instanceof IFishingRod)
            event.multiplyDamage(IFishingRod.CREATURE_MULTIPLIER);

    }
}
