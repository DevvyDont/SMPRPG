package xyz.devvydont.smprpg.commands.items;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.commands.CommandSimplePlayer;
import xyz.devvydont.smprpg.gui.items.MenuTrashItems;

public class CommandTrashItems extends CommandSimplePlayer {
    public CommandTrashItems(String name) {
        super(name);
    }

    @Override
    protected void playerInvoked(@NotNull Player player, @NotNull CommandSourceStack ctx, @NotNull String @NotNull [] args) {
        new MenuTrashItems(player).openMenu();
    }
}
