package xyz.devvydont.smprpg.entity.slayer.shambling.goals

import com.destroystokyo.paper.entity.ai.Goal
import com.destroystokyo.paper.entity.ai.GoalKey
import com.destroystokyo.paper.entity.ai.GoalType
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.Equippable
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationIntermediate
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured
import xyz.devvydont.smprpg.services.AttributeService
import java.util.*

class ShamblingAbominationEnrageGoal(val slayer : ShamblingAbominationParent, val spawnPlayer : Player?) : Goal<Zombie> {

    val multiplierKey : NamespacedKey = NamespacedKey(SMPRPG.plugin, "shambling_abomination_enrage")
    var activated = false
    val zombie = slayer.entity as Zombie

    override fun shouldActivate(): Boolean {
        val maxHp = zombie.getAttribute(Attribute.MAX_HEALTH)
        if (activated)  // We only want enrage to happen once.
            return false

        if (maxHp != null) {
           if (zombie.health <= (maxHp.value * slayer.enrageThreshold)) {
                return true
           }
        }
        return false
    }

    override fun getKey(): GoalKey<Zombie> {
        return GOAL_KEY
    }

    override fun getTypes(): EnumSet<GoalType> {
        return EnumSet.of(GoalType.UNKNOWN_BEHAVIOR)
    }

    override fun shouldStayActive(): Boolean {
        return shouldActivate();
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun tick() {
        if (!activated) {
            activated = true

            // Multiply outgoing damage by 50%
            if (!(slayer is ShamblingAbominationIntermediate)) {  // We only want T3+ Shambling Abominations to get the strength increase, T2 is fine with just attack rate.
                val strInst = AttributeService.instance.getOrCreateAttribute(zombie, AttributeWrapper.STRENGTH)
                strInst.addModifier(
                    AttributeModifier(
                        multiplierKey,
                        0.5,
                        AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    )
                )
            }

            // Speed em up by 25%
            val speedInst = AttributeService.instance.getOrCreateAttribute(zombie, AttributeWrapper.MOVEMENT_SPEED)
            speedInst.addModifier(AttributeModifier(multiplierKey, 0.25, AttributeModifier.Operation.MULTIPLY_SCALAR_1))

            // Attacks twice as fast
            slayer.attackCooldown /= 2

            // Update asset ID on all the equipment
            val zombieEq = zombie.equipment
            val head = ItemStack(Material.PLAYER_HEAD)
            val meta = head.itemMeta as SkullMeta
            meta.playerProfile = ICustomTextured.getProfile(ShamblingAbominationParent.HEAD_TEXTURE_ANGRY)
            head.setItemMeta(meta)
            zombieEq.setHelmet(head)

            val chestplate = zombieEq.chestplate
            chestplate.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.CHEST).assetId(ShamblingAbominationParent.enragedAssetId).build())
            zombieEq.setChestplate(chestplate)

            val leggings = zombieEq.leggings
            leggings.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.LEGS).assetId(ShamblingAbominationParent.enragedAssetId).build())
            zombieEq.setLeggings(leggings)

            val boots = zombieEq.boots
            boots.setData(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.FEET).assetId(ShamblingAbominationParent.enragedAssetId).build())
            zombieEq.setBoots(boots)

            zombie.world.playSound(zombie, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1f, 0.5f)
        }
    }

    companion object {
        val GOAL_KEY : GoalKey<Zombie> = GoalKey.of(Zombie::class.java, NamespacedKey(SMPRPG.Companion.plugin, "shambling_abomination_enrage_goal"))
    }

}