package xyz.devvydont.smprpg.effects.tasks;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.effects.services.SpecialEffectService;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class HollowedEffect extends SpecialEffectTask {

    private final static NamespacedKey ATTRIBUTE_KEY = new NamespacedKey("smprpg", "hollowed");

    public HollowedEffect(SpecialEffectService service, Player player, int seconds) {
        super(service, player, seconds);
    }

    @Override
    public Component getExpiredComponent() {
        return ComponentUtils.create("RECONSTRUCTED!", NamedTextColor.GREEN).decoration(TextDecoration.BOLD, true);
    }

    @Override
    public Component getNameComponent() {
        return ComponentUtils.create("HOLLOWED!", NamedTextColor.BLACK).decoration(TextDecoration.BOLD, true);
    }

    @Override
    public TextColor getTimerColor() {
        return NamedTextColor.RED;
    }

    @Override
    protected void tick() {
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 50, 0, true, true));
        updateAttribute(Attribute.MAX_HEALTH);
        updateAttribute(Attribute.SCALE);
        getPlayer().setHealthScale(SMPRPG.getService(EntityService.class).getPlayerInstance(getPlayer()).getHealthScale());
        new ParticleBuilder(Particle.SMOKE)
                .location(getPlayer().getEyeLocation())
                .count(5)
                .offset(.1, .1, .1)
                .spawn();
    }

    @Override
    protected void expire() {
        clearAttributes();
        getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        getPlayer().setHealthScale(SMPRPG.getService(EntityService.class).getPlayerInstance(getPlayer()).getHealthScale());
        getPlayer().playSound(getPlayer().getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, 1, 1);
    }

    @Override
    public void removed() {
        clearAttributes();
        getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        getPlayer().setHealthScale(SMPRPG.getService(EntityService.class).getPlayerInstance(getPlayer()).getHealthScale());
    }

    private void updateAttribute(Attribute attribute) {
        var attr = getPlayer().getAttribute(attribute);
        if (attr == null)
            return;

        attr.removeModifier(ATTRIBUTE_KEY);
        attr.addTransientModifier(new AttributeModifier(ATTRIBUTE_KEY, -0.99999, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    private void clearAttributes() {
        var hp = getPlayer().getAttribute(Attribute.MAX_HEALTH);
        if (hp != null)
            hp.removeModifier(ATTRIBUTE_KEY);
        var scale = getPlayer().getAttribute(Attribute.SCALE);
        if (scale != null)
            scale.removeModifier(ATTRIBUTE_KEY);
    }
}
