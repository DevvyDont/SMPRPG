package xyz.devvydont.smprpg.block.entity

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.bukkit.entity.data.DisplayData
import net.momirealms.craftengine.bukkit.item.BukkitItemManager
import net.momirealms.craftengine.bukkit.util.ItemStackUtils
import net.momirealms.craftengine.core.block.entity.BlockEntity
import net.momirealms.craftengine.core.block.entity.BlockEntityController
import net.momirealms.craftengine.core.block.entity.render.element.BlockEntityElement
import net.momirealms.craftengine.core.plugin.config.Config
import net.momirealms.craftengine.core.util.Direction
import net.momirealms.craftengine.core.util.VersionHelper
import net.momirealms.craftengine.core.world.Vec3d
import net.momirealms.craftengine.libraries.nbt.CompoundTag
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.joml.Quaternionf
import org.joml.Vector3f
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.block.behaviors.CuttingBoardBehavior
import xyz.devvydont.smprpg.block.entity.renderers.CuttingBoardDisplayElement
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService
import java.util.function.Consumer


class CuttingBoardBlockEntityController(val blockEntity: BlockEntity, val behavior: CuttingBoardBehavior) : BlockEntityController(blockEntity), Listener {

    var item: ItemStack
    var cacheMetadata: List<Object> = listOf()
    val version = VersionHelper.WORLD_VERSION
    var element: CuttingBoardDisplayElement

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)

        item = ItemStack.empty()
        element = CuttingBoardDisplayElement(this, blockEntity.pos)

        updateMetadata()
    }

    override fun saveCustomData(tag: CompoundTag) {
        tag.putInt("data_version", version)
        try {
            if (item.isEmpty) return
            val itemTag = ItemStackUtils.saveBukkitItemAsTag(item)
            if (itemTag == null) return
            tag.put("item", itemTag)
        }
        catch (e: IllegalStateException) {
            plugin.logger.warning("Cutting Board item failed to save: ${e.message}")}  // TODO: On shutdown, we don't have access to saveItemStackAsTag (reason: reflection) so we have a wipe potential here.
    }

    override fun loadCustomData(tag: CompoundTag) {
        val dataVersion = tag.getInt("data_version", Config.itemDataFixerUpperFallbackVersion())
        val itemTag = tag.get("item")
        if (itemTag == null) return
        val itemStack = ItemStackUtils.parseBukkitItem(itemTag, dataVersion)
        if (itemStack == null) return
        item = itemStack
        updateMetadata()

        setItemStack(item)
    }

    fun setItemStack(itemStack: ItemStack?) {
        if (itemStack == null) item = ItemStack.empty()
        else item = itemStack

        val pos = blockEntity.pos
        val world = blockEntity.world
        val chunk = world.getChunkAtIfLoaded(pos.x shr 4, pos.z shr 4)
        if (chunk == null) return
        updateMetadata()
        for (player in chunk.trackedBy) {
            element.update(player)
        }
        world.blockEntityChanged(pos)
    }

    fun updateMetadata() {
        val facing = blockEntity.blockState.customBlockState().getProperty<Direction>("facing").toString()
        val metadataValues: List<Object> = ArrayList()
        DisplayData.ItemDisplayData.ItemStack.addEntityData(BukkitAdaptor.adapt(item).minecraftItem(), metadataValues)
        DisplayData.ItemDisplayData.ItemTransform.addEntityData(7, metadataValues)  // GROUND
        when (facing) {
            "NORTH" -> {
                DisplayData.ItemDisplayData.Translation.addEntityData(Vector3f(0.5f, 0.08f, 0.5f), metadataValues)
                DisplayData.ItemDisplayData.LeftRotation.addEntityData(Quaternionf(0.7f, 0.0f, 0.0f, 0.7f), metadataValues)
            }
            "EAST" -> {
                DisplayData.ItemDisplayData.Translation.addEntityData(Vector3f(0.5f, 0.08f, 0.5f), metadataValues)
                DisplayData.ItemDisplayData.LeftRotation.addEntityData(Quaternionf(0.7f, 0.0f, 0.0f, 0.7f), metadataValues)
                DisplayData.ItemDisplayData.RightRotation.addEntityData(Quaternionf(0.0f, 0.0f, 0.7f, 0.7f), metadataValues)
            }
            "SOUTH" -> {
                DisplayData.ItemDisplayData.Translation.addEntityData(Vector3f(0.5f, 0.08f, 0.5f), metadataValues)
                DisplayData.ItemDisplayData.LeftRotation.addEntityData(Quaternionf(-0.7f, 0.0f, 0.0f, 0.7f), metadataValues)
            }
            else -> {
                DisplayData.ItemDisplayData.Translation.addEntityData(Vector3f(0.5f, 0.08f, 0.5f), metadataValues)
                DisplayData.ItemDisplayData.LeftRotation.addEntityData(Quaternionf(-0.7f, 0.0f, 0.0f, 0.7f), metadataValues)
                DisplayData.ItemDisplayData.RightRotation.addEntityData(Quaternionf(-0.0f, 0.0f, -0.7f, 0.7f), metadataValues)
            }
        }
        cacheMetadata = metadataValues
    }

    override fun onRemove() {
        val pos = Vec3d.atCenterOf(blockEntity.pos)
        blockEntity.world.world.dropItemNaturally(pos, BukkitItemManager.instance().wrap(item))
        blockEntity.world.world.dropItemNaturally(pos, BukkitItemManager.instance().wrap(ItemService.generate(CustomItemType.CUTTING_BOARD)))
        item = ItemStack.empty()

        HandlerList.unregisterAll(this)
    }

    override fun hasElement(): Boolean {
        return true
    }

    override fun gatherElements(consumer: Consumer<BlockEntityElement>) {
        consumer.accept(element)
    }
}