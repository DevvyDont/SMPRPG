package xyz.devvydont.smprpg.commands.items;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.commands.CommandSimplePlayer;
import xyz.devvydont.smprpg.gui.MenuReforgeBrowser;

import java.util.Collection;
import java.util.List;

public class CommandReforges extends CommandSimplePlayer {

    public CommandReforges(String name) {
        super(name);
    }

    @Override
    protected void playerInvoked(@NotNull Player player, @NotNull CommandSourceStack ctx, @NotNull String @NotNull [] args) {
        new MenuReforgeBrowser(player).openMenu();
    }

    @Override
    public Collection<String> getAliases() {
        return List.of(
                "listreforges",
                "allreforges",
                "reforgemenu"
        );
    }
}
