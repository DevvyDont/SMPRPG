package xyz.devvydont.smprpg.effects.tasks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.effects.services.SpecialEffectService;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

public class ShroudedEffect extends SpecialEffectTask implements Listener {

    public ShroudedEffect(SpecialEffectService service, Player player, int seconds) {
        super(service, player, seconds);
    }

    @Override
    public Component getExpiredComponent() {
        return ComponentUtils.create("REVEALED!", NamedTextColor.RED);
    }

    @Override
    public Component getNameComponent() {
        return ComponentUtils.create("Shrouded!", NamedTextColor.AQUA);
    }

    @Override
    public TextColor getTimerColor() {
        return NamedTextColor.GREEN;
    }

    @Override
    protected void tick() {
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50, 2, true, true));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 50, 1, true, true));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 50, 0, true, true));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 50, 0, true, true));
        getPlayer().setAllowFlight(true);
        getPlayer().setFoodLevel(20);
        getPlayer().setSaturation(20);
        // If we are flying, subtract a second depending on the tick. This will make it appear like it's draining.
        if (getPlayer().isFlying())
            seconds--;
    }

    @Override
    protected void expire() {
        if (!getPlayer().getGameMode().isInvulnerable())
            getPlayer().setAllowFlight(false);
    }

    @Override
    public void removed() {
        if (!getPlayer().getGameMode().isInvulnerable())
            getPlayer().setAllowFlight(false);
    }

    /*
     * When an entity targets our player that has the effect, don't allow it to happen
     */
    @EventHandler
    public void onPlayerTargeted(EntityTargetEvent event) {

        // We don't care for untarget events
        if (event.getTarget() == null)
            return;

        // We don't care for non player targets
        if (!(event.getTarget() instanceof Player eventPlayer))
            return;

        // We don't care unless the player targeted is the owner of this effect.
        if (!eventPlayer.equals(getPlayer()))
            return;

        // Our player is being targeted.
        event.setCancelled(true);
    }

    /*
     * If our player receives damage from another entity while shrouded, don't do any damage.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void __onReceiveDamageWhileShrouded(EntityDamageByEntityEvent event) {

        // Ignore non players
        if (!(event.getEntity() instanceof Player eventPlayer))
            return;

        if (!eventPlayer.equals(getPlayer()))
            return;

        event.setDamage(0);
    }

    /*
     * If our player deals damage, remove their shrouded effect.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDealDamage(CustomEntityDamageByEntityEvent event) {

        // Ignore non players
        if (!(event.getDealer() instanceof Player eventPlayer))
            return;

        if (!eventPlayer.equals(getPlayer()))
            return;

        service.removeEffect(eventPlayer);
    }

    /**
     * If a player triggers a loot event, remove their pacifist
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onOpenLootChest(LootGenerateEvent event) {

        // Ignore non players
        if (!(event.getEntity() instanceof Player eventPlayer))
            return;

        // Ignore players that aren't our player
        if (!eventPlayer.equals(_player))
            return;

        service.removeEffect(eventPlayer);
    }
}
