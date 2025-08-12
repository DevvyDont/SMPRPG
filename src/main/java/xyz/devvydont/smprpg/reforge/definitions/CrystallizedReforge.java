package xyz.devvydont.smprpg.reforge.definitions;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffectType;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry;
import xyz.devvydont.smprpg.items.attribute.ScalarAttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;
import xyz.devvydont.smprpg.util.time.TickTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CrystallizedReforge extends ReforgeBase implements Listener {

    public static final int ABILITY_RADIUS = 5;

    public CrystallizedReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Provides a").append(ComponentUtils.create(" SIGNIFICANT", NamedTextColor.GOLD)).append(ComponentUtils.create(" boost")),
                ComponentUtils.create("in combat capabilities"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Shatter Bonus", NamedTextColor.BLUE),
                ComponentUtils.merge(ComponentUtils.create("When defeating an "), ComponentUtils.create("enemy", NamedTextColor.RED), ComponentUtils.create(", damage is")),
                ComponentUtils.merge(ComponentUtils.create("reflected", NamedTextColor.AQUA), ComponentUtils.create(" to any "), ComponentUtils.create("enemies", NamedTextColor.RED)),
                ComponentUtils.merge(ComponentUtils.create("within a "), ComponentUtils.create(ABILITY_RADIUS + " block", NamedTextColor.GREEN), ComponentUtils.create(" radius"))
        );
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                new ScalarAttributeEntry(AttributeWrapper.STRENGTH, SpicyReforge.getDamageBonus(rarity) + .2),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, SpicyReforge.getCriticalBonus(rarity) * 1.5),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_CHANCE, 20 + rarity.ordinal() * 10),
                new MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, .05 * rarity.ordinal())
        );
    }

    @Override
    public int getPowerRating() {
        return 5;
    }

    @EventHandler
    public void __onEntityDeath(EntityDeathEvent event) {

        // Only listen to hostile creatures.
        if (!(event.getEntity() instanceof Enemy))
            return;

        // Only listen if there was a player killer that is holding something with this reforge.
        var killer = event.getEntity().getKiller();
        if (killer == null)
            return;

        var mainHandItem = killer.getInventory().getItemInMainHand();
        var mainHandReforge = SMPRPG.getService(ItemService.class).getReforge(mainHandItem);
        if (mainHandReforge == null)
            return;

        if (!mainHandReforge.getType().equals(this.getType()))
            return;

        // Get nearby enemies.
        var nearby = event.getEntity().getLocation().getNearbyLivingEntities(ABILITY_RADIUS, 1);
        var targets = new ArrayList<LivingEntity>();
        for (var entity : nearby)
            if (entity instanceof Enemy)
                targets.add(entity);
        targets.remove(event.getEntity());

        if (targets.isEmpty())
            return;

        // Work out how much damage we are dealing. All we do is distribute the dead entity's max HP amongst nearby enemies.
        var damage = 1000.0;
        var hp = event.getEntity().getAttribute(Attribute.MAX_HEALTH);
        if (hp != null)
            damage = hp.getValue();
        damage /= targets.size();

        // Damage the targets on the next tick if they are not dead.
        double finalDamage = damage;
        Bukkit.getScheduler().runTaskLater(SMPRPG.getPlugin(), task -> {
            for (var target : targets) {
                if (target.isDead())
                    continue;
                target.damage(finalDamage, DamageSource.builder(DamageType.MAGIC).withDirectEntity(killer).withCausingEntity(killer).build());
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_TURTLE_EGG_BREAK, 1, 2);
                new ParticleBuilder(Particle.END_ROD).location(target.getEyeLocation()).count(3).offset(.25, .1, .25).spawn();
            }
        }, TickTime.TICK);
    }
}
