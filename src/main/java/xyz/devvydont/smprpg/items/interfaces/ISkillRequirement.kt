package xyz.devvydont.smprpg.items.interfaces

import xyz.devvydont.smprpg.skills.SkillType

interface ISkillRequirement {
    val skillRequirements: MutableMap<SkillType, Int>
}
