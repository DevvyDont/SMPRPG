package xyz.devvydont.smprpg.entity.fishing;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.skills.utils.SkillExperienceReward;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.UUID;

public class SeaCreature<T extends LivingEntity> extends CustomEntityInstance<T> implements Listener {

    public static final TextColor NAME_COLOR = TextColor.color(0x3FD6FF);

    /*
    How bad should we nerf lure times if sea creatures are left alive? For every sea creature we leave alive
    We keep compounding a nerf for reeling another fish.
     */
    public static final double SEA_CREATURE_LURE_NERF = .4;

    private @Nullable UUID spawnedBy = null;

    private final String _team = "smprpg:sea_creatures";

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
        return SkillExperienceReward.of(SkillType.FISHING, (int) (getLevel() * 20 * getSkillExperienceMultiplier()));
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

    private Team getTeam() {
        var team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(_team);
        if (team == null)
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(_team);
        team.color(NamedTextColor.AQUA);
        return team;
    }

    /**
     * When a fish spawns in, there is a chance to cancel it if the fisher is the owner of this sea creature.
     * Obviously, if we have more sea creatures alive, there will be more attempts at cancelling the fish.
     */
    @EventHandler(ignoreCancelled = true)
    public void onCast(PlayerFishEvent event) {

        if (!event.getState().equals(PlayerFishEvent.State.LURED))
            return;

        if (!event.getPlayer().getUniqueId().equals(spawnedBy))
            return;

        // We probably shouldn't do this if they are pretty far away.
        if (event.getPlayer().getLocation().distance(_entity.getLocation()) > 100)
            return;

        // Our owner is attempting to spawn a fish. Cancel it if we roll an rng check.
        if (Math.random() > SEA_CREATURE_LURE_NERF)
            return;

        event.getPlayer().sendMessage(ComponentUtils.error("The creature you caught is scaring the fish away!"));
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 1f, 1.25f);
        var team = getTeam();
        team.addEntity(_entity);
        _entity.setGlowing(true);
        event.setCancelled(true);
    }
}
