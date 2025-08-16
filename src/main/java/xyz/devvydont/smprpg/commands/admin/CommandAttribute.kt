package xyz.devvydont.smprpg.commands.admin

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
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
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Entity
import org.bukkit.inventory.EquipmentSlotGroup
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.extensions.queryEnum
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.concurrent.CompletableFuture

/**
 * A complex command that allows users to modify attributes of entities.
 * This command has bulk edit support as well using Minecraft's "selector" arguments (@e, @a etc.) so that the user
 * may perform mass attribute queries/edits on multiple entities, even for custom attributes.
 */
class CommandAttribute : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        val entityArgument = Commands.argument("entities", ArgumentTypes.entities())

        val attributeArgument = Commands.argument("attribute", StringArgumentType.string())
            .suggests(this::getAttributeSuggestions)

        val baseOperationTree = Commands.literal("base")
            .then(Commands.literal("get")
                .executes(this::executeBaseGet))
            .then(Commands.literal("set")
                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                    .executes(this::executeBaseSet)))

        val modifierOperationTree = Commands.literal("modifier")
            .then(Commands.literal("add")
                .then(Commands.argument("key", ArgumentTypes.namespacedKey())
                    .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                        .then(Commands.argument("operation", StringArgumentType.string())
                            .executes(this::executeModifierAdd)
                            .suggests(this::getOperationSuggestions))
                        )))
            .then(Commands.literal("clear")
                .executes(this::executeModifierClear))
            .then(Commands.literal("remove")
                .then(Commands.argument("key", ArgumentTypes.namespacedKey())
                    .suggests(this::getPresentModifierSuggestions)
                    .executes(this::executeModifierRemove)))

        return Commands.literal("attribute")
            .requires { ctx -> ctx.sender.hasPermission("smprpg.command.attribute") }
            .then(entityArgument
                .then(attributeArgument
                    .then(baseOperationTree)
                    .then(Commands.literal("get")
                        .executes(this::executeFinalGet))
                    .then(modifierOperationTree)
                )
            )
            .build()
    }

    /**
     * Build the suggestions when filling out the attribute parameter.
     */
    private fun getAttributeSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        for (attribute in AttributeWrapper.entries) {
            var description = attribute.Description
            if (description == null)
                description = ComponentUtils.create("Unknown attribute description", NamedTextColor.RED)
            builder.suggest(attribute.name.lowercase(), MessageComponentSerializer.message().serialize(
                description
            ))
        }
        return builder.buildFuture()
    }

    /**
     * Build the suggestions when filling out the operation parameter.
     */
    private fun getOperationSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) : CompletableFuture<Suggestions> {
        builder.suggest(AttributeModifier.Operation.ADD_NUMBER.name.lowercase(), MessageComponentSerializer.message().serialize(
            MiniMessage.miniMessage().deserialize("Adds a <green>flat additive</green> modifier to the attribute")
        ))
        builder.suggest(AttributeModifier.Operation.ADD_SCALAR.name.lowercase(), MessageComponentSerializer.message().serialize(
            MiniMessage.miniMessage().deserialize("Adds an <green>additive multiplier</green> modifier to the attribute")
        ))
        builder.suggest(AttributeModifier.Operation.MULTIPLY_SCALAR_1.name.lowercase(), MessageComponentSerializer.message().serialize(
            MiniMessage.miniMessage().deserialize("Adds a <light_purple>multiplicative</light_purple> modifier to the attribute")
        ))
        return builder.buildFuture()
    }

    /**
     * Checks all the entities present in the selector argument, and queries all modifier keys that are present
     * for the given attribute.
     */
    private fun getPresentModifierSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder) : CompletableFuture<Suggestions> {
        val entities = resolveEntitiesFromArgument(ctx)
        val keys = HashSet<String>()

        val attribute: AttributeWrapper? = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
        if (attribute == null)
            return builder.buildFuture()

        for (entity in entities) {

            if (entity !is Attributable)
                continue

            val wrapper = AttributeService.instance.getAttribute(entity, attribute)
            if (wrapper == null)
                continue

            for (modifier in wrapper.modifiers)
                keys.add(modifier.key.asString())
        }

        for (key in keys)
            builder.suggest(key)

        return builder.buildFuture()
    }

    private fun executeBaseGet(ctx: CommandContext<CommandSourceStack>): Int {

        var invalidEntities = 0
        var notPresent = 0
        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
            if (attributeType == null) {
                ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
                return Command.SINGLE_SUCCESS
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                entity.name().colorIfAbsent(NamedTextColor.GREEN),
                ComponentUtils.SPACE,
                ComponentUtils.create("has a base"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("value of"),
                ComponentUtils.SPACE,
                ComponentUtils.create("${attribute.baseValue}", NamedTextColor.GREEN),
            ), NamedTextColor.GREEN))
        }

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun executeBaseSet(ctx: CommandContext<CommandSourceStack>): Int {
        var invalidEntities = 0
        var notPresent = 0
        var success = 0

        val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
        if (attributeType == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
            return Command.SINGLE_SUCCESS
        }
        val value = ctx.getArg<Double>("value")

        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            attribute.baseValue = value
            attribute.save(entity, attributeType)
            success += 1
        }

        if (success > 0)
            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(

                ComponentUtils.create("$success entity(s) have had their base"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("value set to"),
                ComponentUtils.SPACE,
                ComponentUtils.create("$value", NamedTextColor.GREEN),

            ), NamedTextColor.GREEN))

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun executeFinalGet(ctx: CommandContext<CommandSourceStack>): Int {

        var invalidEntities = 0
        var notPresent = 0
        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
            if (attributeType == null) {
                ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
                return Command.SINGLE_SUCCESS
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(
                entity.name().colorIfAbsent(NamedTextColor.GREEN),
                ComponentUtils.SPACE,
                ComponentUtils.create("has a final"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("value of"),
                ComponentUtils.SPACE,
                ComponentUtils.create("${attribute.value}", NamedTextColor.GREEN),
            ), NamedTextColor.GREEN))
        }

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun executeModifierClear(ctx: CommandContext<CommandSourceStack>): Int {
        var invalidEntities = 0
        var notPresent = 0
        var success = 0

        val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
        if (attributeType == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
            return Command.SINGLE_SUCCESS
        }

        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            attribute.clearModifiers()
            attribute.save(entity, attributeType)
            success += 1
        }

        if (success > 0)
            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(

                ComponentUtils.create("$success entity(s) have had their"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("modifiers cleared"),
                ), NamedTextColor.GREEN))

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun executeModifierAdd(ctx: CommandContext<CommandSourceStack>): Int {
        var invalidEntities = 0
        var notPresent = 0
        var success = 0

        val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
        if (attributeType == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
            return Command.SINGLE_SUCCESS
        }

        val key = ctx.getArg<NamespacedKey>("key")
        val amount = ctx.getArg<Double>("value")
        val operation = queryEnum<AttributeModifier.Operation>(ctx.getArg("operation"))
        if (operation == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Invalid operation: $operation"))
            return Command.SINGLE_SUCCESS
        }
        val modifier = AttributeModifier(key, amount, operation, EquipmentSlotGroup.ANY)
        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            attribute.addModifier(modifier)
            attribute.save(entity, attributeType)
            success += 1
        }

        if (success > 0)
            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(

                ComponentUtils.create("$success entity(s) have had the"),
                ComponentUtils.SPACE,
                ComponentUtils.create(key.asString(), NamedTextColor.GREEN),
                ComponentUtils.SPACE,
                ComponentUtils.create("modifier applied to their"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("attribute"),
            ), NamedTextColor.GREEN))

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun executeModifierRemove(ctx: CommandContext<CommandSourceStack>): Int {
        var invalidEntities = 0
        var notPresent = 0
        var success = 0

        val attributeType = queryEnum<AttributeWrapper>(ctx.getArg<String>("attribute"))
        if (attributeType == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Invalid attribute: $attributeType"))
            return Command.SINGLE_SUCCESS
        }

        val key =ctx.getArg<NamespacedKey>("key")
        for (entity in resolveEntitiesFromArgument(ctx)) {

            if (entity !is Attributable) {
                invalidEntities += 1
                continue
            }

            val attribute = AttributeService.instance.getAttribute(entity, attributeType)
            if (attribute == null) {
                notPresent += 1
                continue
            }

            attribute.removeModifier(key)
            attribute.save(entity, attributeType)
            success += 1
        }

        if (success > 0)
            ctx.source.sender.sendMessage(ComponentUtils.alert(ComponentUtils.merge(

                ComponentUtils.create("$success entity(s) have had the"),
                ComponentUtils.SPACE,
                ComponentUtils.create(key.asString(), NamedTextColor.GREEN),
                ComponentUtils.SPACE,
                ComponentUtils.create("modifier removed from their"),
                ComponentUtils.SPACE,
                ComponentUtils.create(attributeType.name.lowercase(), NamedTextColor.GOLD),
                ComponentUtils.SPACE,
                ComponentUtils.create("attribute"),
            ), NamedTextColor.GREEN))

        if (notPresent > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$notPresent entity(s) did not have this attribute registered!"))

        if (invalidEntities > 0)
            ctx.source.sender.sendMessage(ComponentUtils.error("$invalidEntities entity(s) are not allowed to have attributes!"))

        return Command.SINGLE_SUCCESS
    }

    private fun resolveEntitiesFromArgument(ctx: CommandContext<CommandSourceStack>): Collection<Entity> {
        return ctx.getArg<EntitySelectorArgumentResolver>("entities").resolve(ctx.source)
    }
}
