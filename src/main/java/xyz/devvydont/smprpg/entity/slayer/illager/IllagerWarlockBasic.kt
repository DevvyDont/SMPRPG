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
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.util.items.ChancedItemDrop
import xyz.devvydont.smprpg.util.items.LootDrop
import xyz.devvydont.smprpg.util.items.QuantityLootDrop

class IllagerWarlockBasic(entity: LivingEntity?, entityType: CustomEntityType?) : IllagerWarlockParent(entity as Evoker?, entityType) {
    override fun getItemDrops(): List<LootDrop> {
        return listOf(
            QuantityLootDrop(ItemService.Companion.generate(CustomItemType.SPELL_POWDER), 1, 3, this),
            QuantityLootDrop(ItemService.Companion.generate(Material.EMERALD), 5, 16, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.PREMIUM_SPELL_POWDER), 20, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.ENCHANTED_SPELL_POWDER), 100, this),
            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.EVOKATION_CODEX), 100, this),

            ChancedItemDrop(ItemService.Companion.generate(CustomItemType.HORN_OF_WARLOCK), 200, this)
        )
    }

    override fun updateAttributes() {
        super.updateAttributes()
        updateBaseAttribute(AttributeWrapper.KNOCKBACK_RESISTANCE, 0.8)
        updateBaseAttribute(AttributeWrapper.DEFENSE, 50.0)
    }

    override fun setup() {
        super.setup()
        val evoker = entity as Evoker
        val mobGoals = Bukkit.getMobGoals()
        mobGoals.removeAllGoals(evoker)
        mobGoals.addGoal(evoker, 3, IllagerWarlockSpellGoal(this, null, 30, mapOf(
            Pair(SpellType.TELEPORT, 0.5),
            Pair(SpellType.FIREBALL, 0.45)
        )))
    }
}