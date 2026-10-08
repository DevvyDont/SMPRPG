package xyz.devvydont.smprpg.items.blueprints.boss

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.entity.SpectralArrow
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.bosses.BlazeBoss
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.ItemRarity
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ChatService
import xyz.devvydont.smprpg.services.DropsService
import xyz.devvydont.smprpg.services.DropsService.Companion.getMillisecondsUntilExpiry
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.function.Consumer

class InfernoArrow(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IHeaderDescribable, ISellable, Listener, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.ITEM

    enum class InfernoSpawnResult(val message: String) {
        SUCCESS("Success!"),
        WRONG_DIMENSION("This only works in the Nether!"),
        NO_LAVA("This arrow must be shot into lava!"),
        NO_ROOM("There is not enough space!"),
        ALREADY_PRESENT("There is already one present!"),
        NOT_ENABLED("You are not prepared for this encounter yet...");
    }

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        var bossName: Component = ComponentUtils.create("Infernal Phoenix", NamedTextColor.DARK_PURPLE)
        var instructions: Component = ComponentUtils.create("shot in lava", NamedTextColor.GOLD)
        // If boss spawns aren't enabled, obfuscate the instructions
        if (!ALLOW_SPAWNING) {
            bossName = bossName.decorate(TextDecoration.OBFUSCATED)
            instructions = instructions.decorate(TextDecoration.OBFUSCATED)
        }

        val components: MutableList<Component?> = ArrayList()
        components.add(
            ComponentUtils.create("Used to summon an ")
                .append(bossName)
        )
        components.add(
            ComponentUtils.create("when ")
                .append(instructions)
                .append(ComponentUtils.create(" in the "))
                .append(ComponentUtils.create("Nether", NamedTextColor.RED))
        )
        return components
    }

    override fun getWorth(item: ItemStack): Int {
        return 20000 * item.amount
    }

    /**
     * Checks if a certain block in the world is a valid spot to spawn an inferno boss.
     *
     * @param block The block in the world to check
     * @return an enum representing the state of the validity of the spawn location, SUCCESS if we should allow it
     */
    private fun checkLocationValidity(block: Block): InfernoSpawnResult {
        // Are we in the nether?

        if (block.world.environment != World.Environment.NETHER) return InfernoSpawnResult.WRONG_DIMENSION

        // Is this block submerged in lava?
        if (block.type != Material.LAVA) return InfernoSpawnResult.NO_LAVA

        // Is one already spawned?
        for (ignored in SMPRPG.getService(EntityService::class.java)
            .getEntitiesOfClass(BlazeBoss::class.java)) return InfernoSpawnResult.ALREADY_PRESENT

        // Is there room to spawn this boss
        for (xOffset in -2..2) for (zOffset in -2..2) for (y in 1..20) if (!isValidSpawnBlock(
                block.location.clone().add(xOffset.toDouble(), y.toDouble(), zOffset.toDouble()).block.type
            )
        ) return InfernoSpawnResult.NO_ROOM

        // Is the boss enabled?
        if (!ALLOW_SPAWNING) return InfernoSpawnResult.NOT_ENABLED

        // Passed all checks!
        return InfernoSpawnResult.SUCCESS
    }

    /*
     * Decide what to do when this arrow is initially shot. For now, we just add a cute little glow :p
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onShootInfernoArrow(event: EntityShootBowEvent) {
        // Is the projectile our arrow?

        if (event.projectile !is SpectralArrow) return
        val arrow = event.projectile as SpectralArrow

        if (!isItemOfType(arrow.itemStack)) return

        val item: ItemStack = arrow.itemStack
        item.editMeta(Consumer { meta: ItemMeta? ->
            if (event.entity is Player) SMPRPG.getService(DropsService::class.java).setOwner(meta!!, event.entity as Player)
            SMPRPG.getService(DropsService::class.java)
                .setExpiryTimestamp(meta!!, System.currentTimeMillis() + getMillisecondsUntilExpiry(defaultRarity))
        })
        arrow.itemStack = item
        SMPRPG.getService(DropsService::class.java).getTeam(ItemRarity.LEGENDARY)
            .addEntity(arrow) // give it a glow :p
        arrow.isGlowing = true
        arrow.hitSound = Sound.BLOCK_AMETHYST_BLOCK_BREAK
        arrow.isInvulnerable = true
    }

    /*
     * We need to disallow our inferno arrow from being lost to being shot at enemies + in lava to burn
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onInfernoArrowCollideWithEntity(event: ProjectileHitEvent) {
        // Is the projectile our arrow?


        if (event.entity !is SpectralArrow) return
        val arrow = event.entity as SpectralArrow

        if (!isItemOfType(arrow.itemStack)) return

        // This arrow is an inferno arrow. Decide what to do what an inferno arrow collides with something.
        // If we hit an entity, this interaction is completely nullified.
        if (event.hitEntity != null) {
            event.isCancelled =true
            arrow.velocity = (arrow.velocity.multiply(-1).multiply(ARROW_VELOCITY_ENTITY_DAMPENING))
            arrow.world.playSound(arrow.location, Sound.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1.5f)
            return
        }

        // If we didn't hit a block, then we can't handle any additional logic. (Player shot us into the void?)
        val block = event.hitBlock
        if (block == null) return

        // Check if we should refund the arrow back to the player because they used it incorrectly.
        val result = checkLocationValidity(arrow.location.block)

        // Is this location invalid?
        if (result != InfernoSpawnResult.SUCCESS) {
            // Delete the arrow entity and spawn the corresponding item stack so they don't lose it.
            val drop = block.world.dropItemNaturally(arrow.location, arrow.itemStack)
            drop.velocity = arrow.velocity.multiply(-1).multiply(ARROW_VELOCITY_BLOCK_DAMPENING)
            arrow.remove()
            if (event.entity.shooter is Player) {
                event.entity.sendMessage(ComponentUtils.error(result.message))
            }
            return
        }

        // Was this a player?
        if (event.entity.shooter !is Player) return
        val player = event.entity.shooter as Player

        val boss = SMPRPG.getService(EntityService::class.java)
            .spawnCustomEntity(CustomEntityType.INFERNAL_PHOENIX, arrow.location)
        if (boss == null || !boss.getEntity().isValid) {
            player.sendMessage(ComponentUtils.error("Something went wrong and the boss was not spawned."))
            return
        }

        arrow.remove()

        // Summon the boss!
        player.world.playSound(player.location, Sound.ENTITY_BLAZE_DEATH, 1f, .3f)
        val name = SMPRPG.getService(ChatService::class.java).getPlayerDisplay(player)

        Bukkit.broadcast(
            ComponentUtils.merge(
                name,
                ComponentUtils.create(" summoned an "),
                boss.nameComponent,
                ComponentUtils.SYMBOL_EXCLAMATION
            )
        )

        for (i in 0..4) Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                boss.getEntity().world
                    .createExplosion(boss.entity.location.add(0.0, 5.0, 0.0), 5.0f, false, false)
            },
            (i * 3).toLong()
        )
    }

    override fun getDisplayKey(): Key? { return IModelOverridden.ofItemTypeInDirectory(customItemType, "consumables") }

    companion object {
        private fun isValidSpawnBlock(material: Material): Boolean {
            return when (material) {
                Material.LAVA -> true
                Material.WEEPING_VINES -> true
                Material.AIR -> true
                Material.TWISTING_VINES -> true
                else -> false
            }
        }

        // How much should the arrow velocity be dampened by when colliding with a block and reflecting?
        private const val ARROW_VELOCITY_BLOCK_DAMPENING = .2
        private const val ARROW_VELOCITY_ENTITY_DAMPENING = .5

        private const val ALLOW_SPAWNING = true
    }
}
