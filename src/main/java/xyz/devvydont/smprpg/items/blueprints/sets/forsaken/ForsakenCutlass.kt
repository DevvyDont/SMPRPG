package xyz.devvydont.smprpg.items.blueprints.sets.forsaken

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword.Companion.getSwordDamage
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil.getAbilityComponent

class ForsakenCutlass(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    Listener, IHeaderDescribable, IBreakableEquipment, IRepairable, ISkillRequirement {

    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.PREMIUM_NETHER_STAR))
    override val skillRequirements: MutableMap<SkillType, Int> = mutableMapOf(Pair(SkillType.COMBAT, 25))

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        val components: MutableList<Component?> = ArrayList<Component?>()
        components.add(getAbilityComponent("Necrotic", true))
        components.add(
            ComponentUtils.create("Attacks have a ")
                .append(ComponentUtils.create(WITHER_APPLY_CHANCE.toString() + "%", NamedTextColor.GREEN)).append(
                    ComponentUtils.create(" chance to")
                )
        )
        components.add(
            ComponentUtils.create("apply the ").append(ComponentUtils.create("withered", NamedTextColor.DARK_RED))
                .append(
                    ComponentUtils.create(" effect for ")
                        .append(ComponentUtils.create(WITHER_APPLY_SECONDS.toString() + "s", NamedTextColor.GREEN))
                )
        )

        return components
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, getSwordDamage(Material.NETHERITE_SWORD)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.35),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 10.0)
        )
    }

    override fun getPowerRating(): Int {
        return ForsakenArmorSet.POWER
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }


    override fun getMaxDurability(): Int {
        return ForsakenArmorSet.DURABILITY
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun __onAttackWithCutlass(event: CustomEntityDamageByEntityEvent) {
        // Can the attacked entity have potion effects?

        if (event.damaged !is LivingEntity) return

        // Did the attacker use the cutlass?
        if (event.dealer !is LivingEntity) return

        val dealer = event.dealer

        if (dealer.equipment == null) return

        if (!isItemOfType(dealer.equipment!!.itemInMainHand)) return

        // Is this a direct event?
        if (event.isIndirect) return
        val damaged = event.damaged

        // The cutlass was used. Apply withering if we roll for it and it is not already applied
        if (damaged.hasPotionEffect(PotionEffectType.WITHER)) return

        // RNG roll
        if (Math.random() * 100 < WITHER_APPLY_CHANCE) return

        // Apply!
        damaged.addPotionEffect(PotionEffect(PotionEffectType.WITHER, WITHER_APPLY_SECONDS * 20, 0, true, true))
        damaged.getWorld().playSound(damaged.getLocation(), Sound.ENTITY_WITHER_HURT, .5f, 1.5f)
        damaged.getWorld().spawnParticle(Particle.ASH, damaged.getEyeLocation(), 5)
    }

    companion object {
        const val WITHER_APPLY_CHANCE: Int = 20
        const val WITHER_APPLY_SECONDS: Int = 10
    }
}
