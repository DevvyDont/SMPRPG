package xyz.devvydont.smprpg.items.blueprints.resources.slayer

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IConsumable
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.extensions.calculateCompressedWorth
import xyz.devvydont.smprpg.util.time.TickTime

class NecroticFleshFamilyBlueprint(itemService: ItemService, type: CustomItemType) :
    CustomItemBlueprint(itemService, type), ISellable, IEdible, IConsumable, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getWorth(itemStack: ItemStack): Int = calculateCompressedWorth(itemStack)

    override fun getNutrition(item: ItemStack?): Int = 0
    override fun getSaturation(item: ItemStack?): Float = 0.0f
    override fun canAlwaysEat(item: ItemStack?): Boolean = true

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        val effects = ArrayList<ConsumeEffect>()

        if (customItemType == CustomItemType.NECROTIC_FLESH_SINGULARITY) effects.add(
            ConsumeEffect.applyStatusEffects(
                listOf(
                    PotionEffect(PotionEffectType.WITHER, TickTime.minutes(5).toInt(), 9),
                    PotionEffect(PotionEffectType.BLINDNESS, TickTime.minutes(5).toInt(), 1),
                    PotionEffect(PotionEffectType.DARKNESS, TickTime.minutes(5).toInt(), 1)
                ), 1f
            )
        )

        if (customItemType == CustomItemType.ENCHANTED_NECROTIC_FLESH) effects.add(
            ConsumeEffect.applyStatusEffects(
                listOf(
                    PotionEffect(PotionEffectType.WITHER, TickTime.minutes(5).toInt(), 4),
                    PotionEffect(PotionEffectType.BLINDNESS, TickTime.minutes(5).toInt(), 0)
                ), 1f
            )
        )

        if (customItemType == CustomItemType.PREMIUM_NECROTIC_FLESH) effects.add(
            ConsumeEffect.applyStatusEffects(
                listOf(
                    PotionEffect(PotionEffectType.WITHER, TickTime.minutes(5).toInt(), 2)
                ), 1f
            )
        )

        return Consumable.consumable()
            .consumeSeconds(10f)
            .addEffects(effects)
            .build()
    }

    override fun getDisplayKey(): Key? {
        return IModelOverridden.ofItemTypeInDirectory(CustomItemType.NECROTIC_FLESH, "materials")
    }
}
