package xyz.devvydont.smprpg.enchantments.definitions.vanilla

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEntryAddEvent
import io.papermc.paper.registry.event.RegistryEvents
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.enchantments.EnchantmentRarity


/**
 * Acts as a wrapper for vanilla enchantments with no extra behavior. Allows us to define descriptions for them
 */
abstract class UnchangedEnchantment(key: TypedKey<Enchantment>) : VanillaEnchantment(key) {

    override fun bootstrap(context: BootstrapContext) {
        check(!isBootstrapped) { "Enchantment " + javaClass.getName() + " is already bootstrapped!" }

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.entryAdd()
                .newHandler(LifecycleEventHandler { event: RegistryEntryAddEvent<Enchantment, EnchantmentRegistryEntry.Builder> ->
                    event.builder()
                        .description(displayName)
                        .weight(weight)
                }) // Configure the handled to only be called for the Vanilla sharpness enchantment.
                .filter(typedKey)
        )

        bootstrapCompleted()
    }

    override val maxLevel: Int get() = enchantment.maxLevel
    override val weight: Int get() = EnchantmentRarity.UNCOMMON.weight
    override val equipmentSlotGroup: EquipmentSlotGroup? get() = EquipmentSlotGroup.ANY
}
