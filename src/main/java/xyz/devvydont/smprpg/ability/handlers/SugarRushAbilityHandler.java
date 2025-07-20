package xyz.devvydont.smprpg.ability.handlers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.ability.AbilityContext;
import xyz.devvydont.smprpg.ability.AbilityHandler;
import xyz.devvydont.smprpg.util.time.TickTime;

public class SugarRushAbilityHandler implements AbilityHandler {

    public static final int BOOST = 50;
    public static final int DURATION = 30;
    public static final NamespacedKey ATTRIBUTE_KEY = new NamespacedKey("smprpg", "speed_boost_ability");

    @Override
    public boolean execute(AbilityContext ctx) {

        // Adds a speed boost to the player, and removes it 30s later.
        var speed = ctx.caster().getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed == null)
            return false;

        if (speed.getModifier(ATTRIBUTE_KEY) != null)
            return false;

        ctx.caster().getWorld().playSound(ctx.caster().getLocation(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, .25f, 2f);
        speed.removeModifier(ATTRIBUTE_KEY);
        speed.addTransientModifier(new AttributeModifier(ATTRIBUTE_KEY, BOOST/100.0, AttributeModifier.Operation.ADD_SCALAR));
        Bukkit.getScheduler().runTaskLater(SMPRPG.getInstance(), () -> speed.removeModifier(ATTRIBUTE_KEY), TickTime.seconds(DURATION));
        return true;
    }
}
