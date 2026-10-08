package xyz.devvydont.smprpg.items.interfaces

import xyz.devvydont.smprpg.slayer.quest.SlayerType

interface ISlayerProficiencyBoost {
    val slayerToBoost : SlayerType
    val slayerProficiencyBoost : Int
}
