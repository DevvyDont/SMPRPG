package xyz.devvydont.smprpg.items.interfaces

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.AttackRange
import io.papermc.paper.datacomponent.item.PiercingWeapon
import io.papermc.paper.datacomponent.item.SwingAnimation
import io.papermc.paper.datacomponent.item.Weapon
import net.kyori.adventure.key.Key
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack

interface IMageBeam: IMagicWeapon {
    val hitParticle: Particle
    val missParticle: Particle
    val particleDensity: Int
    val particleRange: Int
    val manaCost: Int


    companion object {
        fun updateStaffComponents(itemStack: ItemStack, range: Int, hitboxMargin: Float, swingSound: Key, hitSound: Key) {
            itemStack.setData(DataComponentTypes.WEAPON, Weapon.weapon().build())  // Flag the item as a weapon.
            itemStack.setData(                                                     // Give the item our actual beam effects by increasing the attack range
                DataComponentTypes.ATTACK_RANGE, AttackRange.attackRange()
                    .maxReach(range.toFloat())
                    .maxCreativeReach(range.toFloat())
                    .hitboxMargin(hitboxMargin)
                    .build()
            )
            itemStack.setData(                                                     // Change our swing animation to spear stab, looks more like casting as opposed to whacking
                DataComponentTypes.SWING_ANIMATION, SwingAnimation.swingAnimation()
                    .type(SwingAnimation.Animation.STAB)
                    .duration(10)
                    .build()
            )
            itemStack.setData(                                                     // Add a small piercing bonus for grouped mobs
                DataComponentTypes.PIERCING_WEAPON, PiercingWeapon.piercingWeapon()
                    .dealsKnockback(false)
                    .dismounts(false)
                    .sound(swingSound)
                    .hitSound(hitSound)
                    .build()
            )
            itemStack.setData(DataComponentTypes.MINIMUM_ATTACK_CHARGE, 0.8f)      // Make this a "heavier" weapon by requiring 80% recharge before swinging again.
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 1)                // Limit stacking to single items.
        }
    }
}
