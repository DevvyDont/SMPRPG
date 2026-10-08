package xyz.devvydont.smprpg.gui.base

import io.papermc.paper.dialog.Dialog
import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.ActionButton
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.action.DialogAction
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import io.papermc.paper.registry.data.dialog.type.DialogType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickCallback
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent
import xyz.devvydont.smprpg.SMPRPG

/**
 * A menu that displays a Paper Dialog instead of a chest inventory.
 *
 * Extends [MenuBase] with a 1-row dummy inventory that is never visible to the player.
 * When opened (including via [openSubMenu][MenuBase.openSubMenu]), the dummy inventory
 * is immediately closed and a Paper Dialog is shown instead.
 *
 * Subclasses override the dialog builder methods and the confirm/cancel callbacks.
 */
abstract class MenuDialog(
    player: Player,
    parentMenu: MenuBase? = null
) : MenuBase(player, DUMMY_ROWS, parentMenu) {

    // -------------------------
    //   Subclass API: Dialog
    // -------------------------

    /** The title displayed at the top of the dialog window. */
    protected abstract fun dialogTitle(): Component

    /** Body elements displayed between the title and inputs. */
    protected open fun dialogBody(): List<DialogBody> = emptyList()

    /** Input fields (text, dropdowns, etc.) shown in the dialog form. */
    protected open fun dialogInputs(): List<DialogInput> = emptyList()

    /** Label for the confirm/submit button. */
    protected open fun confirmButtonLabel(): Component = Component.text("Confirm")

    /** Label for the cancel/dismiss button. */
    protected open fun cancelButtonLabel(): Component = Component.text("Cancel")

    /** Whether the player can close the dialog by pressing ESC. */
    protected open fun canCloseWithEscape(): Boolean = true

    /** Called when the player clicks the confirm button. */
    protected abstract fun onConfirm(response: DialogResponseView)

    /** Called when the player clicks the cancel button. Defaults to navigating back. */
    protected open fun onCancel() {
        navigateBack()
    }

    // -------------------
    //   Built-in Helpers
    // -------------------

    /**
     * Opens any [MenuBase] on the next server tick.
     * Required because dialog callbacks fire off the main thread tick cycle.
     */
    protected fun openMenuNextTick(menu: MenuBase) {
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            menu.openMenu()
        }, NEXT_TICK_DELAY)
    }

    /**
     * Returns to the [parentMenu] with a transition sound, or does nothing if null.
     */
    protected fun navigateBack() {
        val parent = parentMenu ?: return
        sounds.playMenuOpenParent()
        openMenuNextTick(parent)
    }

    // -----------------
    //   Lifecycle
    // -----------------

    override fun handleInventoryOpened(event: InventoryOpenEvent) {
        shouldPlayClosingSound = false
        // Must schedule on the next tick — closeInventory() is ignored
        // when called from inside InventoryOpenEvent.
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            closeMenu()
            player.showDialog(buildDialog())
        }, NEXT_TICK_DELAY)
    }

    // -----------------------
    //   Dialog Construction
    // -----------------------

    private fun buildDialog(): Dialog {
        val confirmAction = DialogAction.customClick(
            { response, audience ->
                if (audience is Player) {
                    Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
                        onConfirm(response)
                    }, NEXT_TICK_DELAY)
                }
            },
            ClickCallback.Options.builder().uses(SINGLE_USE).build()
        )

        val cancelAction = DialogAction.customClick(
            { _, audience ->
                if (audience is Player) {
                    Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
                        onCancel()
                    }, NEXT_TICK_DELAY)
                }
            },
            ClickCallback.Options.builder().uses(SINGLE_USE).build()
        )

        return Dialog.create { factory ->
            factory.empty().apply {
                base(
                    DialogBase.builder(dialogTitle())
                        .canCloseWithEscape(canCloseWithEscape())
                        .afterAction(DialogBase.DialogAfterAction.CLOSE)
                        .body(dialogBody())
                        .inputs(dialogInputs())
                        .build()
                )
                type(
                    DialogType.confirmation(
                        ActionButton.builder(confirmButtonLabel())
                            .action(confirmAction)
                            .build(),
                        ActionButton.builder(cancelButtonLabel())
                            .action(cancelAction)
                            .build()
                    )
                )
            }
        }
    }

    companion object {
        private const val DUMMY_ROWS = 1
        private const val NEXT_TICK_DELAY = 1L
        private const val SINGLE_USE = 1
    }
}
