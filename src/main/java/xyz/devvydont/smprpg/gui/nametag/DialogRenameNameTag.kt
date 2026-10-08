package xyz.devvydont.smprpg.gui.nametag

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.input.DialogInput
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.gui.base.MenuDialog
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.persistence.KeyStore

/**
 * Lets a player type the name they want to give a mob with a name tag. Replaces the vanilla anvil flow now
 * that the vanilla anvil GUI is gone. The typed name is stored on the name tag's PDC and applied to a mob
 * later when the player right-clicks it (see NameTagListener). Submitting a blank name clears the tag.
 */
class DialogRenameNameTag(
    player: Player,
    private val nameTag: ItemStack
) : MenuDialog(player) {

    override fun dialogTitle(): Component =
        ComponentUtils.create("Name Tag")

    override fun dialogBody(): List<DialogBody> = listOf(
        DialogBody.plainMessage(
            ComponentUtils.create("Enter a name, then right-click a mob to give it that name. Leave it blank to clear the tag.")
        )
    )

    override fun dialogInputs(): List<DialogInput> = listOf(
        DialogInput.text(INPUT_KEY_NAME, ComponentUtils.create("Name"))
            .initial(currentName())
            .maxLength(MAX_NAME_LENGTH)
            .build()
    )

    override fun onConfirm(response: DialogResponseView) {
        val typed = response.getText(INPUT_KEY_NAME)?.let { sanitize(it) } ?: ""

        if (typed.isBlank()) {
            nameTag.editPersistentDataContainer { it.remove(KeyStore.ASSIGNED_NAME) }
            nameTag.editMeta { it.displayName(null) }
            playSound(Sound.UI_BUTTON_CLICK)
            sendMessageToPlayer(ComponentUtils.success("Cleared this name tag."))
            return
        }

        nameTag.editPersistentDataContainer { it.set(KeyStore.ASSIGNED_NAME, PersistentDataType.STRING, typed) }
        nameTag.editMeta { it.displayName(ComponentUtils.create(typed, NamedTextColor.YELLOW)) }
        playSound(Sound.UI_BUTTON_CLICK)
        sendMessageToPlayer(ComponentUtils.success("Name tag set! Right-click a mob to name it \"$typed\"."))
    }

    private fun currentName(): String =
        nameTag.persistentDataContainer.get(KeyStore.ASSIGNED_NAME, PersistentDataType.STRING) ?: ""

    companion object {
        private const val INPUT_KEY_NAME = "name"
        private const val MAX_NAME_LENGTH = 35

        /**
         * Strips any legacy formatting/section characters so a typed name can't smuggle in colors or special
         * codes, and trims surrounding whitespace.
         */
        private fun sanitize(raw: String): String =
            raw.replace(LEGACY_FORMAT_CHARS, "").trim()

        private val LEGACY_FORMAT_CHARS = Regex("[§&]")
    }
}
