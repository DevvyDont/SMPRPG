package xyz.devvydont.smprpg.gui.slayer

import io.papermc.paper.dialog.DialogResponseView
import io.papermc.paper.registry.data.dialog.body.DialogBody
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.gui.base.MenuBase
import xyz.devvydont.smprpg.gui.base.MenuDialog
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.SlayerService
import xyz.devvydont.smprpg.slayer.quest.SlayerClassification
import xyz.devvydont.smprpg.slayer.quest.SlayerQuest
import xyz.devvydont.smprpg.slayer.quest.SlayerType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.MinecraftStringUtils
import xyz.devvydont.smprpg.util.formatting.Symbols

class MenuSlayerQuestConfirmDialog(
    player: Player,
    parentMenu: MenuBase?,
    private val slayerClassification : SlayerClassification) : MenuDialog(player, parentMenu) {

    override fun dialogTitle(): Component {
        return Component.text("Start Slayer Quest?")
    }

    override fun dialogBody(): List<DialogBody> {
        val itemService = SMPRPG.getService(ItemService::class.java)
        var item : ItemStack?
        when (slayerClassification.slayerType) {
            SlayerType.SHAMBLING_ABOMINATION -> item = itemService.getCustomItem(CustomItemType.NECROTIC_FLESH)
            SlayerType.PIGLIN_WARLORD -> item = itemService.getCustomItem(Material.COOKED_PORKCHOP)
            SlayerType.ILLAGER_WARLOCK -> item = itemService.getCustomItem(CustomItemType.SPELL_POWDER)
        }

        // TODO: Potentially obfuscate stats until player has killed that boss tier?
        return listOf(
            DialogBody.item(item)
                .description(DialogBody.plainMessage(ComponentUtils.create(slayerClassification.entityType.Name, NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)))
                .showTooltip(false)
                .showDecorations(false).build(),
            DialogBody.plainMessage(ComponentUtils.merge(
                ComponentUtils.create("Cost: "),
                ComponentUtils.money(slayerClassification.cost)
            )),
            DialogBody.plainMessage(ComponentUtils.merge(
                ComponentUtils.create("Experience to spawn: "),
                ComponentUtils.create(slayerClassification.xpToSpawn, NamedTextColor.AQUA)
            )),
            DialogBody.plainMessage(ComponentUtils.create("                          ", TextDecoration.UNDERLINED)),
            DialogBody.plainMessage(ComponentUtils.create("Boss Stats", TextDecoration.BOLD)),
            DialogBody.plainMessage(ComponentUtils.merge(
                ComponentUtils.merge(
                    ComponentUtils.create("Max Health: "),
                    ComponentUtils.merge(
                        ComponentUtils.create(MinecraftStringUtils.formatNumber(slayerClassification.entityType.Hp.toLong()), NamedTextColor.RED),
                        ComponentUtils.create(" " + Symbols.HEART, NamedTextColor.RED)
                    )
                )
            )),
            DialogBody.plainMessage(ComponentUtils.merge(
                ComponentUtils.merge(
                    ComponentUtils.create("Damage: "),
                    ComponentUtils.merge(
                        ComponentUtils.create(MinecraftStringUtils.formatNumber(slayerClassification.entityType.Damage.toLong()), NamedTextColor.DARK_RED),
                        ComponentUtils.create(" " + Symbols.SWORD, NamedTextColor.DARK_RED)
                    )
                )
            ))
        )
    }

    override fun onConfirm(response: DialogResponseView) {
        val questOwner = SMPRPG.getService(EntityService::class.java).getPlayerInstance(player)
        val quest = SlayerQuest(questOwner, slayerClassification)
        quest.questState = SlayerQuest.SlayerQuestState.XP_COLLECTION
        SMPRPG.getService(SlayerService::class.java).registerQuest(quest)
        player.playSound(player, Sound.ENTITY_WITHER_DEATH, 1.0f, 1.5f)
        closeMenu()
    }
}