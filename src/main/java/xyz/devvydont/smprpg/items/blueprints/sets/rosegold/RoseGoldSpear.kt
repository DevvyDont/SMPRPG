package xyz.devvydont.smprpg.items.blueprints.sets.rosegold

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.KineticWeapon
import io.papermc.paper.registry.keys.SoundEventKeys
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.blueprints.vanilla.ItemSpear
import xyz.devvydont.smprpg.items.interfaces.IBreakableEquipment
import xyz.devvydont.smprpg.items.interfaces.IModelOverridden
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.skills.SkillType

class RoseGoldSpear(itemService: ItemService, type: CustomItemType) : RoseGoldAttributeItem(itemService, type), IBreakableEquipment, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.SPEAR
    override val skillRequirements: MutableMap<SkillType, Int> get() = mutableMapOf(Pair(SkillType.COMBAT, toolStats.skillReqLevel))

    override fun getAttributeModifiers(item: ItemStack?): MutableCollection<AttributeEntry?>? {
        return mutableListOf(
            AdditiveAttributeEntry(AttributeWrapper.STRENGTH, ItemSpear.getSpearDamage(customItemType)),
            MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, ItemSpear.getSpearRecovery(customItemType))
        )
    }

    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)
        itemStack.setData(DataComponentTypes.KINETIC_WEAPON, KINETIC_COMP)
    }

    companion object {
        val KINETIC_COMP : KineticWeapon = KineticWeapon.kineticWeapon()
            .dismountConditions(KineticWeapon.condition(70, 10.0f, 0.0f))
            .forwardMovement(0.38f)
            .hitSound(SoundEventKeys.ITEM_SPEAR_HIT)
            .sound(SoundEventKeys.ITEM_SPEAR_USE)
            .knockbackConditions(KineticWeapon.condition(170, 5.1f, 0.0f))
            .delayTicks(14)
            .damageMultiplier(0.9f)
            .damageConditions(KineticWeapon.condition(275, 0.0f, 4.6f))
            .build()
    }
}
