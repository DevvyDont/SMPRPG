package xyz.devvydont.smprpg.blockbreaking

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import com.destroystokyo.paper.ParticleBuilder
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.data.Waterlogged
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.enchantments.definitions.MinersFervorArtificeEnchantment
import xyz.devvydont.smprpg.hooks.WorldGuardHook
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.interfaces.IFueledEquipment
import xyz.devvydont.smprpg.listeners.damage.popup.DamagePopup
import xyz.devvydont.smprpg.listeners.damage.popup.PopupStyleResolver
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.isBroken
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*
import kotlin.math.max
import kotlin.math.roundToInt

class BlockDamage {
    val plugin: SMPRPG = SMPRPG.plugin

    fun configureBreakingPacket(player: Player, block: Block) {
        val breakingAnimation: PacketContainer = manager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)


        // this ensures that the player won't conflict with another player's breaking animation
        var entityId = player.entityId + 1
        entityId = entityId * 1000


        breakingAnimation.integers.write(0, entityId)
        breakingAnimation.blockPositionModifier.write(0, BlockPosition(block.x, block.y, block.z))

        breakingTimeCheck(player, block, breakingAnimation)
    }

    private fun breakingTimeCheck(player: Player, block: Block, breakingAnimation: PacketContainer) {
        val breakingTimeTicks = getBreakingTime(player, block)


        // Check if the breakingTime is instant/unbreakable
        if (breakingTimeTicks <= 0) {
            if (breakingTimeTicks == -1.0) return
            playerBreakBlock(player, block)
            return
        }

        startBreaking(player, breakingAnimation, breakingTimeTicks, block)
    }

    private fun startBreaking(
        player: Player,
        breakingAnimation: PacketContainer,
        breakingTimeTicks: Double,
        originalBlock: Block
    ) {
        val taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, object : Runnable {
            var currentTicks: Double = 0.0

            override fun run() {
                // stops breaking if player isn't actively breaking the block

                if (!(PacketManager.armSwinging.containsKey(player.uniqueId))) {
                    Bukkit.getScheduler().cancelTask(scheduleId.get(player.uniqueId)!!.taskId)
                    scheduleId.remove(player.uniqueId)


                    // returns the breaking animation back to none
                    breakingAnimation.integers.write(1, -1)
                    for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                        manager.sendServerPacket(onlinePlayer, breakingAnimation)
                    }
                    return
                }

                val currentTarget = player.getTargetBlockExact(5)


                // removes any progress if mining from block onto air and cancels this task
                if (currentTarget == null) {
                    Bukkit.getScheduler().cancelTask(scheduleId.get(player.uniqueId)!!.taskId)
                    scheduleId.remove(player.uniqueId)


                    // returns the breaking animation back to none
                    breakingAnimation.integers.write(1, -1)
                    for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                        manager.sendServerPacket(onlinePlayer, breakingAnimation)
                    }
                    return
                }


                // If we have reached the block HP threshold, break the block and let the online players know.
                if (currentTicks >= breakingTimeTicks) {
                    // Sets the final breaking animation
                    breakingAnimation.integers.write(
                        1,
                        10
                    ) // Set to 10 to remove break stage. Anything not within 0-9 unsigned byte range uses no texture.
                    for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                        manager.sendServerPacket(onlinePlayer, breakingAnimation)
                    }

                    playerBreakBlock(player, originalBlock)
                    Bukkit.getScheduler().cancelTask(scheduleId.get(player.uniqueId)!!.taskId)
                    scheduleId.remove(player.uniqueId)
                    return
                } else {
                    var multiplier = 0.1
                    for (x in 0..9) {
                        if (currentTicks <= (breakingTimeTicks * multiplier)) {
                            breakingAnimation.integers.write(1, x - 1)
                            // For active animations, we want to only update nearby players, since this packet will be sent rapidly.
                            for (nearbyPlayer in getPlayersInRange(originalBlock)) {
                                manager.sendServerPacket(nearbyPlayer, breakingAnimation)
                            }
                            break
                        }
                        multiplier += 0.1
                    }
                }

                var tickInc = 1.0
                if (isEyesInWater(player)) tickInc *= instance.getOrCreateAttribute(
                    player,
                    AttributeWrapper.UNDERWATER_MINING
                ).getValue()

                if (!player.isOnGround) tickInc *= instance.getOrCreateAttribute(
                    player,
                    AttributeWrapper.AIRBORNE_MINING
                ).getValue()

                currentTicks = currentTicks + tickInc
            }
        }, 0L, 1L)

        scheduleId.put(player.uniqueId, ScheduleTask(taskId, originalBlock))
    }

    /**
     * Determines whether the player's eyes are submerged in water, mirroring vanilla's underwater mining
     * penalty which only applies when the eye line is in a water fluid. This intentionally checks the eye
     * location rather than the player's feet/body, so a player merely standing in shallow water is not
     * penalized.
     */
    private fun isEyesInWater(player: Player): Boolean {
        val eyeBlock = player.eyeLocation.block
        if (eyeBlock.type == Material.WATER || eyeBlock.type == Material.BUBBLE_COLUMN)
            return true

        val data = eyeBlock.blockData
        return data is Waterlogged && data.isWaterlogged
    }

    private fun getBreakingTime(player: Player, block: Block): Double {
        var speedMultiplier = 100.0

        // Check if held item has a proper tool component. If it doesn't, assume unarmed
        val item = player.equipment.itemInMainHand
        val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(item)
        var entry = BlockPropertiesRegistry.get(block)
        val preferredTools: MutableSet<ItemClassification?>?

        // Failfast if entry is null.
        if (entry == null) {
            val tools = mutableSetOf<ItemClassification?>()
            var entryBp = 0.0f
            if (Tag.MINEABLE_AXE.isTagged(block.type)) {
                tools.add(ItemClassification.AXE)
                tools.add(ItemClassification.HATCHET)
            }
            if (Tag.MINEABLE_PICKAXE.isTagged(block.type)) {
                tools.add(ItemClassification.PICKAXE)
                tools.add(ItemClassification.DRILL)
            }
            if (Tag.MINEABLE_SHOVEL.isTagged(block.type)) {
                tools.add(ItemClassification.SHOVEL)
                tools.add(ItemClassification.DRILL)
            }
            if (Tag.MINEABLE_HOE.isTagged(block.type)) {
                tools.add(ItemClassification.HOE)
                tools.add(ItemClassification.HATCHET)
            }

            if (Tag.INCORRECT_FOR_DIAMOND_TOOL.isTagged(block.type))
                entryBp = 4.0f
            else if (Tag.INCORRECT_FOR_IRON_TOOL.isTagged(block.type))
                entryBp = 3.0f
            else if (Tag.INCORRECT_FOR_STONE_TOOL.isTagged(block.type))
                entryBp = 2.0f
            else if (Tag.INCORRECT_FOR_WOODEN_TOOL.isTagged(block.type))
                entryBp = 1.0f

            entry = BlockPropertiesEntry.builder(*tools.toTypedArray())
                .hardness(block.type.hardness * 100)
                .breakingPower(entryBp)
                .softRequirement(tools.isEmpty())
                .build()
            BlockPropertiesRegistry.register(block.type, entry)
            //player.sendMessage(
            //    ComponentUtils.alert(
            //        ComponentUtils.merge(
            //            ComponentUtils.create(
            //                "This block is missing block properties! Tell a developer that the following block is not defined: ",
            //                NamedTextColor.RED
            //            ),
            //            ComponentUtils.create(block.blockData.asString, NamedTextColor.WHITE),
            //            ComponentUtils.create("\nBlockState: ", NamedTextColor.LIGHT_PURPLE),
            //            ComponentUtils.create(block.toString(), NamedTextColor.GRAY)
            //        )
            //    )
            //)
            SMPRPG.plugin.logger
                .warning("Unknown block entry " + block.blockData.asString + ". Dynamically created for now, but please add it to BlockPropertiesRegistry!")
            //player.world.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.75f)
            //return -1.0
            preferredTools = entry.preferredTools
        } else if (blueprint is IFueledEquipment) {
            val maxFuel = (blueprint as IFueledEquipment).getMaxFuel(item)
            val fuelUsed = (blueprint as IFueledEquipment).getFuelUsed(item)
            if ((fuelUsed >= (maxFuel - IFueledEquipment.FUEL_OFFSET))) {
                player.sendMessage(
                    ComponentUtils.alert(
                        ComponentUtils.merge(
                            ComponentUtils.create(
                                "Your tool is out of fuel! Refuel it by crafting your tool with furnace fuels in a crafting grid.",
                                NamedTextColor.RED
                            )
                        )
                    )
                )
                player.world.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.75f)
                return -1.0
            }
            preferredTools = entry.preferredTools
        } else preferredTools = entry.preferredTools

        // Do a quick attribute scan to remove Miner's Fervor modifier if we aren't holding a properly enchanted item.
        if (!item.containsEnchantment(EnchantmentService.MINERS_FERVOR.enchantment)) {
            val miningSpeedInst = instance.getOrCreateAttribute(player, AttributeWrapper.MINING_SPEED)
            miningSpeedInst.removeModifier(MinersFervorArtificeEnchantment.MODIFIER_KEY)
            miningSpeedInst.save(player, AttributeWrapper.MINING_SPEED)
        }

        // Instant breaking if this block is flagged to instabreak with this tool classification
        val instabreakToolsArr = entry.instabreakTools
        if (instabreakToolsArr != null) {
            val instabreakTools = setOf(*instabreakToolsArr)
            if (instabreakTools.contains(blueprint.itemClassification)) {
                return 0.0
            }
        }

        var isPreferred = false
        var correctTool = false
        if (preferredTools != null) {
            correctTool = preferredTools.contains(blueprint.itemClassification)
            isPreferred = correctTool || entry.softRequirement
        }

        if (isPreferred)  // Only add extra mining speed once we know this is a preferred break option
        {
            if (correctTool) {
                speedMultiplier -= 100.0 // Subtract our implicit 100 speed given for unarmed/non-tool options
                speedMultiplier += instance.getOrCreateAttribute(player, AttributeWrapper.MINING_SPEED).getValue()
            }
        }

        // Haste gives +100 mining speed per level, Mining Fatigue gives -100 mining speed per level.
        if (player.hasPotionEffect(PotionEffectType.HASTE)) speedMultiplier += (400 * player.getPotionEffect(
            PotionEffectType.HASTE
        )!!
            .amplifier).toDouble()
        if (player.hasPotionEffect(PotionEffectType.MINING_FATIGUE)) speedMultiplier -= (400 * player.getPotionEffect(
            PotionEffectType.MINING_FATIGUE
        )!!
            .amplifier).toDouble()

        var damage: Double


        // Checks for a custom hardness.
        val hardness: Float

        // Failsafe, block is unbreakable if not properly defined.
        val playerBp = instance.getOrCreateAttribute(player, AttributeWrapper.MINING_POWER).getValue()
        if (playerBp >= entry.breakingPower && (correctTool || entry.softRequirement)) {
            hardness = entry.hardness
            if (hardness <= -1) return -1.0
            if (Bukkit.getServer().pluginManager.getPlugin("WorldGuard") != null)
                if (!WorldGuardHook.isLocationBreakable(block.location, player)) return -1.0
        } else {
            val popupLoc = player.eyeLocation.toVector()
            popupLoc.midpoint(block.location.add(Vector(0.5, 0.0, 0.5)).toVector())
            popupLoc.subtract(Vector(0.25, 0.0, 0.25))
            val warningLocation = popupLoc.toLocation(block.world)

            // Track whether this warning is brand new so we only play the "denied" sound on a fresh
            // label. Spamming a failing block simply re-anchors and replays the existing warning.
            var spawnedFreshWarning = false
            if (!correctTool) {
                checkNotNull(preferredTools)
                if (preferredTools.isNotEmpty()) {
                    spawnedFreshWarning = DamagePopup.spawnMiningWarning(
                        player,
                        warningLocation,
                        PopupStyleResolver.requiresTool(),
                        "Requires " + preferredTools.toTypedArray()[0].toString().lowercase(
                            Locale.getDefault()
                        )
                    )
                }
            } else spawnedFreshWarning = DamagePopup.spawnMiningWarning(
                player,
                warningLocation,
                PopupStyleResolver.miningPower(entry.breakingPower),
                entry.breakingPower.toDouble()
            )

            if (spawnedFreshWarning)
                player.world.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f)

            return -1.0
        }
        damage = speedMultiplier / hardness

        if (isPreferred && !isBroken(item)) damage /= 30.0
        else damage /= 100.0

        damage = max(damage, 0.0) // Prevents negative mining speed.

        // Instant breaking
        if (damage > 1) {
            return 0.0
        }


        return (1 / damage).roundToInt().toDouble()
    }

    private fun playerBreakBlock(player: Player, block: Block) {
        val blockSound = BlockPropertiesRegistry.get(block)!!.blockSound
        if (blockSound != null) block.world
            .playSound(block.location, blockSound.BreakSound, blockSound.BreakVolume, blockSound.BreakPitch)
        else block.world.playSound(
            block.location,
            block.blockSoundGroup.breakSound,
            1.0f,
            0.8f
        ) // This is making a WILD assumption that all block breaks use this pitch. We will need to do data entry at some point.

        ParticleBuilder(Particle.BLOCK)
            .location(block.location.toCenterLocation())
            .data(block.blockData)
            .count(40)
            .offset(0.25, 0.25, 0.25)
            .spawn()

        player.breakBlock(block)
    }

    private fun getPlayersInRange(block: Block): Set<Player> {
        val retSet = mutableSetOf<Player>()
        for (player in block.location.world.players) {
            if (player.location.distanceSquared(block.location) <= broadcastRadiusSq)
                retSet.add(player)
        }
        return retSet
    }

    companion object {
        private val scheduleId = HashMap<UUID, ScheduleTask>()

        private val manager: ProtocolManager = ProtocolLibrary.getProtocolManager()
        private val broadcastRadiusSq: Double = 30.0 * 30.0

        fun cancelTaskWithBlockReset(player: Player) {
            if (scheduleId.containsKey(player.uniqueId)) {
                val block: Block = scheduleId.get(player.uniqueId)!!.block

                Bukkit.getScheduler().cancelTask(scheduleId.get(player.uniqueId)!!.taskId)
                scheduleId.remove(player.uniqueId)


                // this enusres that the player won't conflict with another player's breaking animation
                var entityId = player.entityId + 1
                entityId = entityId * 1000
                val breakingAnimation: PacketContainer =
                    manager.createPacket(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)

                breakingAnimation.integers.write(0, entityId)
                breakingAnimation.blockPositionModifier
                    .write(0, BlockPosition(block.x, block.y, block.z))


                // returns the breaking animation back to none
                breakingAnimation.integers.write(1, -1)
                for (onlinePlayer in Bukkit.getOnlinePlayers())
                    manager.sendServerPacket(onlinePlayer, breakingAnimation)
            }
        }
    }
}