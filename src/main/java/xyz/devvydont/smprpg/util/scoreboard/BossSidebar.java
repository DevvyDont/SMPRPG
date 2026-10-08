package xyz.devvydont.smprpg.util.scoreboard;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundResetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A per-viewer boss sidebar rendered by sending scoreboard packets straight to each viewer.
 *
 * <p>Historically bosses showed their sidebar by cloning the main scoreboard and swapping involved
 * players onto the clone. The sidebar display slot is per-scoreboard, so a separate scoreboard was the
 * only way to show boss-specific lines to only some players. The cost was that the clone was a frozen
 * snapshot: global systems (below-name HP, item rarity glow) only ever write to the main scoreboard, so
 * any player viewing the clone lost those features.
 *
 * <p>This class removes the clone. Every player stays on the main scoreboard at all times, and the boss
 * sidebar is sent to involved players as a purely client-side objective. Because the sidebar objective has
 * no server-side counterpart, it never collides with real objectives and the server never sends conflicting
 * updates for it. With a single scoreboard in play, below-name HP and rarity glow keep working for everyone.
 *
 * <p>Packets are constructed and sent directly against the server's internals. ProtocolLib cannot build the
 * modern scoreboard packets on this server version (it fails to populate the {@code Optional<NumberFormat>}
 * fields), so we go straight to the source. Lines are rendered using each score's optional display component;
 * the right-aligned score numbers are hidden with a blank number format on the objective.
 */
public class BossSidebar {

    private static final int MAX_LINES = 16;

    // Sidebar scores render highest-first, so line 0 (top) gets the largest score and we count down.
    private static final int TOP_LINE_SCORE = MAX_LINES - 1;

    // Stable, unique owner strings for each sidebar line. The display component overrides their rendering,
    // so the actual contents only need to be distinct.
    private static final String[] LINE_ENTRIES = new String[MAX_LINES];

    static {
        for (int i = 0; i < MAX_LINES; i++)
            LINE_ENTRIES[i] = "§" + Integer.toHexString(i);
    }

    // Objective names share a client-side namespace, so each sidebar instance needs a unique one to avoid
    // two simultaneous bosses interfering with each other's sidebar.
    private static int instanceCounter = 0;

    private static synchronized String nextObjectiveName() {
        instanceCounter = (instanceCounter + 1) & 0xFFFF;
        return "boss_" + Integer.toHexString(instanceCounter);
    }

    private final String objectiveName = nextObjectiveName();
    private final Objective objective;

    private final List<Player> players = new ArrayList<>();
    private final List<Component> lines = new ArrayList<>();

    public BossSidebar(Component title) {
        // A detached objective that only carries the data the packets read (title, render type, number format).
        // The blank number format hides the right-aligned score numbers for every line under it.
        this.objective = new Objective(
                new Scoreboard(),
                objectiveName,
                ObjectiveCriteria.DUMMY,
                PaperAdventure.asVanilla(title),
                ObjectiveCriteria.RenderType.INTEGER,
                false,
                BlankFormat.INSTANCE
        );
    }

    /**
     * Starts showing this sidebar to the given player and sends them the current line contents. Idempotent:
     * if the player is already a viewer this does nothing, so callers may invoke it freely (e.g. on every
     * boss hit). Re-creating an objective the client already has is a protocol error that disconnects them.
     */
    public void display(Player player) {
        if (players.contains(player))
            return;
        players.add(player);
        refresh(player);
    }

    /**
     * Re-sends the objective, display slot and every current line to a viewer. Used both on initial display
     * and to recover after a client-side scoreboard reset (e.g. respawn).
     */
    public void refresh(Player player) {
        // Remove before adding so the ADD always targets an objective the client does not currently have.
        // If the client still has it (e.g. its respawn-time scoreboard reset has not fired yet) a second ADD
        // is a protocol error that disconnects them. METHOD_REMOVE for an absent objective is a safe
        // client-side no-op, so this ordering is race-free regardless of when the client clears its state.
        send(player, new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
        send(player, new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_ADD));
        send(player, new ClientboundSetDisplayObjectivePacket(DisplaySlot.SIDEBAR, objective));
        for (int i = 0; i < lines.size(); i++)
            send(player, scorePacket(i, lines.get(i)));
    }

    /**
     * Stops showing this sidebar to the given player by removing the objective from their client.
     */
    public void hide(Player player) {
        if (!players.remove(player))
            return;
        send(player, new ClientboundSetObjectivePacket(objective, ClientboundSetObjectivePacket.METHOD_REMOVE));
    }

    public void cleanup() {
        for (Player player : List.copyOf(players))
            hide(player);
        players.clear();
    }

    public boolean showing(Player player) {
        return players.contains(player);
    }

    public void setLines(Component... lines) {
        setLines(Arrays.asList(lines));
    }

    public void setLines(List<Component> newLines) {
        int newCount = Math.min(newLines.size(), MAX_LINES);
        int oldCount = lines.size();

        for (int i = 0; i < Math.max(newCount, oldCount); i++) {

            boolean hasNew = i < newCount;
            boolean hadOld = i < oldCount;

            if (hasNew) {
                Component line = newLines.get(i);
                // The line's score (its position) never changes for a given index, so only re-send when the
                // rendered text actually changed to avoid spamming packets every tick.
                if (hadOld && lines.get(i).equals(line))
                    continue;
                broadcast(scorePacket(i, line));
            } else {
                broadcast(resetScorePacket(i));
            }
        }

        lines.clear();
        lines.addAll(newLines.subList(0, newCount));
    }

    private Packet<?> scorePacket(int lineIndex, Component line) {
        return new ClientboundSetScorePacket(
                LINE_ENTRIES[lineIndex],
                objectiveName,
                TOP_LINE_SCORE - lineIndex,
                Optional.of(PaperAdventure.asVanilla(line)),
                Optional.empty()
        );
    }

    private Packet<?> resetScorePacket(int lineIndex) {
        return new ClientboundResetScorePacket(LINE_ENTRIES[lineIndex], objectiveName);
    }

    private void send(Player player, Packet<?> packet) {
        if (!player.isOnline())
            return;
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    private void broadcast(Packet<?> packet) {
        for (Player player : players)
            send(player, packet);
    }
}
