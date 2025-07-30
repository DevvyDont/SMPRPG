package xyz.devvydont.smprpg.effects.tasks;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.effects.services.SpecialEffectService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class DisintegratingEffect extends SpecialEffectTask {

    public static final int SECONDS = 30;

    public static final int TIER_2_TICK_THRESHOLD = SECONDS / 3 * 20;
    public static final int TIER_3_TICK_THRESHOLD = SECONDS / 3 * 2 * 20;

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
        return ComponentUtils.encrypt("DISINTEGRATING!", getTier() / 10f * 25).color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.BOLD, true);
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
        scaleDrain += .005 * tier;

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

        updateAttribute(Attribute.MAX_HEALTH, -hpDrain);
        updateAttribute(Attribute.SCALE, -scaleDrain);

        // Damage the player for whatever tier we are in.
        if (getPlayer().getHealth() > 1)
            getPlayer().setHealth(getPlayer().getHealth() - 1);
    }

    @Override
    protected void expire() {
        clearAttributes();
    }

    @Override
    public void removed() {
        clearAttributes();
    }
}
