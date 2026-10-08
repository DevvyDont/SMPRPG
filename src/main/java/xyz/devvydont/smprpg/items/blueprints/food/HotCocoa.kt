@file:Suppress("UnstableApiUsage")

package xyz.devvydont.smprpg.items.blueprints.food

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import io.papermc.paper.registry.keys.SoundEventKeys
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineFoodBlueprint
import xyz.devvydont.smprpg.items.interfaces.IEdible
import xyz.devvydont.smprpg.services.ItemService

class HotCocoa(itemService: ItemService, type: CustomItemType) : CraftEngineFoodBlueprint(itemService, type) {

    override fun getConsumableComponent(item: ItemStack): Consumable {
        return Consumable.consumable()
            .consumeSeconds(IEdible.DEFAULT_EAT_SPEED)
            .sound(SoundEventKeys.ENTITY_GENERIC_DRINK)
            .animation(ItemUseAnimation.DRINK)
            .hasConsumeParticles(false)
            .build()
    }
}