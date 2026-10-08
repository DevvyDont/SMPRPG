package xyz.devvydont.smprpg.block.entity.renderers

import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.Vec3
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement
import net.momirealms.craftengine.core.entity.player.Player
import net.momirealms.craftengine.core.world.BlockPos
import xyz.devvydont.smprpg.block.entity.CuttingBoardBlockEntityController
import java.util.*

class CuttingBoardDisplayElement(val boardEntity: CuttingBoardBlockEntityController, val pos: BlockPos) : BlockEntityElement {

    var entityId: Int = -1
    var cachedSpawnPacket: Any? = null
    var cachedDespawnPacket: Any? = null

    init {
        entityId = Entity.nextEntityId()
        val spawnPacketClass = ClientboundAddEntityPacket::class.java
        val spawnConstructor = spawnPacketClass.getConstructor(
            Int::class.java,    // entityId
            UUID::class.java,   // uuid
            Double::class.java, // x
            Double::class.java, // y
            Double::class.java, // z
            Float::class.java,  // pitch
            Float::class.java,  // yaw
            net.minecraft.world.entity.EntityType::class.java,  // entityType
            Int::class.java,  // data
            net.minecraft.world.phys.Vec3::class.java,  // velocity
            Double::class.java,  // extraData
        )
        cachedSpawnPacket = spawnConstructor.newInstance(entityId,
            UUID.randomUUID(),
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            0.0f,
            0.0f,
            EntityType.ITEM_DISPLAY,
            0,
            Vec3.ZERO,
            0.0)

        val despawnPacketClass = ClientboundRemoveEntitiesPacket::class.java
        val despawnConstructor = despawnPacketClass.getConstructor(
            IntList::class.java
        )
        cachedDespawnPacket = despawnConstructor.newInstance(IntList.of(entityId))
    }

    override fun show(player: Player) {
        player.sendPacket(cachedSpawnPacket, false)
        update(player)
    }

    override fun hide(player: Player) {
        player.sendPacket(cachedDespawnPacket, false)
    }

    override fun update(player: Player) {
        val metadata = boardEntity.cacheMetadata
        val setEntityDataClass = ClientboundSetEntityDataPacket::class.java
        val setEntityDataConstructor = setEntityDataClass.getConstructor(
            Int::class.java,
            List::class.java
        )
        val packet = setEntityDataConstructor.newInstance(entityId, metadata)
        player.sendPacket(packet, false)
    }
}