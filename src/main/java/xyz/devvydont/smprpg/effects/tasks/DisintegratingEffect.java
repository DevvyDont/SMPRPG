package xyz.devvydont.smprpg.effects.tasks;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.effects.services.SpecialEffectService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class DisintegratingEffect extends SpecialEffectTask implements Listener {

    public static final int SECONDS = 30;

    public static final int TIER_2_TICK_THRESHOLD = SECONDS / 3 * 10;
    public static final int TIER_3_TICK_THRESHOLD = SECONDS / 3 * 2 * 10;

    private static final NamespacedKey MODIFIER_KEY = new NamespacedKey("smprpg", "disintegration");

    private int hpDrain = 0;
    private double scaleDrain = 0;

    public DisintegratingEffect(SpecialEffectService service, Player player, int seconds) {
        super(service, player, seconds);
    }

    @Override
    public Component getExpiredComponent() {
        return ComponentUtils.create("STABILIZED!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
    }

    @Override
    public Component getNameComponent() {
        var text = "DISINTEGRATING";
        if (_ticks < TIER_2_TICK_THRESHOLD)
            text = "CORRODING";
        else if (_ticks < TIER_3_TICK_THRESHOLD)
            text = "FRACTURING";
        return ComponentUtils.encrypt(text, getTier() / 10f * 1.5).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, true);
    }

    @Override
    public TextColor getTimerColor() {
        return NamedTextColor.DARK_PURPLE;
    }

    public void updateAttribute(Attribute attribute, double modifier) {
        var attr = getPlayer().getAttribute(attribute);
        if (attr == null)
            return;

        attr.removeModifier(MODIFIER_KEY);
        attr.addTransientModifier(new AttributeModifier(MODIFIER_KEY, modifier, AttributeModifier.Operation.ADD_NUMBER));
    }

    public void clearAttributes() {
        var hp = getPlayer().getAttribute(Attribute.MAX_HEALTH);
        if (hp != null)
            hp.removeModifier(MODIFIER_KEY);
        var scale = getPlayer().getAttribute(Attribute.SCALE);
        if (scale != null)
            scale.removeModifier(MODIFIER_KEY);
        getPlayer().removePotionEffect(PotionEffectType.SLOWNESS);
        getPlayer().removePotionEffect(PotionEffectType.NAUSEA);
    }

    public int getTier() {
        if (_ticks >= TIER_3_TICK_THRESHOLD)
            return 3;
        if (_ticks >= TIER_2_TICK_THRESHOLD)
            return 2;
        return 1;
    }

    @Override
    protected void tick() {

        var tier = getTier();
        hpDrain += tier;
        scaleDrain += .002 * tier;

        new ParticleBuilder(Particle.PORTAL)
                .location(getPlayer().getEyeLocation())
                .offset(.25, .1, .25)
                .count(2)
                .receivers(20)
                .spawn();

        if (_ticks == TIER_3_TICK_THRESHOLD)
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20*10, 0, true, true));

        if (_ticks == TIER_2_TICK_THRESHOLD)
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20*20, 0, true, true));

        if (_ticks == TIER_3_TICK_THRESHOLD || _ticks == TIER_2_TICK_THRESHOLD) {
            getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.BLOCK_GLASS_BREAK, 1, .5f);
            new ParticleBuilder(Particle.FLASH)
                    .location(getPlayer().getEyeLocation())
                    .spawn();
        }

        // Damage the player for whatever tier we are in.
        if (getPlayer().getHealth() > 1)
            getPlayer().setHealth(getPlayer().getHealth() - 1);

        updateAttribute(Attribute.MAX_HEALTH, -hpDrain);
        updateAttribute(Attribute.SCALE, -scaleDrain);

        getPlayer().setHealthScale(SMPRPG.getService(EntityService.class).getPlayerInstance(getPlayer()).getHealthScale());
    }

    @Override
    protected void expire() {
        clearAttributes();
        var ses = SMPRPG.getService(SpecialEffectService.class);
        getPlayer().getWorld().playSound(getPlayer().getLocation(), Sound.ENTITY_WITHER_BREAK_BLOCK, 1, 1);
        new ParticleBuilder(Particle.DRAGON_BREATH)
                .location(getPlayer().getEyeLocation())
                .offset(.1, .1, .1)
                .count(5)
                .spawn();
        ses.giveEffect(getPlayer(), new HollowedEffect(ses, getPlayer(), 10));
    }

    @Override
    public void removed() {
        clearAttributes();
    }

    /**
     * Any teleport event of any kind will clear the effect, pretty simple.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void __onTeleport(PlayerTeleportEvent event) {
        if (!event.getPlayer().equals(getPlayer()))
            return;

        SMPRPG.getService(SpecialEffectService.class).removeEffect(getPlayer());
    }
}
