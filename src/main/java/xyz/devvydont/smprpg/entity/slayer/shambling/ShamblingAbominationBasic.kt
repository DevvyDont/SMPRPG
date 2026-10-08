package xyz.devvydont.smprpg.entity.slayer.shambling

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationChaseGoal
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class ShamblingAbominationBasic(entity: LivingEntity?, entityType: CustomEntityType?) : ShamblingAbominationParent(entity as Zombie?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(ItemService.Companion.generate(CustomItemType.NECROTIC_FLESH), 1, 3, this),
            QuantityLootDrop(ItemService.Companion.generate(Material.ROTTEN_FLESH), 5, 16, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.PREMIUM_FLESH), 20, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.ENCHANTED_FLESH), 100, this),

            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.REVILED_VISCERA), 100, this)
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 0.25)
        updateBaseAttribute(AttributeWrapper.SCALE, 1.0)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 25.0)
    }

    override fun setup() {
        super.setup()
        val zombie = entity as Zombie
        zombie.setAdult()
        if (zombie.vehicle != null)
            zombie.vehicle!!.removePassenger(zombie)
        val reinforcements = zombie.getAttribute(Attribute.SPAWN_REINFORCEMENTS)
        if (reinforcements != null)
            reinforcements.baseValue = 0.0
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(zombie)
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            mobGoals.addGoal(zombie, 3, ShamblingAbominationChaseGoal(this, spawner, 1.5))
        }, 1L)

    }
}