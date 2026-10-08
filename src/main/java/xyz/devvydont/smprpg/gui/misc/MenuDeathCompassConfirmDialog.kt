package xyz.devvydont.smprpg.gui.misc

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.body.DialogBody
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuDialog
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.misc.DeathCertificate
import xyz.devvydont.smprpg.items.blueprints.misc.DeathCompass
import xyz.devvydont.smprpg.services.EconomyService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.EnvironmentDisplay

/**
 * Confirmation dialog shown before converting a Death Certificate into a Death Compass.
 * Prevents accidental spending by requiring the player to confirm the coin fee.
 */
class MenuDeathCompassConfirmDialog(
    player: Player,
    private val certificateItem: ItemStack,
    private val deathLocation: Location,
    private val deceased: String,
    private val timestamp: Long
) : MenuDialog(player, null) {

    override fun dialogTitle(): Component {
        return Component.text("Forge Death Compass?")
    }

    override fun dialogBody(): List<DialogBody> {

        val world = deathLocation.world
        val coordinates = "${deathLocation.blockX} ${deathLocation.blockY} ${deathLocation.blockZ}"

        return listOf(
            DialogBody.item(buildPreview())
                .showTooltip(true)
                .showDecorations(true)
                .build(),
            DialogBody.plainMessage(
                ComponentUtils.merge(
                    ComponentUtils.create(deceased, NamedTextColor.AQUA),
                    ComponentUtils.create(" died in "),
                    EnvironmentDisplay.name(world.environment)
                )
            ),
            DialogBody.plainMessage(
                ComponentUtils.merge(
                    ComponentUtils.create("Coordinates: "),
                    ComponentUtils.create(coordinates, NamedTextColor.BLUE)
                )
            ),
            DialogBody.plainMessage(
                ComponentUtils.merge(
                    ComponentUtils.create("Cost: "),
                    ComponentUtils.money(DeathCompass.CONVERSION_COST.toInt())
                )
            ),
            DialogBody.plainMessage(
                ComponentUtils.create("This consumes your Death Certificate.", NamedTextColor.DARK_GRAY)
            )
        )
    }

    override fun confirmButtonLabel(): Component {
        return ComponentUtils.merge(
            ComponentUtils.create("Forge ("),
            ComponentUtils.money(DeathCompass.CONVERSION_COST.toInt()),
            ComponentUtils.create(")")
        )
    }

    override fun onConfirm(response: DialogResponseView) {

        // Re-validate funds at confirm time; the player's balance may have changed since the prompt opened.
        val economy = SMPRPG.getService(EconomyService::class.java)
        if (economy.getMoney(player) < DeathCompass.CONVERSION_COST) {
            player.sendMessage(ComponentUtils.error("You can't afford to forge a Death Compass."))
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.5f)
            closeMenu()
            return
        }

        // The certificate may have moved or been consumed while the dialog was open.
        val blueprint = ItemService.blueprint(certificateItem)
        if (blueprint !is DeathCertificate) {
            player.sendMessage(ComponentUtils.error("Your Death Certificate is no longer available."))
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.5f, 1.5f)
            closeMenu()
            return
        }

        economy.spendMoney(player, DeathCompass.CONVERSION_COST)
        certificateItem.subtract()

        val compass = buildPreview()
        player.give(compass)

        player.sendMessage(ComponentUtils.success("You forged a Death Compass from your Death Certificate."))
        player.playSound(player, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.2f)
        closeMenu()
    }

    /**
     * Builds a Death Compass bound to this death. Used both as the dialog preview and as the item the player receives.
     */
    private fun buildPreview(): ItemStack {
        val compass = ItemService.generate(CustomItemType.DEATH_COMPASS)
        val blueprint = ItemService.blueprint(CustomItemType.DEATH_COMPASS) as DeathCompass
        blueprint.bind(compass, deathLocation, deceased, timestamp)
        return compass
    }
}
