package xyz.devvydont.smprpg.entity.slayer.shambling

import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationChaseGoal
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationEnrageGoal
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationImplodeGoal
import xyz.devvydont.smprpg.entity.slayer.shambling.goals.ShamblingAbominationSyphonGoal
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop
import xyz.devvydont.smprpg.util.time.TickTime

class ShamblingAbominationBrutal(entity: LivingEntity?, entityType: CustomEntityType?) : ShamblingAbominationParent(entity as Zombie?, entityType) {
    override var enrageThreshold = 0.35
    override var explosionDamage = 1_000.0

    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.ENCHANTED_NECROTIC_FLESH), 1, 2, this),
            ChancedItemDrop(generate(CustomItemType.NECROTIC_FLESH_SINGULARITY), 50, this),
            QuantityLootDrop(generate(CustomItemType.PREMIUM_FLESH), 12, 20, this),
            QuantityLootDrop(generate(CustomItemType.ENCHANTED_FLESH), 1, 3, this),

            ChancedItemDrop(LOOT_SMITE_SCROLL, 20, this),
            ChancedItemDrop(generate(CustomItemType.REVILED_VISCERA), 5, this),
            ChancedItemDrop(generate(CustomItemType.UNDIGESTED_BRAINS), 100, this),
            ChancedItemDrop(generate(CustomItemType.SYPHON_SPELL), 100, this),
            ChancedItemDrop(generate(CustomItemType.NECRONOMICON_EXCERPTS), 200, this),
            ChancedItemDrop(generate(CustomItemType.NECRONOMICON), 500, this),
            ChancedItemDrop(generate(CustomItemType.RECOMBOBULATOR), 250, this),
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 1.0)
        updateBaseAttribute(AttributeWrapper.SCALE, 1.25)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 600.0)
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
            mobGoals.addGoal(zombie, 0, ShamblingAbominationChaseGoal(this, spawner, 2.0))
            mobGoals.addGoal(zombie, 1, ShamblingAbominationEnrageGoal(this, spawner))
            mobGoals.addGoal(zombie, 2, ShamblingAbominationImplodeGoal(this, spawner, TickTime.seconds(10).toInt()))
            mobGoals.addGoal(zombie, 3, ShamblingAbominationSyphonGoal(this, spawner, 0.1))
        }, 1L)
    }
}