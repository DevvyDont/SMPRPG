package xyz.devvydont.smprpg.entity.slayer.illager.goals

import com.destroystokyo.paper.ParticleBuilder
import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DyedItemColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.damage.DamageSource
import org.bukkit.damage.DamageType
import org.bukkit.entity.Evoker
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

enum class AuraType(val color : DyedItemColor, val breakableColor : DyedItemColor, val sound : Sound) {
    DAMAGE(DyedItemColor.dyedItemColor().color(Color.fromRGB(255, 0, 0)).build(),
        DyedItemColor.dyedItemColor().color(Color.fromRGB(226, 60, 0)).build(),
        Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED),
    MANA(DyedItemColor.dyedItemColor().color(Color.fromRGB(0, 255, 255)).build(),
        DyedItemColor.dyedItemColor().color(Color.fromRGB(0, 173, 235)).build(),
        Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM),
    FATIGUE(DyedItemColor.dyedItemColor().color(Color.fromRGB(255, 255, 0)).build(),
        DyedItemColor.dyedItemColor().color(Color.fromRGB(235, 225, 0)).build(),
        Sound.WEATHER_RAIN_ABOVE)
}


class IllagerWarlockAuraCloudGoal(val slayer : IllagerWarlockParent, val spawnPlayer : Player?, val baseRadius : Double) :
    Goal<Evoker> {

    val evoker = slayer.entity as Evoker
    var shieldAngle = 0.0f
    var lastGroundedY = 0.0
    var hitsLastTick = 0
    val shieldRegen = 0.005
    var shieldDamage = 0.0
    var shieldType = AuraType.DAMAGE
    var timeBeforeShieldSwap = 300
    var interpDuration = 5
    var radius = baseRadius
    var rotating = true

    override fun shouldActivate(): Boolean {
        return true
    }

    override fun getKey(): GoalKey<Evoker> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.LOOK)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun tick() {
        var hitsThisTick = slayer.damageTracker.getTotalNumberOfHits()
        if (evoker.isOnGround)
            lastGroundedY = evoker.location.y

        val groundLoc = Location(evoker.world, evoker.location.x, lastGroundedY, evoker.location.z, 0f, 0f)

        // Shield logic
        if (timeBeforeShieldSwap > 0) {
            timeBeforeShieldSwap--
            if (timeBeforeShieldSwap == 0) {
                evoker.world.spawnParticle(Particle.TOTEM_OF_UNDYING, evoker.location, 100, 0.0, 10.0, 0.0, 0.5)
                updateShieldModel(shieldType, true)
                evoker.world.playSound(evoker.location, Sound.BLOCK_BEACON_DEACTIVATE, 2f, 1f)
            }
        }

        if (rotating) {
            shieldAngle += 0.1f
            shieldAngle = shieldAngle % 360
        }

        // Handle shrinking on hits
        val cap = if (timeBeforeShieldSwap > 0) 0.75 else 1.0
        shieldDamage = min(shieldDamage + ((hitsThisTick - hitsLastTick) * 0.05), cap)
        evoker.world.playSound(groundLoc, shieldType.sound, 0.5f, 1.5f - (shieldDamage.toFloat() * 0.5f))
        radius = baseRadius * (1 - shieldDamage)
        if (radius > baseRadius)
            radius = baseRadius

        if (shieldDamage == 1.0) {
            when (shieldType) {
                AuraType.DAMAGE -> swapShield(AuraType.MANA)
                AuraType.MANA -> swapShield(AuraType.FATIGUE)
                AuraType.FATIGUE -> swapShield(AuraType.DAMAGE)
            }
            timeBeforeShieldSwap = 340
        }

        ParticleBuilder(Particle.INSTANT_EFFECT)
            .location(evoker.location)
            .count(64)
            .data(Particle.Spell(shieldType.color.color(), 2.0f))
            .extra(0.0)
            .offset(radius - 1.0, 0.5, radius - 1.0)
            .spawn()

        val scaleRad = radius.toFloat() * 8.0f
        val display = slayer.shieldDisplay!!

        display.transformation = Transformation(Vector3f(), AxisAngle4f(), Vector3f(scaleRad, 1f, scaleRad), AxisAngle4f(shieldAngle, 0f, 1f, 0f))
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(interpDuration);

        display.teleport(groundLoc)

        for (player in evoker.world.getNearbyPlayers(groundLoc, radius)) {
            if (player in slayer.activelyInvolvedPlayers) {
                if (player.isOnGround) {
                    when (shieldType) {
                        AuraType.DAMAGE -> {
                            player.noDamageTicks = 0
                            player.damage(radius * radius * radius,
                                DamageSource.builder(DamageType.MAGIC).withDirectEntity(evoker).withCausingEntity(evoker)
                                    .build()
                            )
                        }
                        AuraType.MANA -> {
                            val lvlPlayer = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
                            lvlPlayer.useMana((lvlPlayer.maxMana * (radius / 1000.0)).roundToInt())
                            player.playSound(player.location, Sound.BLOCK_BEACON_DEACTIVATE, 1f, 2f)
                        }
                        AuraType.FATIGUE -> {
                            if (player.getPotionEffect(PotionEffectType.MINING_FATIGUE) == null) {
                                player.addPotionEffect(PotionEffect(PotionEffectType.MINING_FATIGUE, 5 * 20, (radius / 2).roundToInt(), true, false))
                                player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 2, true, false))
                                player.playSound(player.location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 2f)
                                player.spawnParticle(Particle.ELDER_GUARDIAN, player.location, 1)
                            }
                        }
                    }
                }
            }
        }
        hitsLastTick = hitsThisTick
        shieldDamage = max(shieldDamage - shieldRegen, 0.0)
        interpDuration = 5
    }

    fun swapShield(type : AuraType) {
        for (player in slayer.activelyInvolvedPlayers) {
            player.playSound(player, Sound.BLOCK_PORTAL_TRIGGER, 2f, 2f)
        }
        evoker.world.spawnParticle(Particle.END_ROD, evoker.location, 100, 0.0, 0.0, 0.0, 0.125)
        rotating = false
        shieldAngle = 0.0f
        object : BukkitRunnable() {
            override fun run() {
                shieldType = type
                updateShieldModel(type, false)

                radius = baseRadius
                shieldDamage = 0.0

                evoker.world.playSound(evoker.location, Sound.ENTITY_WITHER_SPAWN, 2f, 1f)
                evoker.world.playSound(evoker.location, Sound.BLOCK_BEACON_ACTIVATE, 2f, 1f)
                evoker.world.playSound(evoker.location, Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f)
                interpDuration = 0
                rotating = true
                }
        }.runTaskLater(plugin, TickTime.seconds(2))
    }

    fun updateShieldModel(type : AuraType, breakable : Boolean) {
        val display = slayer.shieldDisplay!!
        val newItem = display.itemStack.clone()
        newItem.setData(DataComponentTypes.DYED_COLOR, if (breakable) type.breakableColor else type.color)
        display.setItemStack(newItem)
    }

    companion object {
        val GOAL_KEY : GoalKey<Evoker> = GoalKey.of(
            Evoker::class.java,
            NamespacedKey(plugin, "illager_warlock_aura_cloud_goal")
        )
    }

}