package xyz.devvydont.smprpg.entity.slayer.illager

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Evoker
import org.bukkit.entity.LivingEntity
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockAuraCloudGoal
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockFangChaseGoal
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockSpellGoal
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockSpellGoal.SpellType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class IllagerWarlockBrutal(entity: LivingEntity?, entityType: CustomEntityType?) : IllagerWarlockParent(entity as Evoker?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.ENCHANTED_SPELL_POWDER), 1, 2, this),
            ChancedItemDrop(generate(CustomItemType.SPELL_POWDER_SINGULARITY), 50, this),
            QuantityLootDrop(generate(Material.EMERALD_BLOCK), 12, 20, this),
            QuantityLootDrop(generate(CustomItemType.ENCHANTED_EMERALD), 1, 3, this),

            ChancedItemDrop(LOOT_VIGILANTE_SCROLL, 20, this),
            ChancedItemDrop(LOOT_APTITUDE_SCROLL, 40, this),
            ChancedItemDrop(LOOT_INSIGHT_SCROLL, 65, this),
            ChancedItemDrop(LOOT_BURDEN_SCROLL, 100, this),
            ChancedItemDrop(generate(CustomItemType.CRYSTAL_BALL), 50, this),
            ChancedItemDrop(generate(CustomItemType.HORN_OF_WARLOCK), 15, this),
            ChancedItemDrop(generate(CustomItemType.FANG_STRIKE_SPELL), 25, this),
            ChancedItemDrop(generate(CustomItemType.DAMAGE_AURA_SPELL), 50, this),
            ChancedItemDrop(generate(CustomItemType.HEALING_AURA_SPELL), 75, this),
            ChancedItemDrop(generate(CustomItemType.RECOMBOBULATOR), 187, this)

        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 1.0)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 250.0)
    }

    override fun setup() {
        super.setup()
        val evoker = entity as Evoker
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(evoker)
        mobGoals.addGoal(evoker, 3, IllagerWarlockAuraCloudGoal(this, null, 9.0))
        mobGoals.addGoal(evoker, 3, IllagerWarlockFangChaseGoal(this, null, 10))
        mobGoals.addGoal(evoker, 3, IllagerWarlockSpellGoal(this, null, 20, mapOf(
            Pair(SpellType.TELEPORT, 0.2),
            Pair(SpellType.FIREBALL, 0.1),
            Pair(SpellType.VEX, 0.1),
            Pair(SpellType.TOSS, 0.1),
            Pair(SpellType.FANGS, 0.5)
        )))
    }
}