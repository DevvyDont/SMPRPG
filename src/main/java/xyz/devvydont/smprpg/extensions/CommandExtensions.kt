package xyz.devvydont.smprpg.extensions

import com.mojang.brigadier.context.CommandContext

/**
 * Used as a shortcut to get an argument from a command builder.
 * You would usually have to do something like:
 * `val player: PlayerSelectorArgumentResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)`
 *
 * With this, you can instead just do:
 * `val player: PlayerSelectorArgumentResolver = ctx.getArg("player")`
 * OR
 * `val player = ctx.getArg<PlayerSelectorArgumentResolver>("player")`
 */
inline fun <reified T> CommandContext<*>.getArg(name: String): T =
    this.getArgument(name, T::class.java)