package xyz.devvydont.smprpg.entity.slayer.shambling

import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.monster.zombie.Zombie
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Sound

class ShamblingNMSEntity(val level : Level) : Zombie(level) {
    val world = level.world

    override fun playStepSound(pos: BlockPos, block: BlockState) {
        world.playSound(this.bukkitEntity, Sound.ENTITY_CAMEL_HUSK_STEP, 1f, 0.5f)
    }

    override fun playAmbientSound() {
        world.playSound(this.bukkitEntity, Sound.ENTITY_CAMEL_HUSK_AMBIENT, 1f, 0.75f)
    }

    override fun playHurtSound(damageSource: DamageSource) {
        world.playSound(this.bukkitEntity, Sound.ENTITY_CAMEL_HUSK_HURT, 1f, 0.75f)
    }

    override fun playSecondaryHurtSound(damageSource: DamageSource) {
        world.playSound(this.bukkitEntity, Sound.ENTITY_CAMEL_HUSK_HURT, 1f, 0.75f)
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.CAMEL_HUSK_DEATH
    }
}