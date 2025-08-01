package xyz.devvydont.smprpg.fishing.loot;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.fishing.utils.FishingContext;
import xyz.devvydont.smprpg.fishing.loot.requirements.FishingLootRequirement;
import xyz.devvydont.smprpg.services.EntityService;

import java.util.Collection;

/**
 * Represents a loot item that is a sea creature. Is essentially just a custom mob you can fish up.
 */
public class SeaCreatureFishingLoot extends FishingLootBase {

    private final CustomEntityType customEntityType;

    protected SeaCreatureFishingLoot(CustomEntityType entityType, int weight, int fishingExperience, int minecraftExperience, Collection<FishingLootRequirement> requirements) {
        super(weight, fishingExperience, minecraftExperience, requirements);
        this.customEntityType = entityType;
    }

    @Override
    public @Nullable Entity generate(FishingContext ctx) {

        // Spawn the entity. This can fail, but we allow null returns if something breaks.
        var entity = SMPRPG.getService(EntityService.class).spawnCustomEntity(customEntityType, ctx.getLocation());
        if (entity == null) {
            SMPRPG.getInstance().getLogger().severe("Failed to generate custom sea creature for fishing event of type " + customEntityType);
            return null;
        }

        // Tag the entity with who was responsible for spawning it. This allows the person to always get loot from them
        // even if they had their kill stolen.
        if (entity instanceof SeaCreature<?> seaCreature)
            seaCreature.setSpawnedBy(ctx.getPlayer().getUniqueId());

        return entity.getEntity();
    }


    /**
     * Gets the {@link NamespacedKey} that can be used to reference this loot for things
     * like {@link PersistentDataContainer}s.
     *
     * @return A unique identifying key.
     */
    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(SMPRPG.getInstance(), customEntityType.key());
    }

    public CustomEntityType getCustomEntityType() {
        return customEntityType;
    }

    @Override
    public Material getDisplayMaterial() {
        return customEntityType.getDisplayMaterial();
    }

    /**
     * A utility builder to help in the construction of sea creature fishing loot.
     */
    public static class Builder extends FishingLootBuilder<SeaCreatureFishingLoot, Builder> {

        private final CustomEntityType creature;

        public Builder(CustomEntityType creature) {
            this.creature = creature;
        }

        @Override
        public SeaCreatureFishingLoot build() {
            return new SeaCreatureFishingLoot(creature, weight, experienceReward, minecraftExperience, requirements);
        }
    }
}
