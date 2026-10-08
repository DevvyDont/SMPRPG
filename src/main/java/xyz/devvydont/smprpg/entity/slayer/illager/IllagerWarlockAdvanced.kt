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
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class IllagerWarlockAdvanced(entity: LivingEntity?, entityType: CustomEntityType?) : IllagerWarlockParent(entity as Evoker?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(generate(CustomItemType.PREMIUM_SPELL_POWDER), 2, 5, this),
            QuantityLootDrop(generate(Material.EMERALD), 24, 48, this),
            QuantityLootDrop(generate(Material.EMERALD_BLOCK), 1, 2, this),
            ChancedItemDrop(generate(CustomItemType.ENCHANTED_EMERALD), 50, this),

            ChancedItemDrop(generate(CustomItemType.EVOKATION_CODEX), 25, this),
            ChancedItemDrop(LOOT_VIGILANTE_SCROLL, 65, this),
            ChancedItemDrop(LOOT_APTITUDE_SCROLL, 100, this),
            ChancedItemDrop(generate(CustomItemType.CRYSTAL_BALL), 200, this),
            ChancedItemDrop(generate(CustomItemType.HORN_OF_WARLOCK), 50, this),
            ChancedItemDrop(generate(CustomItemType.FANG_STRIKE_SPELL), 100, this),
            ChancedItemDrop(generate(CustomItemType.DAMAGE_AURA_SPELL), 100, this),
            ChancedItemDrop(generate(CustomItemType.RECOMBOBULATOR), 750, this),
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 0.9)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 150.0)
    }

    override fun setup() {
        super.setup()
        val evoker = entity as Evoker
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(evoker)
        mobGoals.addGoal(evoker, 3, IllagerWarlockAuraCloudGoal(this, null, 5.0))
        mobGoals.addGoal(evoker, 3, IllagerWarlockFangChaseGoal(this, null, 20))
        mobGoals.addGoal(evoker, 3, IllagerWarlockSpellGoal(this, null, 26, mapOf(
            Pair(SpellType.TELEPORT, 0.25),
            Pair(SpellType.FIREBALL, 0.25),
            Pair(SpellType.TOSS, 0.2),
            Pair(SpellType.FANGS, 0.3),
        )))
    }
}