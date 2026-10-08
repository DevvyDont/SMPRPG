package xyz.devvydont.smprpg.commands.items

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.enchantments.CustomEnchantment
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.extensions.queryEnum
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import java.util.concurrent.CompletableFuture

class CommandEnchant(val root: String) : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        val entityArgument = Commands.argument("entities", ArgumentTypes.entities())

        val enchantArgument = Commands.argument("enchant", ArgumentTypes.key())
            .suggests(this::getEnchantmentSuggestions)

        val levelArgument = Commands.argument("level", IntegerArgumentType.integer(0, 255))

        return Commands.literal(root)
            .requires { ctx -> ctx.sender.hasPermission("smprpg.command.enchant") }
            .then(entityArgument
                .then(enchantArgument
                    .then(levelArgument
                        .executes(this::executeEnchant))
                )
            )
            .build()
    }

    /**
     * Build the suggestions when filling out the attribute parameter.
     */
    private fun getEnchantmentSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {

        var argSoFar: Key? = null
        try {
            argSoFar = ctx.getArg<Key>("enchant")
        } catch (e: IllegalArgumentException) {}

        for (enchantment in EnchantmentService.CUSTOM_ENCHANTMENTS) {
            val dummyEnchantment = enchantment.build(1)
            val description = dummyEnchantment.description
            val enchantKey = enchantment.enchantment.key
            if (argSoFar == null || enchantKey.value().contains(argSoFar.value()) || enchantKey.asString().contains(argSoFar.asString()) || enchantKey.namespace.contains(argSoFar.value()))
                builder.suggest(enchantment.enchantment.key.asString(), MessageComponentSerializer.message().serialize(
                    description
                ))
        }
        return builder.buildFuture()
    }

    private fun executeEnchant(ctx: CommandContext<CommandSourceStack>): Int {

        val enchant = ctx.getArg<Key>("enchant")
        val level = ctx.getArg<Int>("level")

        // Attempt to match an enchant.
        val enchantMatches = mutableListOf<CustomEnchantment>()
        for (otherEnchantment in EnchantmentService.CUSTOM_ENCHANTMENTS) {
            // First check if we have an exact key match.
            if (enchant == otherEnchantment.key) {
                enchantMatches.add(otherEnchantment)
                break
            }

            // Check if maybe this is an enchantment that is custom, but they didn't provide a key.
            if (otherEnchantment.key!!.value() == enchant.value())
                enchantMatches.add(otherEnchantment)
        }

        if (enchantMatches.isEmpty()) {
            ctx.source.executor?.sendMessage(ComponentUtils.error("Could not find enchantment with name: $enchant"))
            return Command.SINGLE_SUCCESS
        }

        if (enchantMatches.count() > 1) {
            ctx.source.executor?.sendMessage(ComponentUtils.error("Found too many matching enchants. Please be extremely specific! Candidates: $enchantMatches"))
            return Command.SINGLE_SUCCESS
        }

        val enchantmentToApply = enchantMatches.first().build(level)

        var invalidEntities = 0
        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is LivingEntity) {
                invalidEntities++
                continue
            }

            val equipment = entity.equipment
            if (equipment == null) {
                invalidEntities++
                continue
            }

            val item = equipment.itemInMainHand
            if (item.type == Material.AIR) {
                invalidEntities++
                continue
            }

            item.addUnsafeEnchantment(enchantmentToApply.enchantment, level)
            ItemService.blueprint(item).updateItemData(item)

            val enchantComponent = ComponentUtils.merge(
                ComponentUtils.create(Symbols.SPARKLES, enchantmentToApply.enchantColor),
                ComponentUtils.SPACE,
                enchantmentToApply.displayName.color(enchantmentToApply.enchantColor),
                ComponentUtils.SPACE,
                ComponentUtils.create("$level", enchantmentToApply.enchantColor)
            )
            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                entity.name().colorIfAbsent(NamedTextColor.GREEN),
                ComponentUtils.SPACE,
                ComponentUtils.create("had their"),
                ComponentUtils.SPACE,
                item.displayName(),
                ComponentUtils.SPACE,
                ComponentUtils.create("enchanted with"),
                ComponentUtils.SPACE,
                enchantComponent
            ), NamedTextColor.GREEN))
        }

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) could not have the item in their main hand enchanted!"))

        return Command.SINGLE_SUCCESS
    }

    private fun resolveEntitiesFromArgument(ctx: CommandContext<CommandSourceStack>): Collection<Entity> {
        return ctx.getArg<EntitySelectorArgumentResolver>("entities").resolve(ctx.source)
    }
}
