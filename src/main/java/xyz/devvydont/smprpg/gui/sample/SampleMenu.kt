package xyz.devvydont.smprpg.gui.sample

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import java.util.*
import kotlin.math.ceil
import kotlin.math.min

class SampleMenu(player: Player) : MenuBase(player, 3) {
    private var pageIndex = 0
    private val pages: Array<Array<Material?>?> = generatePages()

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        event.titleOverride(ComponentUtils.create("Sample Menu", NamedTextColor.BLACK))
        render()
    }

    private fun render() {
        this.clear()
        this.setBorderFull()

        // Render page counter
        this.setSlot(4, createNamedItem(Material.CHERRY_SIGN, String.format("Page %s/%s", pageIndex + 1, pages.size)))

        // Render the item buttons
        val currentPage = this.pages[this.pageIndex]!!
        for (i in currentPage.indices) {
            val material: Material = currentPage[i]!!
            this.setButton(10 + i, ItemStack(material)) { e: InventoryClickEvent ->
                this.openSubMenu(SampleSubMenu(this.player, this, material))
            }
        }

        // Render the navigation buttons
        this.setButton(21, BUTTON_PAGE_PREVIOUS) { e: InventoryClickEvent ->
            this.pageIndex--
            if (this.pageIndex < 0) {
                this.pageIndex = this.pages.size - 1
            }
            this.sounds.playPagePrevious()
            this.render()
        }
        this.setButton(22, BUTTON_EXIT) { e: InventoryClickEvent ->
            this.closeMenu()
        }
        this.setButton(23, BUTTON_PAGE_NEXT) { e: InventoryClickEvent ->
            this.pageIndex++
            if (this.pageIndex >= this.pages.size) {
                this.pageIndex = 0
            }
            this.sounds.playPageNext()
            this.render()
        }
    }

    override fun handleInventoryClicked(event: InventoryClickEvent) {
        val clickedItem = this.getItem(event.slot)
        if (clickedItem == null || BORDER_NORMAL == clickedItem) {
            this.playInvalidAnimation()
        }
    }

    companion object {
        private fun generatePages(): Array<Array<Material?>?> {
            val chunkSize = 7
            val valuesToChunk = Arrays.stream(Material.entries.toTypedArray()).toArray()
            val numOfChunks = ceil(valuesToChunk.size.toDouble() / chunkSize).toInt()
            val output = arrayOfNulls<Array<Material?>>(numOfChunks)
            for (i in 0..<numOfChunks) {
                val start = i * chunkSize
                val length = min(valuesToChunk.size - start, chunkSize)
                val temp = arrayOfNulls<Material>(length)
                System.arraycopy(valuesToChunk, start, temp, 0, length)
                output[i] = temp
            }
            return output
        }
    }
}
