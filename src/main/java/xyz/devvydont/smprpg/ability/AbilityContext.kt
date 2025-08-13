package xyz.devvydont.smprpg.ability

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.EquipmentSlot

@JvmRecord
data class AbilityContext(@JvmField val caster: LivingEntity, @JvmField val hand: EquipmentSlot?)
