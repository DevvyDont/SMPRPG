package xyz.devvydont.smprpg.items.blueprints.sets.abomination

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.AttackRange
import io.papermc.paper.datacomponent.item.PiercingWeapon
import io.papermc.paper.datacomponent.item.SwingAnimation
import io.papermc.paper.registry.keys.SoundEventKeys
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.*
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.entity.slayer.shambling.ShamblingAbominationParent
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.ItemClassification
import xyz.devvydont.smprpg.items.attribute.AdditiveAttributeEntry
import xyz.devvydont.smprpg.items.attribute.AttributeEntry
import xyz.devvydont.smprpg.items.attribute.MultiplicativeAttributeEntry
import xyz.devvydont.smprpg.items.base.CustomAttributeItem
import xyz.devvydont.smprpg.items.interfaces.*
import xyz.devvydont.smprpg.services.EntityService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.slayer.quest.SlayerType
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.formatting.Symbols
import xyz.devvydont.smprpg.util.items.AbilityUtil
import xyz.devvydont.smprpg.util.persistence.KeyStore

class AbominableHalberd(itemService: ItemService, type: CustomItemType) : CustomAttributeItem(itemService, type),
    Listener, IHeaderDescribable, IBreakableEquipment, IRepairable, ISlayerProficiencyBoost, IModelOverridden {

    override val itemClassification: ItemClassification get() = ItemClassification.SWORD
    override val repairMaterial: MutableCollection<ItemStack> get() = mutableListOf(itemService.getCustomItem(CustomItemType.REVILED_VISCERA))

    override val slayerToBoost: SlayerType get() = SlayerType.SHAMBLING_ABOMINATION
    override val slayerProficiencyBoost: Int get() = 75

    override fun getHeader(itemStack: ItemStack?): MutableList<Component?> {
        val components: MutableList<Component?> = ArrayList()
        components.add(AbilityUtil.getAbilityComponent("Divine Executioner", passive = true))
        components.add(
            ComponentUtils.create("Attacks deal ")
                .append(ComponentUtils.create(DAMAGE_MULT.toInt().toString() + "x", NamedTextColor.GREEN))
                .append(ComponentUtils.create(" damage against"))
        )
        components.add(
            ComponentUtils.merge(
                ComponentUtils.create("Shambling Abominations", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD),
                ComponentUtils.create(" and associated mobs.")
            )
        )
        components.add(
            ComponentUtils.create("Attacks deal ").append(ComponentUtils.create("10%", NamedTextColor.RED))
                .append(ComponentUtils.create(" damage to any other mobs."))
        )
        components.add(
            ComponentUtils.create("Attacks heal ")
                .append(ComponentUtils.create("+" + HEAL_AMOUNT.toInt(), NamedTextColor.RED))
                .append(ComponentUtils.create(Symbols.HEART, NamedTextColor.RED))
                .append(ComponentUtils.create(" on critical hits."))
        )
        components.add(
            ComponentUtils.create("Receive ")
                .append(ComponentUtils.create(BOSS_DAMAGE_REDUCTION.toInt().toString() + "%", NamedTextColor.GREEN))
                .append(ComponentUtils.create(" less damage from")).append(
                    ComponentUtils.create(
                        " Shambling Abominations",
                        NamedTextColor.DARK_PURPLE,
                        TextDecoration.BOLD
                    )
                ).append(ComponentUtils.create("."))
        )

        return components
    }

    override fun getAttributeModifiers(item: ItemStack): MutableCollection<AttributeEntry?> {
        return if (item.persistentDataContainer.getOrDefault(MODE_KEY, PersistentDataType.BOOLEAN!!, false)
        )  // Serialized for STAB mode
        {
            mutableListOf(
                AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 450.0),
                MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.75),
                AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 90.0)
            )
        } else {
            mutableListOf(
                AdditiveAttributeEntry(AttributeWrapper.STRENGTH, 450.0),
                MultiplicativeAttributeEntry(AttributeWrapper.ATTACK_SPEED, -.6),
                AdditiveAttributeEntry(AttributeWrapper.CRITICAL_DAMAGE, 90.0)
            )
        }
    }

    override fun getPowerRating(): Int {
        return 30
    }

    override fun getActiveSlot(): EquipmentSlotGroup {
        return EquipmentSlotGroup.MAINHAND
    }


    override fun updateItemData(itemStack: ItemStack) {
        super.updateItemData(itemStack)

        var range = 4.0f

        if (itemStack.persistentDataContainer.getOrDefault(MODE_KEY, PersistentDataType.BOOLEAN!!, false)
        ) {
            itemStack.setData<SwingAnimation?>(
                DataComponentTypes.SWING_ANIMATION, SwingAnimation.swingAnimation()
                    .type(SwingAnimation.Animation.STAB)
                    .duration(12)
                    .build()
            )
            itemStack.setData<PiercingWeapon?>(
                DataComponentTypes.PIERCING_WEAPON, PiercingWeapon.piercingWeapon()
                    .sound(SoundEventKeys.ITEM_SPEAR_ATTACK)
                    .hitSound(SoundEventKeys.ITEM_SPEAR_HIT)
                    .build()
            )
            range = 6.0f
        } else {
            itemStack.unsetData(DataComponentTypes.SWING_ANIMATION)
            itemStack.unsetData(DataComponentTypes.PIERCING_WEAPON)
        }

        itemStack.setData<AttackRange?>(
            DataComponentTypes.ATTACK_RANGE, AttackRange.attackRange()
                .hitboxMargin(0.3f)
                .maxReach(range)
                .maxCreativeReach(range)
                .build()
        )
    }

    override fun getMaxDurability(): Int {
        return 1_000
    }

    override fun getDisplayKey(): Key {
        return IModelOverridden.ofItemType(type)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onAttackWithHalberd(event: CustomEntityDamageByEntityEvent) {
        // Did the attacker use the halberd?

        if (event.dealer !is LivingEntity) return
        val dealer = event.dealer

        if (dealer.equipment == null) return

        if (!isItemOfType(dealer.equipment!!.itemInMainHand)) return

        // Is this a direct event?
        if (event.isIndirect) return

        // Is the attacked mob a shambling abomination or associated?
        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged)
        val isBoss = (entity.getEntity().persistentDataContainer
            .getOrDefault(
                KeyStore.SLAYER_SPAWN_TYPE!!,
                PersistentDataType.STRING!!,
                ""
            ) == ShamblingAbominationParent.SPAWN_MOB_FLAG)
        if (!isBoss) {
            // If it isn't, we quarter our damage instead of sextuple it.
            // This makes it not viable outside of slayer usage.
            event.multiplyDamage(0.1)
        } else {
            event.multiplyDamage(DAMAGE_MULT)
        }

        // We can safely heal the player now
        if (event.isCritical) dealer.heal(HEAL_AMOUNT)
    }

    @EventHandler
    fun onReceiveDamageFromBoss(event: CustomEntityDamageByEntityEvent) {
        // Is the attacker a shambling abomination?
        val entity = SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged)
        val isBoss = (entity.getEntity().persistentDataContainer
            .getOrDefault(
                KeyStore.SLAYER_SPAWN_TYPE!!,
                PersistentDataType.STRING!!,
                ""
            ) == ShamblingAbominationParent.SPAWN_MOB_FLAG)
        if (isBoss) {
            // Is the attacker holding the halberd?
            if (event.damaged !is LivingEntity) return
            val damaged = event.damaged

            if (damaged.equipment == null) return

            if (!isItemOfType(damaged.equipment!!.itemInMainHand)) return

            // Is this a direct event?
            if (event.isIndirect) return

            // Reduce damage
            event.multiplyDamage(1.0 - (BOSS_DAMAGE_REDUCTION / 100.0))
        }
    }

    @EventHandler
    fun onToggleHalberdMode(event: PlayerInteractEvent) {
        if (event.action.isRightClick) {
            if (event.hand == EquipmentSlot.HAND) {
                val player = event.player
                val item = player.equipment.itemInMainHand
                if (!isItemOfType(item)) return

                val attackMode = item.persistentDataContainer.getOrDefault(MODE_KEY, PersistentDataType.BOOLEAN!!, false)
                val modeComp = if (attackMode) ComponentUtils.create("SLASH", NamedTextColor.RED)
                else ComponentUtils.create("STAB", NamedTextColor.DARK_PURPLE)

                player.sendMessage(
                    ComponentUtils.merge(
                        ComponentUtils.create("Switched to "),
                        modeComp,
                        ComponentUtils.create(" mode.")
                    )
                )
                item.editPersistentDataContainer { pdc: PersistentDataContainer? ->
                    pdc!!.set(
                        MODE_KEY,
                        PersistentDataType.BOOLEAN,
                        !item.persistentDataContainer
                            .getOrDefault(
                                MODE_KEY,
                                PersistentDataType.BOOLEAN!!,
                                false
                            )!!
                    )
                }
                blueprint(item).updateItemData(item)
                player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            }
        }
    }

    companion object {
        const val DAMAGE_MULT: Double = 8.0
        const val HEAL_AMOUNT: Double = 50.0
        const val BOSS_DAMAGE_REDUCTION: Double = 10.0
        val MODE_KEY: NamespacedKey = NamespacedKey(plugin, "halberd_attack_mode")
    }
}
