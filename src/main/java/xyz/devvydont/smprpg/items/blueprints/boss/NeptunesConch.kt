package xyz.devvydont.smprpg.items.blueprints.boss

import io.papermc.paper.datacomponent.item.Consumable
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.ElderGuardian
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.generator.structure.Structure
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint
import xyz.devvydont.smprpg.items.interfaces.IConsumable
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.items.interfaces.ISellable
import xyz.devvydont.smprpg.services.ChatService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.formatting.ComponentUtils

class NeptunesConch(itemService: ItemService, type: CustomItemType) : CustomItemBlueprint(itemService, type),
    IHeaderDescribable, Listener, IConsumable, ISellable {
    override fun getHeader(meta: ItemStack?): MutableList<Component?> {
        return mutableListOf(
            ComponentUtils.create("Consume while in an"),
            ComponentUtils.create("Ocean Monument", NamedTextColor.AQUA).append(
                ComponentUtils.create(" while ").append(ComponentUtils.create("underwater", NamedTextColor.BLUE))
            ),
            ComponentUtils.create("to summon an ").append(
                ComponentUtils.create("Elder Guardian", NamedTextColor.DARK_PURPLE).append(ComponentUtils.create("!"))
            )
        )
    }

    override val itemClassification: ItemClassification get() = ItemClassification.CONSUMABLE

    override fun getConsumableComponent(item: ItemStack?): Consumable {
        return Consumable.consumable()
            .consumeSeconds(5f)
            .sound(SoundEventKeys.ITEM_GOAT_HORN_SOUND_6)
            .animation(ItemUseAnimation.BOW)
            .hasConsumeParticles(false)
            .build()
    }

    private fun isInMonument(player: Player): Boolean {
        for (structure in player.chunk.getStructures(Structure.MONUMENT)) if (structure.boundingBox
                .overlaps(player.boundingBox)
        ) return true
        return false
    }

    @EventHandler
    fun onConsumeConch(event: PlayerItemConsumeEvent) {
        val consumedItem = event.item
        if (!isItemOfType(consumedItem)) return

        val refused: Component = ComponentUtils.error("Neptune refused your call!")

        // A player consumed the conch. Are they underwater?
        if (!event.getPlayer().isUnderWater) {
            event.getPlayer().sendMessage(refused)
            event.getPlayer().sendMessage(
                ComponentUtils.alert(
                    ComponentUtils.create("You must be")
                        .append(ComponentUtils.create(" underwater", NamedTextColor.BLUE))
                        .append(ComponentUtils.create("!"))
                )
            )
            event.isCancelled = true
            return
        }

        // Are they in a temple?
        if (!isInMonument(event.getPlayer())) {
            event.getPlayer().sendMessage(refused)
            event.getPlayer().sendMessage(
                ComponentUtils.alert(
                    ComponentUtils.create("You must be inside an")
                        .append(ComponentUtils.create(" Ocean Monument", NamedTextColor.AQUA))
                        .append(ComponentUtils.create("!"))
                )
            )
            event.isCancelled = true
            return
        }

        // Summon the boss!
        event.getPlayer().world
            .playSound(event.getPlayer().location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1f)
        Bukkit.broadcast(
            ComponentUtils.alert(
                SMPRPG.getService(ChatService::class.java).getPlayerDisplay(event.getPlayer())
                    .append(ComponentUtils.create(" summoned an "))
            )
                .append(ComponentUtils.create("Elder Guardian", NamedTextColor.DARK_PURPLE))
                .append(ComponentUtils.create("!"))
        )
        val guardian = event.getPlayer().world.spawn<ElderGuardian>(
            event.getPlayer().location,
            ElderGuardian::class.java,
            CreatureSpawnEvent.SpawnReason.NATURAL
        )
        guardian.world.createExplosion(guardian, 2.0f, false, false)
    }

    override fun getWorth(item: ItemStack): Int {
        return 2500 * item.amount
    }
}
