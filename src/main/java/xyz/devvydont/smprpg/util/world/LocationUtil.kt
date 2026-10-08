package xyz.devvydont.smprpg.util.world

import org.bukkit.Location

class LocationUtil {

    companion object {
        fun snapLocationToFloor(location: Location): Location {
            var y = location.y
            // Don't bother iterating if we are above the void.
            if (location.world.getHighestBlockAt(location).y <= location.world.minHeight) return location
            while (!location.world.getBlockAt(location.x.toInt(), y.toInt(), location.z.toInt()).isSolid) {
                y--
                if (location.world.getBlockAt(location.x.toInt(), y.toInt(), location.z.toInt()).isSolid || y <= location.world.minHeight) {
                    location.y = y + 1
                    return location
                }
            }
            location.y = y + 1
            return location
        }
    }
}