package xyz.devvydont.smprpg.entity.slayer.shambling

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationChaseGoal
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationEnrageGoal
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class ShamblingAbominationIntermediate(entity: LivingEntity?, entityType: CustomEntityType?) : ShamblingAbominationParent(entity as Zombie?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.NECROTIC_FLESH), 4, 12, this),
            QuantityLootDrop(generate(Material.ROTTEN_FLESH), 16, 32, this),
            ChancedItemDrop(generate(CustomItemType.PREMIUM_FLESH), 10, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_FLESH), 70, this),

            ChancedItemDrop(generate(CustomItemType.REVILED_VISCERA), 50, this),
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 0.35)
        updateBaseAttribute(AttributeWrapper.SCALE, 1.1)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 75.0)
    }

    override fun setup() {
        super.setup()
        val zombie = entity as Zombie
        zombie.setAdult()
        if (zombie.vehicle != null)
            zombie.vehicle!!.removePassenger(zombie)
        val mobGoals = Bukkit.getMobGoals()
        val reinforcements = zombie.getAttribute(Attribute.SPAWN_REINFORCEMENTS)
        if (reinforcements != null)
            reinforcements.baseValue = 0.0
        mobGoals.removeAllGoals(zombie)
        Bukkit.getScheduler().runTaskLater(SMPRPG.plugin, Runnable {
            mobGoals.addGoal(zombie, 3, ShamblingAbominationChaseGoal(this, spawner, 1.6))
            mobGoals.addGoal(zombie, 4, ShamblingAbominationEnrageGoal(this, spawner))
        }, 1L)
    }
}