package xyz.devvydont.smprpg.items.blueprints.tomes.spells

import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.ability.Ability
import xyz.devvydont.smprpg.ability.AbilityActivationMethod
import xyz.devvydont.smprpg.ability.AbilityCost
import xyz.devvydont.smprpg.ability.AbilityCost.Companion.of
import xyz.devvydont.smprpg.ability.handlers.DamageAuraAbilityHandler
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.interfaces.IAbilityCaster.AbilityEntry
import xyz.devvydont.smprpg.services.ItemService

class DamageAuraSpellScroll(itemService: ItemService, type: CustomItemType) : SpellBlueprint(itemService, type) {
    override fun getAbilities(item: ItemStack): Collection<AbilityEntry> {
        return mutableListOf(
            AbilityEntry(
                Ability.DAMAGE_AURA,
                AbilityActivationMethod.RIGHT_CLICK,
                of(AbilityCost.Resource.MANA, 250)
            )
        )
    }

    override fun getCooldown(item: ItemStack): Long {
        return DamageAuraAbilityHandler.COOLDOWN
    }
}