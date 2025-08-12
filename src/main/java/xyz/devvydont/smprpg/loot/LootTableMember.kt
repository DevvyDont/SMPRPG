package xyz.devvydont.smprpg.loot

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.enchantments.EnchantmentOffer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.devvydont.smprpg.enchantments.calculator.EnchantmentCalculator
import xyz.devvydont.smprpg.events.CustomChancedItemDropSuccessEvent
import xyz.devvydont.smprpg.events.CustomItemDropRollEvent
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.items.LootSource

/**
 * Represents something that may appear in a loot table.
 * The "chance" is the percent chance that this item is rolled FOR EVERY SLOT in an inventory.
 * The "rolls" is how many times to roll for this member, meaning it can show up multiple times when being rolled.
 * The min and max constraints allow you to define a range of the item stack quantity that it can spawn in.
 *
 * Currently, these are only used for chests and stuff
 */
class LootTableMember(val item: ItemStack) : LootSource {
    var min: Int = 1
        private set
    var max: Int = 1
        private set
    var rolls: Int = 1
        private set
    var chance: Float = 1f
        private set
    var isWantEnchants: Boolean = false
        private set
    private var enchantPower = 15

    fun withMin(min: Int): LootTableMember {
        this.min = min
        return this
    }

    fun withMax(max: Int): LootTableMember {
        this.max = max
        return this
    }

    fun withChance(chance: Float): LootTableMember {
        this.chance = chance
        return this
    }

    fun withRolls(rolls: Int): LootTableMember {
        this.rolls = rolls
        return this
    }

    fun withEnchants(wantEnchants: Boolean, power: Int): LootTableMember {
        this.isWantEnchants = wantEnchants
        this.enchantPower = power
        return this
    }

    fun enchantItem(item: ItemStack) {
        // Make an enchantment calculator and use the best option possible with the desired enchanting level

        val calculator = EnchantmentCalculator(item, EnchantmentCalculator.MAX_BOOKSHELF_BONUS, enchantPower)
        val results: MutableList<EnchantmentOffer> =
            calculator.calculate()[EnchantmentCalculator.EnchantmentSlot.EXPENSIVE]!!

        // Apply them!
        for (offer in results) item.addUnsafeEnchantment(offer.enchantment, offer.enchantmentLevel)
    }

    fun roll(player: Player): ItemStack? {
        val reward = this.item.clone()
        reward.amount = (Math.random() * this.max).toInt() + this.min

        if (this.isWantEnchants) enchantItem(reward)

        val rollEvent =
            CustomItemDropRollEvent(player, player.inventory.itemInMainHand, chance.toDouble(), reward, this)
        rollEvent.callEvent()

        val success = Math.random() < rollEvent.chance
        if (!success)
            return null

        CustomChancedItemDropSuccessEvent(player, rollEvent.chance, reward, this).callEvent()
        return reward
    }

    /**
     * Component that displays in chat when a rare drop is obtained
     *
     * @return
     */
    override fun getAsComponent(): Component {
        return ComponentUtils.create("looting a ").append(ComponentUtils.create("chest!", NamedTextColor.GOLD))
    }
}
