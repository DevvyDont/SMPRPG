package xyz.devvydont.smprpg.enchantments.definitions.vanilla

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEntryAddEvent
import io.papermc.paper.registry.event.RegistryEvents
import net.kyori.adventure.key.Key
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.services.EnchantmentService
import java.util.*

abstract class VanillaEnchantment(vanillaTypedKey: TypedKey<Enchantment>) : CustomEnchantment(vanillaTypedKey.key().value()) {
    val vanillaEnchantment: Enchantment get() = SMPRPG.getService(EnchantmentService::class.java).getEnchantment(typedKey)

    init {
        typedKey = vanillaTypedKey
        key = typedKey.key()
    }

    override fun bootstrap(context: BootstrapContext) {
        check(!isBootstrapped) { "Enchantment " + javaClass.getName() + " is already bootstrapped!" }

        context.getLifecycleManager().registerEventHandler(
            RegistryEvents.ENCHANTMENT.entryAdd() // Increase the max level to 10
                .newHandler( { event: RegistryEntryAddEvent<Enchantment, EnchantmentRegistryEntry.Builder> ->
                    event.builder()
                        .description(displayName)
                        .anvilCost(2)
                        .maxLevel(maxLevel)
                        .weight(weight)
                        .exclusiveWith(conflictingEnchantments)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                })
                .filter(typedKey)
        )

        bootstrapCompleted()
    }

    override fun getRecipeKey(level: Int): NamespacedKey {
        return NamespacedKey(
            Key.MINECRAFT_NAMESPACE,
            key.value().lowercase(Locale.getDefault()) + String.format("%s-recipe", level)
        )
    }
}
