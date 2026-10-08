package xyz.devvydont.smprpg.entity.slayer.illager.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import io.papermc.paper.entity.LookAnchor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.*
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.ability.listeners.PlayerFreezeService
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.SlayerBossInstance
import xyz.devvydont.smprpg.entity.slayer.illager.IllagerWarlockParent
import xyz.devvydont.smprpg.events.slayer.SlayerSpawnBossEvent
import xyz.devvydont.smprpg.services.AttributeService
import xyz.devvydont.smprpg.services.EntityDamageCalculatorService
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.slayer.quest.SlayerQuest
import xyz.devvydont.smprpg.slayer.quest.SlayerQuest.SlayerQuestState
import xyz.devvydont.smprpg.util.goals.GoalUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.time.TickTime
import java.util.*
import kotlin.random.Random

class IllagerWarlockSpellGoal(val slayer : IllagerWarlockParent, val spawnPlayer : Player?, val spellCastFrequency: Int, spellOptions : Map<SpellType, Double>) :
    Goal<Evoker> {

    enum class SpellType {
        TELEPORT,
        FIREBALL,
        VEX,
        FANGS,
        TOSS
    }

    enum class TeleportType {
        BEHIND_PLAYER,
        SHORT_RANDOM,
        LONG_RANDOM,
        TELEPORT_TO_ME  // Only used when nobody is nearby, not rolled in random
    }

    enum class FangType {
        LINE,
        ENCIRCLE,
        SELF_ENCIRCLE
    }

    val evoker                        = slayer.entity as Evoker     // This Illager Warlock's vanilla entity object
    var spellClock                    = spellCastFrequency          // Internal clock, reduced by 1 every tick. Used for spell decisions
    var nextSpell : SpellType         = SpellType.TELEPORT          // The next spell that is queued in the spellcast loop
    val spells : ArrayList<SpellType> = ArrayList()                 // Weighted ArrayList of spells that this Illager Warlock can use.
    var lastGroundedY : Double        = 0.0                         // Storage variable for the last Y value that this Illager Warlock was grounded at. Used for fangs.
    val freezeService = SMPRPG.getService(PlayerFreezeService::class.java)
    var movementFrozen                = false                       // Boolean flag for if this Illager Warlock should pathfind towards players
    var previousSpell                 = SpellType.TELEPORT          // Previous spell cache, used for spell decisions
    var nextCast : Int                = spellCastFrequency          // How many ticks on spellcast loop cycle should elapse before a new spell is cast.

    // Teleport spell vars
    val validRandomTypes = ArrayList<TeleportType>()                // Valid types of TeleportType that can be rolled in the random teleport choice
    var teleportType : TeleportType? = TeleportType.BEHIND_PLAYER   // Type of teleport to be used for a teleport spell.

    init {
        for (spellOption in spellOptions) {
            val range = (spellOption.value * 100).toInt()
            for (i in 1..range) {
                spells.add(spellOption.key)
            }
        }

        for (teleportType in TeleportType.entries) {
            if (teleportType != TeleportType.TELEPORT_TO_ME)
                validRandomTypes.add(teleportType)
        }
    }

    override fun shouldActivate(): Boolean {
        if (!slayer.activelyInvolvedPlayers.isEmpty())
            return true
        return false
    }

    override fun getKey(): GoalKey<Evoker> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.MOVE)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
        spellClock = spellCastFrequency
    }

    override fun stop() {
        spellClock = -1
    }

    override fun tick() {
        // Keep track of the last time we were grounded, and at what Y level we were at.
        // This is done in case we get knocked around in the air so things like fangs dont spawn mid air
        if (evoker.isOnGround)
            lastGroundedY = evoker.location.y

        // Keep an eye on our closest target.
        val closestPlayer : Player? = GoalUtils.getClosestPlayer(evoker, 20.0, spawnPlayer)
        if (closestPlayer != null) {
            if (movementFrozen)
                evoker.pathfinder.stopPathfinding()
            else
                evoker.pathfinder.moveTo(closestPlayer, 0.25)
            evoker.lookAt(closestPlayer)
        }

        // As long as our spell clock is active, we need to be selecting generic spells.
        if (spellClock > 0) {
            spellClock--
            if (spellClock == nextCast) {
                // Time to pick a spell!

                // First pick from our weighted spells, then we will do some logic to refine our choice
                nextSpell = spells.random()
                val numPlayersVeryNearby = evoker.world.getNearbyPlayers(evoker.location, 8.0).size
                val numPlayersNearby = evoker.world.getNearbyPlayers(evoker.location, 20.0).size

                //if (SpellType.TOSS in spells) {
                //    // Crowd control for toss spell
                //    // No need to crowd control if it's just one player nearby, just rely on regular weighted chance to toss.
//
                //    if (numPlayersVeryNearby != 1) {
                //        // If at least 60% of the actively involved players are right next to the boss, he needs to yeet them away
                //        if (numPlayersVeryNearby >= slayer.activelyInvolvedPlayers.size * 0.6) {
                //            nextSpell = SpellType.TOSS
                //        }
                //    }
                //}

                if (nextSpell == SpellType.TELEPORT) {
                    // Nobody in a 20 block radius? Force them to us.
                    if (numPlayersNearby == 0) {
                        teleportType = TeleportType.TELEPORT_TO_ME
                    }
                }

                when (nextSpell) {
                    SpellType.TELEPORT, SpellType.VEX, SpellType.FANGS -> {
                        evoker.world.playSound(evoker.location, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1f, 1f)
                        evoker.world.playSound(evoker.location, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f)

                        if (nextSpell == SpellType.TELEPORT) {
                            evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_TELEPORT_CAST.toString(), 1f, 1f)
                            evoker.spell = Spellcaster.Spell.DISAPPEAR  // Set our spell to cast about 1 second before teleport
                        }
                        else if (nextSpell == SpellType.VEX) {
                            evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_VEX_CAST.toString(), 1f, 1f)
                            evoker.spell = Spellcaster.Spell.SUMMON_VEX
                        }
                        else if (nextSpell == SpellType.FANGS) {
                            evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_FANGS_CAST.toString(), 1f, 1f)
                            evoker.spell = Spellcaster.Spell.FANGS
                        }
                    }
                    SpellType.FIREBALL -> {
                        evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_FIREBALL_CAST.toString(), 1f, 1f)
                        evoker.world.playSound(evoker.location, Sound.ENTITY_GHAST_SHOOT, 1f, 0.5f)
                        evoker.world.playSound(evoker.location, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f)
                        evoker.spell = Spellcaster.Spell.WOLOLO  // Set our spell to cast about 1 second before teleport
                    }

                    SpellType.TOSS -> {
                        evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_TOSS_CAST.toString(), 1f, 1f)
                        evoker.world.playSound(evoker.location, KeyStore.AUDIO_ILLAGER_WARLOCK_TOSS_CAST_VO.toString(), 1f, 1f)
                        evoker.spell = Spellcaster.Spell.BLINDNESS

                        val nearbyPlayers = evoker.world.getNearbyPlayers(evoker.location, 20.0)
                        movementFrozen = true

                        for (player in slayer.activelyInvolvedPlayers) {
                            // Throw nearby players
                            if (player in nearbyPlayers) {
                                // Start by launching our players up
                                player.velocity = Vector(0.0, 1.0, 0.0)

                                // 9 Ticks later, freeze them in air.
                                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                                    player.velocity.y = 0.0
                                    player.lookAt(evoker, LookAnchor.EYES, LookAnchor.EYES)  // Lock player's eyes onto the evoker
                                }, TickTime.TICK * 9)

                                // 10 Ticks later, queue up our freeze watchers
                                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                                    freezeService.addMovementWatch(player.uniqueId)  // Freeze player in place, and lock their mouse movement
                                    freezeService.addOrientationWatch(player.uniqueId)
                                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                                        launchPlayer(player, 4.0)
                                    }, TickTime.TICK * 30)
                                }, TickTime.TICK * 10)
                            }
                        }
                    }
                }
            }

            if (spellClock == 0) {
                // Time to cast the spell!
                spellClock = spellCastFrequency
                nextCast = spellClock - 1
                evoker.spell = Spellcaster.Spell.NONE  // Clear our spell

                when (nextSpell) {
                    SpellType.TELEPORT -> {
                        // Pick a random teleport type to do

                        var type = teleportType
                        if (teleportType == null)
                            type = validRandomTypes.random()
                        when (type) {
                            TeleportType.BEHIND_PLAYER -> {
                                var player = slayer.activelyInvolvedPlayers.random()
                                val behindLocation =
                                    player.location.block // Block the player is currently positioned at (relative to feet)
                                        .getRelative(player.facing.oppositeFace) // Get block facing the OPPOSITE direction that the player is facing (facing east = get block west of the player)
                                        .location // Return the location of that block
                                teleportTo(evoker, behindLocation)
                            }

                            TeleportType.SHORT_RANDOM -> teleportRandomlySafe(evoker, 3.0)
                            TeleportType.LONG_RANDOM -> teleportRandomlySafe(evoker, 6.0)
                            TeleportType.TELEPORT_TO_ME -> {
                                for (player in slayer.activelyInvolvedPlayers) {
                                    teleportRandomlySafe(player, 8.0)
                                }
                            }

                            null -> throw IllegalStateException("Illager Warlock tried to use 'null' TeleportType!")
                        }
                        spellClock = (spellCastFrequency / 2)  // Teleport casts directly into another spell
                        nextCast = spellClock - 1
                        teleportType = null
                    }

                    SpellType.FIREBALL -> {
                        val fireball = evoker.launchProjectile(
                            Fireball::class.java,
                            evoker.location.getDirection().normalize().multiply(2)
                        )
                        SMPRPG.getService(EntityDamageCalculatorService::class.java)
                            .setBaseProjectileDamage(
                                fireball,
                                AttributeService.instance.getOrCreateAttribute(evoker, AttributeWrapper.STRENGTH).value
                            )
                        slayer.fireballs.add(fireball)
                    }

                    SpellType.VEX -> {
                        val evokerVec = evoker.location.toVector().normalize()
                        val vexVec = evokerVec.multiply(3)
                        for (i in 0..3) {
                            vexVec.rotateAroundY(90.0 * i)
                            val vex = evoker.world.spawnEntity(
                                evoker.eyeLocation.add(
                                    vexVec.toLocation(
                                        evoker.world,
                                        0.0f,
                                        30.0f
                                    )
                                ), EntityType.VEX, CreatureSpawnEvent.SpawnReason.CUSTOM
                            )
                            val leveledVex = SMPRPG.getService(EntityService::class.java).getEntityInstance(vex)
                            leveledVex.setup()
                            leveledVex.setLevel(slayer.level - 10)
                        }
                    }

                    SpellType.FANGS -> {
                        // Pick a random fang type attack to use.
                        var type = FangType.entries.toTypedArray().random()
                        if (closestPlayer == null)
                            type = FangType.SELF_ENCIRCLE  // Fallback in case we have a targeted fang attack, but no players nearby.
                        when (type) {
                            FangType.LINE -> {
                                evoker.lookAt(closestPlayer as Entity)  // Force an update to look at our closest player
                                val startLoc = evoker.location
                                startLoc.y = lastGroundedY
                                val endLoc = closestPlayer.location
                                val dirVector = evoker.eyeLocation.direction.normalize()

                                object : BukkitRunnable() {
                                    private var clock = 0

                                    override fun run() {
                                        val dist = startLoc.distance(endLoc) * (clock / 10.0)
                                        val currLocation = startLoc.clone().add(  // Clone our start location
                                            startLoc.toVector()
                                                .normalize()  // Normalize the start location as a vector to get our heading
                                                .multiply(dist)  // Multiply the vector to get how far from the start we are in this iteration
                                                .multiply(dirVector) // Then multiply by our direction vector to make it face the right way
                                                .multiply(2)  // Then multiply by 2 to make it extend a bit past the end point
                                                .toLocation(evoker.world)  // Convert to location
                                        )
                                        currLocation.y = lastGroundedY
                                        spawnFang(currLocation)
                                        clock++
                                        if (clock > 10) {
                                            this.cancel()
                                        }
                                    }
                                }.runTaskTimer(plugin, TickTime.INSTANTANEOUSLY, TickTime.INSTANTANEOUSLY)
                            }

                            FangType.ENCIRCLE -> {
                                val playerLoc = closestPlayer!!.location
                                spawnFang(playerLoc)

                                val rotateVec = Vector(4, 0, 0)
                                for (i in 0..10) {
                                    val fang = spawnFang(playerLoc.clone().add(rotateVec.clone().rotateAroundY(20.0 * i)))
                                    fang.attackDelay = i
                                }
                            }
                            FangType.SELF_ENCIRCLE -> {
                                val evokerLoc = evoker.location
                                evokerLoc.y = lastGroundedY

                                val rotateVec = Vector(4, 0, 0)
                                for (i in 0..2) {
                                    rotateVec.x = 4 + (i * 2.0)
                                    for (j in 0..10) {
                                        val fang = spawnFang(evokerLoc.clone().add(rotateVec.clone().rotateAroundY(20.0 * j)))
                                        fang.attackDelay = i * 5
                                    }
                                }
                            }
                        }
                    }

                    SpellType.TOSS -> {
                        spellClock += TickTime.seconds(1).toInt()  // Give 1 second extra to the spellClock to let this longer spell cast.
                        movementFrozen = false
                    }
                }
                previousSpell = nextSpell  // Cache our previously used spell for our spell choice logic
            }
        }
    }

    fun teleportRandomlySafe(entity : Entity, diameter : Double) : Boolean {
        var teleported = false
        val startLoc = evoker.location
        val destLoc = startLoc.clone()

        // Make 16 attempts to teleport, the chances of this failing are pretty low unless forced
        for (i in 0..15) {
            destLoc.x = startLoc.x + Random.nextDouble(-1 * diameter, diameter)
            destLoc.z = startLoc.z + Random.nextDouble(-1 * diameter, diameter)
            destLoc.y = startLoc.y
            var yAttempts = 0

            // Loop through (diameter) times until we find a collidable block to stand on (so block above should NOT be collidable)
            if (!entity.world.getBlockAt(destLoc).isPassable) {
                while (yAttempts < diameter) {
                    destLoc.y++
                    yAttempts++
                    if (entity.world.getBlockAt(destLoc).isPassable)
                        break
                }
            }

            // If the block we found isn't collidable, it means we found a safe place to teleport.
            var blockAt = entity.world.getBlockAt(destLoc)
            if (blockAt.isPassable) {
                teleported = true
                break
            }
        }

        if (teleported) {
            teleportTo(entity, destLoc)
            entity.world.playSound(entity.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
        }

        return teleported
    }

    fun teleportTo(entity : Entity, location : Location) {
        entity.world.spawnParticle(Particle.WITCH, evoker.location, 100, 0.2, 0.0, 0.2, 1.0)
        entity.teleport(location)
        entity.world.playSound(evoker.location, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f)
    }

    fun spawnFang(location : Location) : EvokerFangs {
        val fang: EvokerFangs = evoker.world.spawnEntity(
            location,
            EntityType.EVOKER_FANGS,
            CreatureSpawnEvent.SpawnReason.CUSTOM
        ) as EvokerFangs
        fang.owner = evoker
        return fang
    }

    fun launchPlayer(player : Player, power : Double) {
        freezeService.removeMovementWatch(player.uniqueId)
        freezeService.removeOrientationWatch(player.uniqueId)

        player.playSound(player.location, Sound.ENTITY_WITHER_SHOOT, 1f, 2f)
        player.playSound(player.location, Sound.ENTITY_ARROW_SHOOT, 1f, 0.5f)
        player.velocity = Vector(1, 1, 1).multiply(player.location.direction.normalize()).multiply(power * -1.0)
    }

    companion object {
        val GOAL_KEY : GoalKey<Evoker> = GoalKey.of(
            Evoker::class.java,
            NamespacedKey(plugin, "illager_warlock_teleport_goal")
        )
    }

}