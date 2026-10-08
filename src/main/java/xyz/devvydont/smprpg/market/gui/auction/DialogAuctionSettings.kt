package xyz.devvydont.smprpg.market.gui.auction

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput
import net.kyori.adventure.text.Component
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuDialog
import xyz.devvydont.smprpg.market.MarketConstants
import xyz.devvydont.smprpg.market.auction.AuctionType
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

/**
 * Dialog for editing auction listing settings (price, duration, type).
 * Opened from [MenuAuctionCreate] when the player clicks "Edit Listing Settings".
 */
class DialogAuctionSettings(
    player: Player,
    parentMenu: MenuBase?,
    private val currentType: AuctionType,
    private val currentDurationIndex: Int,
    private val currentPrice: Long,
    private val savedItem: ItemStack?
) : MenuDialog(player, parentMenu) {

    override fun dialogTitle(): Component =
        Component.text("Edit Listing Settings")

    override fun dialogBody(): List<DialogBody> {
        val description = DialogBody.plainMessage(
            Component.text("Configure your auction listing settings below.")
        )
        if (savedItem != null) {
            return listOf(
                DialogBody.item(savedItem)
                    .description(description)
                    .showDecorations(true)
                    .showTooltip(true)
                    .build()
            )
        }
        return listOf(description)
    }

    override fun dialogInputs(): List<DialogInput> {
        val durationEntries = MarketConstants.AUCTION_DURATIONS.mapIndexed { index, (_, name) ->
            SingleOptionDialogInput.OptionEntry.create(
                index.toString(),
                Component.text(name),
                index == currentDurationIndex
            )
        }

        val typeEntries = listOf(
            SingleOptionDialogInput.OptionEntry.create(
                TYPE_ID_BUY_IT_NOW,
                Component.text("Buy It Now"),
                currentType == AuctionType.BUY_IT_NOW
            ),
            SingleOptionDialogInput.OptionEntry.create(
                TYPE_ID_BID,
                Component.text("Auction"),
                currentType == AuctionType.BID
            )
        )

        return listOf(
            DialogInput.text(INPUT_KEY_PRICE, Component.text("Starting Price"))
                .initial(currentPrice.toString())
                .maxLength(MAX_PRICE_INPUT_LENGTH)
                .build(),
            DialogInput.singleOption(INPUT_KEY_DURATION, Component.text("Duration"), durationEntries)
                .build(),
            DialogInput.singleOption(INPUT_KEY_TYPE, Component.text("Auction Type"), typeEntries)
                .build()
        )
    }

    override fun onConfirm(response: DialogResponseView) {
        val priceText = response.getText(INPUT_KEY_PRICE)
        val durationId = response.getText(INPUT_KEY_DURATION)
        val typeId = response.getText(INPUT_KEY_TYPE)

        val newDurationIndex = durationId?.toIntOrNull()
            ?.coerceIn(0, MarketConstants.AUCTION_DURATIONS.lastIndex)
            ?: currentDurationIndex

        val newType = when (typeId) {
            TYPE_ID_BID -> AuctionType.BID
            else -> AuctionType.BUY_IT_NOW
        }

        val parsedPrice = priceText?.toLongOrNull()
        val priceErrorMessage = validatePrice(parsedPrice)

        if (priceErrorMessage != null) {
            playSound(ERROR_SOUND, ERROR_VOLUME, ERROR_PITCH)
            sendMessageToPlayer(ComponentUtils.error(priceErrorMessage))
            openMenuNextTick(
                MenuAuctionCreate(
                    player, parentMenu, newType, newDurationIndex,
                    currentPrice, savedItem, false
                )
            )
        } else {
            openMenuNextTick(
                MenuAuctionCreate(
                    player, parentMenu, newType, newDurationIndex,
                    parsedPrice!!, savedItem, true
                )
            )
        }
    }

    override fun onCancel() {
        openMenuNextTick(
            MenuAuctionCreate(
                player, parentMenu, currentType, currentDurationIndex,
                currentPrice, savedItem
            )
        )
    }

    companion object {
        private const val INPUT_KEY_PRICE = "price"
        private const val INPUT_KEY_DURATION = "duration"
        private const val INPUT_KEY_TYPE = "type"
        private const val TYPE_ID_BUY_IT_NOW = "buy_it_now"
        private const val TYPE_ID_BID = "bid"
        private const val MAX_PRICE_INPUT_LENGTH = 10

        private val ERROR_SOUND = Sound.ENTITY_ITEM_BREAK
        private const val ERROR_VOLUME = 0.4f
        private const val ERROR_PITCH = 1.0f

        private fun validatePrice(price: Long?): String? {
            if (price == null) {
                return "Invalid price! Enter a whole number."
            }
            if (price < MarketConstants.AUCTION_MIN_PRICE) {
                return "Price must be at least ${EconomyService.formatMoney(MarketConstants.AUCTION_MIN_PRICE)}!"
            }
            if (price > MarketConstants.AUCTION_MAX_PRICE) {
                return "Price cannot exceed ${EconomyService.formatMoney(MarketConstants.AUCTION_MAX_PRICE)}!"
            }
            return null
        }
    }
}
