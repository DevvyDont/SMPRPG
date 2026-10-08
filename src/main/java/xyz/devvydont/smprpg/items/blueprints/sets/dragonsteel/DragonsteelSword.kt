package xyz.devvydont.smprpg.items.blueprints.sets.dragonsteel

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSword
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.AbilityUtil

class DragonsteelSword(itemService: ItemService, type: CustomItemType) : DragonsteelAttributeItem(itemService, type),
    IHeaderDescribable, IBreakableEquipment, Listener {

    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, toolStats.skillReqLevel))

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        val components: MutableList<Component?> = ArrayList()
        components.add(AbilityUtil.getAbilityComponent("Draconic Summoner", passive = true))
        components.add(
            ComponentUtils.create("Summoning Crystal drops are ").append(
                ComponentUtils.create(
                    SUMMONING_CRYSTAL_BOOST.toString() + "x", NamedTextColor.GREEN
                )
            ).append(ComponentUtils.create(" more common"))
        )
        components.add(
            ComponentUtils.create("when killing ").append(ComponentUtils.create("Ender", NamedTextColor.DARK_PURPLE))
                .append(ComponentUtils.create(" type mobs."))
        )
        return components
    }

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?> {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 120.0),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemSword.SWORD_ATTACK_SPEED_DEBUFF),
            AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 10.0)
        )
    }

    @EventHandler
    fun onRollWitherSkull(event: CustomItemDropRollEvent) {
        if (event.tool == null || event.tool.type == Material.AIR) return

        if (!isItemOfType(event.tool)) return

        val drop = itemService.getBlueprint(event.getDrop())
        if (drop == itemService.getBlueprint(CustomItemType.SUMMONING_CRYSTAL)) event.chance =
            event.chance * SUMMONING_CRYSTAL_BOOST
    }

    companion object {
        const val SUMMONING_CRYSTAL_BOOST: Int = 2
    }
}
