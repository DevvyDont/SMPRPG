package xyz.devvydont.smprpg.entity.slayer.illager

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Evoker
import org.bukkit.entity.LivingEntity
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockAuraCloudGoal
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockSpellGoal
import xyz.devvydont.smprpg.entity.slayer.illager.goals.IllagerWarlockSpellGoal.SpellType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class IllagerWarlockIntermediate(entity: LivingEntity?, entityType: CustomEntityType?) : IllagerWarlockParent(entity as Evoker?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.SPELL_POWDER), 4, 12, this),
            QuantityLootDrop(generate(Material.EMERALD), 16, 32, this),
            ChancedItemDrop(generate(Material.EMERALD_BLOCK), 10, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_EMERALD), 70, this),

            ChancedItemDrop(generate(CustomItemType.EVOKATION_CODEX), 50, this),
            ChancedItemDrop(generate(CustomItemType.HORN_OF_WARLOCK), 100, this),
            ChancedItemDrop(generate(CustomItemType.FANG_STRIKE_SPELL), 200, this),
            ChancedItemDrop(generate(CustomItemType.DAMAGE_AURA_SPELL), 200, this)
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 0.85)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 100.0)
    }

    override fun setup() {
        super.setup()
        val evoker = entity as Evoker
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(evoker)
        mobGoals.addGoal(evoker, 3, IllagerWarlockAuraCloudGoal(this, null, 3.0))
        mobGoals.addGoal(evoker, 3, IllagerWarlockSpellGoal(this, null, 28, mapOf(
            Pair(SpellType.TELEPORT, 0.35),
            Pair(SpellType.FIREBALL, 0.35),
            Pair(SpellType.FANGS, 0.3),
        )))
    }
}