package xyz.devvydont.smprpg.reforge.definitions;

import com.destroystokyo.paper.ParticleBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.fishing.SeaCreature;
import xyz.devvydont.smprpg.items.ItemRarity;
import xyz.devvydont.smprpg.items.attribute.AttributeEntry;
import xyz.devvydont.smprpg.reforge.ReforgeBase;
import xyz.devvydont.smprpg.reforge.ReforgeType;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.Collection;
import java.util.List;

public class SirenicReforge extends ReforgeBase implements Listener {

    public static final int DODGE_CHANCE = 15;

    public SirenicReforge(ReforgeType type) {
        super(type);
    }

    @Override
    public Collection<AttributeEntry> getAttributeModifiersWithRarity(ItemRarity rarity) {
        return List.of(
                AttributeEntry.additive(AttributeWrapper.FISHING_RATING, 30),
                AttributeEntry.additive(AttributeWrapper.FISHING_CREATURE_CHANCE, AlluringReforge.getChance(rarity) / 2),
                AttributeEntry.scalar(AttributeWrapper.STRENGTH, .1),
                AttributeEntry.additive(AttributeWrapper.CRITICAL_DAMAGE, 15)
        );
    }

    /**
     * An item lore friendly list of components to display as a vague description of the item for what it does
     * @return
     */
    @Override
    public List<Component> getDescription() {
        return List.of(
                ComponentUtils.create("Drastically increases chance"),
                ComponentUtils.merge(ComponentUtils.create("to fish up "), ComponentUtils.create("Sea Creatures", SeaCreature.NAME_COLOR)),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Siren's Aura Bonus:", NamedTextColor.BLUE),
                ComponentUtils.merge(ComponentUtils.create("When attacked by a "), ComponentUtils.create("Sea Creature", SeaCreature.NAME_COLOR), ComponentUtils.create(", you")),
                ComponentUtils.merge(ComponentUtils.create("have a "), ComponentUtils.create(DODGE_CHANCE + "%", NamedTextColor.GREEN), ComponentUtils.create(" chance to dodge the attack!")),
                ComponentUtils.create("Apply to multiple pieces", NamedTextColor.DARK_GRAY),
                ComponentUtils.create("to boost the effect!", NamedTextColor.DARK_GRAY)
        );
    }

    /**
     * How much should we increase the power rating of an item if this container is present?
     *
     * @return
     */
    @Override
    public int getPowerRating() {
        return 3;
    }

    /**
     * When an entity receives damage, work out how many stacks of the reforge they have. If the attacker is a sea
     * creature, we have a chance to negate the damage!
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    private void __onReceiveDamageFromSeaCreature(EntityDamageEvent event) {

        var damager = event.getDamageSource().getCausingEntity();
        if (damager == null)
            return;

        if (!(damager instanceof LivingEntity))
            return;

        var damagerWrapper = SMPRPG.getService(EntityService.class).getEntityInstance(damager);
        if (!(damagerWrapper instanceof SeaCreature<?>))
            return;

        if (!(event.getEntity() instanceof LivingEntity living))
            return;

        var equipment = living.getEquipment();
        var sirenicStacks = 0;

        if (equipment == null)
            return;

        for (var armor : equipment.getArmorContents())
            if (hasReforge(armor))
                sirenicStacks++;
        if (hasReforge(equipment.getItemInMainHand()))
            sirenicStacks++;
        if (hasReforge(equipment.getItemInOffHand()))
            sirenicStacks++;

        var chance = sirenicStacks * DODGE_CHANCE;
        var roll = Math.random() * 100;
        if (roll > chance)
            return;

        event.setCancelled(true);
        living.setNoDamageTicks(20);
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.ENTITY_BREEZE_DEATH, 1, 1.5f);
        new ParticleBuilder(Particle.FLASH)
                .location(event.getEntity().getLocation().add(0, 1, 0))
                .spawn();
    }
}
