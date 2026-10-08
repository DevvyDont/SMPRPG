package xyz.devvydont.smprpg.slayer.quest

import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils

enum class SlayerType(val spawnFlag : String) {
    SHAMBLING_ABOMINATION(ShamblingAbominationParent.SPAWN_MOB_FLAG),
    PIGLIN_WARLORD("nyi"),
    ILLAGER_WARLOCK(IllagerWarlockParent.SPAWN_MOB_FLAG);

    fun display(): String {
        return MinecraftStringUtils.getTitledString(this.name)
    }
}