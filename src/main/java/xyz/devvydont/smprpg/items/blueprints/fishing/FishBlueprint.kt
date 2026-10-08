package xyz.devvydont.smprpg.items.blueprints.fishing

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ItemService

/**
 * Represents a blueprint for a basic fish that you can fish up.
 */
class FishBlueprint(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IModelOverridden, ISellable, IEdible {

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    /**
     * Because we don't want fish to interact with furnaces, all custom fish should be clownfish items under the hood.
     * We can however overwrite what the fish looks like here. If we don't define a material, we are going to assume
     * we want the item to display as defined in [CustomItemType].
     * @return The material texture override.
     */
    override fun getDisplayKey(): Key { return IModelOverridden.ofItemTypeInDirectory(customItemType, "consumables/fish") }

    override fun getWorth(item: ItemStack): Int { return getBaseWorth(this.customItemType) * item.amount }

    override fun canAlwaysEat(item: ItemStack?): Boolean { return false }

    override fun getNutrition(item: ItemStack?): Int { return getNutrition(this.customItemType) }

    override fun getSaturation(item: ItemStack?): Float { return getSaturation(this.customItemType).toFloat() }

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        val component = Consumable.consumable()
            .consumeSeconds(1.6f)

        // Conditionally add effects for any "weird" fish.
        if (this.customItemType == CustomItemType.PUFFERFISH) component.addEffect(
            ConsumeEffect.applyStatusEffects(
                listOf(PotionEffect(PotionEffectType.POISON, 20, 0, false, false)),
                1.0f
            )
        )

        return component.build()
    }

    companion object {
        /**
         * Get the base worth of this fish before applying a rarity modifier on it.
         * This essentially maps the COMMON worth of this item.
         * @return The base worth.
         */
        fun getBaseWorth(fish: CustomItemType): Int {
            return when (fish) {
                CustomItemType.COD -> 75
                CustomItemType.CARP -> 55
                CustomItemType.SALMON -> 100
                CustomItemType.GUPPY -> 120
                CustomItemType.SNAPPER -> 450
                CustomItemType.CATFISH -> 600
                CustomItemType.PUFFERFISH -> 500
                CustomItemType.CLOWNFISH -> 800
                CustomItemType.BASS -> 1000
                CustomItemType.YELLOWFIN_TUNA -> 7500
                CustomItemType.BARRACUDA -> 4000
                CustomItemType.PIKE -> 4500
                CustomItemType.STURGEON -> 5500
                CustomItemType.BLUE_TANG -> 7000
                CustomItemType.GOLIATH_GROUPER -> 35000
                CustomItemType.LEAFY_SEADRAGON -> 30000
                CustomItemType.LIONFISH -> 25000
                CustomItemType.BLUE_MARLIN -> 90000
                CustomItemType.FANGTOOTH -> 100000
                CustomItemType.DEEP_SEA_ANGLERFISH -> 125000
                CustomItemType.BLISTERFISH -> 180
                CustomItemType.IMPLING -> 215
                CustomItemType.CRIMSONFISH -> 750
                CustomItemType.BONE_MAW -> 4500
                CustomItemType.SOUL_SCALE -> 7500
                CustomItemType.FLAREFIN -> 50000
                CustomItemType.GHOST_FISH -> 60000
                CustomItemType.DEVIL_RAY -> 200000
                CustomItemType.VOIDFIN -> 225
                CustomItemType.ORBLING -> 300
                CustomItemType.WARPER -> 1500
                CustomItemType.BLOBFISH -> 2000
                CustomItemType.GOBLIN_SHARK -> 40000
                CustomItemType.STARSURFER -> 175000
                CustomItemType.ABYSSAL_SQUID -> 250000
                CustomItemType.TWILIGHT_ANGLERFISH -> 500000
                CustomItemType.COSMIC_CUTTLEFISH -> 750000
                else -> 50
            }
        }

        /**
         * Maps fish types to the amount of nutrition (hunger bars) that they replenish.
         * @param fish The fish type.
         * @return The amount of half hunger bars to replenish.
         */
        fun getNutrition(fish: CustomItemType): Int {
            // Lazy for now, just use the rarity.
            return (fish.DefaultRarity.ordinal + 1) * 2
        }

        /**
         * Maps fish types to the amount of saturation (healing efficiency and delayed hunger decay) that they replenish.
         * @param fish The fish type.
         * @return The amount of saturation.
         */
        fun getSaturation(fish: CustomItemType): Int {
            // Lazy for now, just use the rarity.
            return (fish.DefaultRarity.ordinal + 1) * 2
        }
    }
}
