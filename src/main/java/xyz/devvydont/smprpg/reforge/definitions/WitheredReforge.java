package xyz.devvydont.smprpg.reforge.definitions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class WitheredReforge extends ReforgeBase implements Listener {

    public static float getDamageBuff(ItemRarity rarity) {
        return switch (rarity) {
            case COMMON -> .4f;
            case UNCOMMON -> .45f;
            case RARE -> .5f;
            case EPIC -> .55f;
            case LEGENDARY -> .65f;
            case MYTHIC -> .7f;
            case DIVINE -> .75f;
            case TRANSCENDENT -> .75f;
            case SPECIAL -> .75f;
        };
    }

    public WitheredReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD)).append(ComponentUtils.create(" boost")),
                ComponentUtils.create("in attack damage and attack speed"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Withered Bonus", NamedTextColor.BLUE),
                ComponentUtils.create("Deal ").append(ComponentUtils.create("+50%", NamedTextColor.GREEN)).append(ComponentUtils.create(" damage to enemies who")),
                ComponentUtils.create("have the ").append(ComponentUtils.create("withered", NamedTextColor.DARK_RED).append(ComponentUtils.create(" potion effect")))
        );
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, getDamageBuff(rarity)),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 2 + rarity.ordinal() * 2),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, 2 + rarity.ordinal() * 2),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .10f)
        );
    }

    @Override
    public int getPowerRating() {
        return 5;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDealDamageToWitheredEnemies(CustomEntityDamageByEntityEvent event) {

        // Is the damaged entity withered?
        if (!(event.getDamaged() instanceof LivingEntity damaged))
            return;

        if (damaged.getPotionEffect(PotionEffectType.WITHER) == null)
            return;

        if (!(event.getDealer() instanceof LivingEntity living))
            return;

        if (living.getEquipment() == null)
            return;

        boolean hasWitheredReforge = hasReforge(living.getEquipment().getItemInMainHand()) || hasReforge(living.getEquipment().getItemInOffHand());
        if (!hasWitheredReforge)
            return;

        // We have the withered reforge and the one getting attacked is withered. 2x the damage
        event.multiplyDamage(1.5);
    }
}
