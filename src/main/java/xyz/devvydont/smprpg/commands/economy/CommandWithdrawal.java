package xyz.devvydont.smprpg.commands.economy;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.commands.CommandSimplePlayer;
import xyz.devvydont.smprpg.gui.economy.MenuWithdraw;

public final class CommandWithdrawal extends CommandSimplePlayer {
    public CommandWithdrawal(String name) {
        super(name);
    }

    @Override
    protected void playerInvoked(@NotNull Player player, @NotNull CommandSourceStack ctx, @NotNull String @NotNull [] args) {
        new MenuWithdraw(player).openMenu();
    }
}
