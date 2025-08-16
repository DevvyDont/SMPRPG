package xyz.devvydont.smprpg.commands.player

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.commands.ICommand
import xyz.devvydont.smprpg.entity.player.LeveledPlayer
import xyz.devvydont.smprpg.events.skills.SkillExperienceGainEvent
import xyz.devvydont.smprpg.extensions.getArg
import xyz.devvydont.smprpg.extensions.queryEnum
import xyz.devvydont.smprpg.reforge.ReforgeType
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.SkillService
import xyz.devvydont.smprpg.skills.SkillGlobals.getCumulativeExperienceForLevel
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import java.util.concurrent.CompletableFuture


const val ADMIN_PERMISSION = "smprpg.command.skill.admin"

class CommandSkill : ICommand {

    /**
     * Get the root of the command builder.
     */
    override fun getRoot(): LiteralCommandNode<CommandSourceStack> {

        // This command is a little complex, we have the ability to either reset, or select a skill (or use all), then input a number.
        return Commands.literal("skill")
            .executes(this::executeBaseCommand)
            .then(Commands.literal("reset")
                .requires { ctx -> ctx.sender.hasPermission(ADMIN_PERMISSION) || ctx.sender.isOp }
                .executes(this::executeResetCommand))
            .then(Commands.literal("all")
                .requires { ctx -> ctx.sender.hasPermission(ADMIN_PERMISSION) || ctx.sender.isOp }
                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                    .suggests(this::getLevelSuggestions)
                    .executes(this::executeSetAllSkills)))
            .then(Commands.literal("set")
                .requires { ctx -> ctx.sender.hasPermission(ADMIN_PERMISSION) || ctx.sender.isOp }
                .then(Commands.argument("skill", StringArgumentType.string())
                    .suggests(this::getSkillSuggestions)
                    .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                        .suggests(this::getLevelSuggestions)
                        .executes(this::executeSetSkill))))
            .build()
    }

    private fun executeBaseCommand(context: CommandContext<CommandSourceStack>): Int {
        val target = context.source.executor
        if (target !is Player) {
            context.source.sender.sendMessage(ComponentUtils.error("Target must be a player!"))
            return Command.SINGLE_SUCCESS
        }
        val wrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(target)
        for (line in getSkillDisplay(wrapper))
            context.source.sender.sendMessage(line)
        return Command.SINGLE_SUCCESS
    }

    private fun executeResetCommand(context: CommandContext<CommandSourceStack>): Int {
        val target = context.source.executor
        if (target !is Player) {
            context.source.sender.sendMessage(ComponentUtils.error("Target must be a player!"))
            return Command.SINGLE_SUCCESS
        }

        val wrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(target)
        for (skill in wrapper.skills)
            skill.experience = 0
        context.source.sender.sendMessage(ComponentUtils.success("All skills reset!"))
        SMPRPG.getService(SkillService::class.java).syncSkillAttributes(wrapper)
        return Command.SINGLE_SUCCESS
    }

    private fun setSkillExp(player: LeveledPlayer, skill: SkillType, level: Int) {
        val inst =
            SMPRPG.getService(SkillService::class.java).getNewSkillInstance(player.player, skill)
        val targetExp = getCumulativeExperienceForLevel(level)
        if (targetExp > inst.experience) inst.addExperience(
            targetExp - inst.experience,
            SkillExperienceGainEvent.ExperienceSource.COMMANDS
        )
        else inst.experience = targetExp
        player.entity.sendMessage(ComponentUtils.success("Set the " + skill.displayName + " skill to level " + inst.level))
    }

    private fun executeSetAllSkills(ctx: CommandContext<CommandSourceStack>): Int {
        val level = ctx.getArg<Int>("value")
        val target = ctx.source.executor
        if (target !is Player) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Target must be a player!"))
            return Command.SINGLE_SUCCESS
        }
        val wrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(target)
        for (skill in wrapper.skills)
            setSkillExp(wrapper, skill.type, level)
        SMPRPG.getService(SkillService::class.java).syncSkillAttributes(wrapper)
        ctx.source.sender.sendMessage(ComponentUtils.success("Set all skills to level $level!"))
        return Command.SINGLE_SUCCESS
    }

    private fun executeSetSkill(ctx: CommandContext<CommandSourceStack>): Int {
        val level = ctx.getArg<Int>("value")
        val skillType = queryEnum<SkillType>(ctx.getArg<String>("skill"))
        if (skillType == null) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Unknown skill type!"))
            return Command.SINGLE_SUCCESS
        }
        val target = ctx.source.executor
        if (target !is Player) {
            ctx.source.sender.sendMessage(ComponentUtils.error("Target must be a player!"))
            return Command.SINGLE_SUCCESS
        }
        val wrapper = SMPRPG.getService(EntityService::class.java).getPlayerInstance(target)
        setSkillExp(wrapper, skillType, level)
        SMPRPG.getService(SkillService::class.java).syncSkillAttributes(wrapper)
        return Command.SINGLE_SUCCESS
    }

    private fun getSkillDisplay(player: LeveledPlayer): List<Component> {
        val output: ArrayList<Component> = ArrayList()
        output.add(ComponentUtils.EMPTY)
        for (skill in player.skills) {
            output.add(
                ComponentUtils.merge(
                    ComponentUtils.create(skill.type.displayName + " " + skill.level, NamedTextColor.AQUA),
                    ComponentUtils.create(" - "),
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(skill.experienceProgress.toLong()),
                        NamedTextColor.GREEN
                    ),
                    ComponentUtils.create("/"),
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(skill.nextExperienceThreshold.toLong()),
                        NamedTextColor.GOLD
                    ),
                    ComponentUtils.create(" ("),
                    ComponentUtils.create(
                        MinecraftStringUtils.formatNumber(skill.experience.toLong()) + "XP",
                        NamedTextColor.DARK_GRAY
                    ),
                    ComponentUtils.create(")")
                )
            )
        }
        output.add(ComponentUtils.EMPTY)
        output.add(ComponentUtils.create("Skill Average: "))
        output.add(ComponentUtils.create(String.format("%.2f", player.getAverageSkillLevel()), NamedTextColor.GOLD))
        return output
    }

    /**
     * Build the suggestions when filling out the reforge parameter.
     */
    private fun getSkillSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        for (skill in SkillType.entries)
            builder.suggest(skill.name.lowercase())
        return builder.buildFuture()
    }

    private fun getLevelSuggestions(ctx: CommandContext<CommandSourceStack>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        builder.suggest(0)
        builder.suggest(25)
        builder.suggest(50)
        builder.suggest(75)
        builder.suggest(100)
        return builder.buildFuture()
    }
}