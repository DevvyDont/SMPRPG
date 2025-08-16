package xyz.devvydont.smprpg.ability.handlers

import org.bukkit.entity.Item
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.ability.AbilityContext
import xyz.devvydont.smprpg.ability.AbilityHandler
import xyz.devvydont.smprpg.services.DropsService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Admin ability. Used for an admin item. Teleports and untags any nearby items in a 25 block radius.
 */
class ItemSweepAbilityHandler : AbilityHandler {
    /**
     * Attempts to execute the ability.
     *
     * @param ctx The context of the ability.
     * @return True if the ability succeeded and should have cost reduced, false otherwise.
     */
    override fun execute(ctx: AbilityContext): Boolean {
        val canUse = ctx.caster.permissionValue("smprpg.item.itemsweep").toBooleanOrElse(false) || ctx.caster.isOp
        if (!canUse) {
            ctx.caster.sendMessage(ComponentUtils.error("You cannot use this item."))
            return false
        }

        if (ctx.caster !is Player) return false

        var items = 0
        for (item in ctx.caster.location.getNearbyEntitiesByType<Item>(Item::class.java, 25.0)) {
            SMPRPG.getService(DropsService::class.java).setOwner(item, ctx.caster)
            item.owner = ctx.caster.uniqueId
            item.teleport(ctx.caster.location)
            items++
        }

        ctx.caster.sendMessage(ComponentUtils.success("Teleported and untagged $items items to you!"))
        return true
    }
}
