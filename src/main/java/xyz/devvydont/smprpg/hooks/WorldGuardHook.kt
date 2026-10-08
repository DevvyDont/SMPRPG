package xyz.devvydont.smprpg.hooks

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.StateFlag
import org.bukkit.Location
import org.bukkit.entity.Player

class WorldGuardHook {

    companion object {

        /**
         * Whether [player] is allowed to break the block at [loc] according to WorldGuard's region protection.
         */
        fun isLocationBreakable(loc: Location, player: Player? = null): Boolean {
            return isFlagAllowed(loc, player, Flags.BLOCK_BREAK)
        }

        /**
         * Whether [player] is allowed to interact with (use) the block at [loc] according to WorldGuard's
         * region protection. Consults the USE flag, which governs interactable blocks such as anvils, doors,
         * and buttons.
         */
        fun isLocationUsable(loc: Location, player: Player? = null): Boolean {
            return isFlagAllowed(loc, player, Flags.USE)
        }

        /**
         * Whether [player] is permitted the action represented by [flag] at [loc]. The action is allowed unless
         * an applicable region explicitly denies it. Region owners and members are exempt for a given region,
         * but an overlapping region they do not belong to can still deny them.
         */
        private fun isFlagAllowed(loc: Location, player: Player?, flag: StateFlag): Boolean {
            val container = WorldGuard.getInstance().platform.regionContainer
            val query = container.createQuery()
            val regionSet = query.getApplicableRegions(BukkitAdapter.adapt(loc))
            for (region in regionSet) {
                if (player != null && (region.owners.contains(player.uniqueId) || region.members.contains(player.uniqueId)))
                    continue
                if (region.getFlag(flag) == StateFlag.State.DENY)
                    return false
            }
            return true
        }
    }
}
