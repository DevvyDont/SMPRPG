package xyz.devvydont.smprpg.services

import org.bukkit.*
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.attribute.AttributeWrapper
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.listeners.damage.CriticalDamageListener
import xyz.devvydont.smprpg.listeners.damage.DamagePopupListener
import xyz.devvydont.smprpg.listeners.damage.EnvironmentalDamageListener
import xyz.devvydont.smprpg.services.AttributeService.Companion.instance
import xyz.devvydont.smprpg.services.DifficultyService.Companion.getDamageMultiplier
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.util.formatting.ComponentUtils
import xyz.devvydont.smprpg.util.time.TickTime
import kotlin.math.max
import kotlin.math.pow

/**
 * Overrides all instances of vanilla damage that is desired to fit our new attribute logic
 */
class EntityDamageCalculatorService : Listener, IService {
    // Instantiate event handlers.
    private val criticalDamageListener: CriticalDamageListener = CriticalDamageListener()
    private val popupListener: DamagePopupListener = DamagePopupListener()

    /*
     * SERVICE BOILERPLATE
     */
    @Throws(RuntimeException::class)
    override fun setup() {
        // Start event handlers. You can comment these out to disable game features.
        criticalDamageListener.start() // Handles the critical damage calculation mechanic.
        popupListener.start() // Handles popups in the world for health related events.
    }

    override fun cleanup() {
        HandlerList.unregisterAll(this)

        // Stop event handlers.
        criticalDamageListener.stop()
        popupListener.stop()
    }

    /*
     * HELPER METHODS
     */
    /**
     * The difficulty of the world affects the damage multiplier in some cases, we should account for that
     * @param world The world to extract difficulty from.
     * @param damage The final damage to multiply.
     * @return A multiplied damage value.
     */
    private fun getDifficultyAdjustedDamage(world: World, damage: Double): Double {
        return when (world.difficulty) {
            Difficulty.PEACEFUL -> EASY_DAMAGE_MULTIPLIER * damage * .5
            Difficulty.EASY -> EASY_DAMAGE_MULTIPLIER * damage
            Difficulty.NORMAL -> NORMAL_DAMAGE_MULTIPLIER * damage
            Difficulty.HARD -> HARD_DAMAGE_MULTIPLIER * damage
        }
    }

    /**
     * Given an arrow entity, return its tagged modified damage. Returns 5 by default if this arrow is not tagged.
     * @param projectile The projectile to query.
     * @return The intended base projectile damage.
     */
    fun getBaseProjectileDamage(projectile: Projectile): Double {
        return projectile.persistentDataContainer.getOrDefault(
            PROJECTILE_DAMAGE_TAG,
            PersistentDataType.DOUBLE,
            BASE_ARROW_DAMAGE
        )!!
    }

    /**
     * Given an arrow and a damage value, set the arrow's base damage to deal upon impact.
     * @param projectile The projectile to set damage to.
     * @param damage The damage to set.
     */
    fun setBaseProjectileDamage(projectile: Entity, damage: Double) {
        projectile.persistentDataContainer.set(PROJECTILE_DAMAGE_TAG, PersistentDataType.DOUBLE, damage)
    }

    /**
     * Helper method that determines if a certain damage cause should use the entity's strength stat as a base
     * damage point. We do this so that we can properly override vanilla minecraft's mechanics such as resistance,
     * armor plating, strength, and vanilla enchantments so we can define those attributes ourselves.
     * @param cause The damage cause of an EntityDamageEvent
     * @return true if we should use strength, false if we should use vanilla's logic or handle it somewhere else.
     */
    private fun doesntUseStrengthAttribute(cause: DamageCause): Boolean {
        // If this is environmental damage, we don't need to worry about the case

        if (EnvironmentalDamageListener.getEnvironmentalDamagePercentage(cause) > 0)
            return true

        // Used as a whitelist for certain cases where two entities are damaging each other
        return !when (cause) {
            DamageCause.ENTITY_SWEEP_ATTACK, DamageCause.ENTITY_EXPLOSION, DamageCause.ENTITY_ATTACK, DamageCause.SONIC_BOOM -> true
            else -> false
        }
    }

    /*
     * GENERAL DAMAGE EVENTS
     * The order of events *should* somewhat match the expected order of execution of events, to make this feel less
     * like spaghetti. The general flow of events should be something like this:
     * - EntityDamageEvent/EntityDamageByEntityEvent LOWEST -> HIGHEST (Keep in mind, EntityDamageEvent handlers will ALSO receive EntityDamageByEntityEvent instances!)
     * - CustomEntityDamageByEntityEvent LOWEST -> MONITOR
     * - EntityDamageEvent/EntityDamageByEntityEvent -> MONITOR
     * As you can see, the CustomEntityDamageByEntity executes before the normal Bukkit even is done.
     * This is because our event is fired *during* the Bukkit one, preferably during HIGHEST priority.
     * This is crucial as if we have a "monitor" handler in our custom event, and any modifications were made in
     * a high priority standard event that happened after our custom one, our results will seem mixed.
     */
    /*
     * The very first entry point into our damage calculations. If an entity is dealing damage to another one, set the
     * damage to the attack to the strength stat of the entity.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityDealDamageToAnotherEntity(event: EntityDamageByEntityEvent) {

        if (event.entity is LivingEntity) {
            val living = event.entity as LivingEntity
            if (living.noDamageTicks > 0 && living.noDamageTicks * 2 > living.maximumNoDamageTicks) {
                event.isCancelled = true
                return
            }
        }

        // Depending on the cause of the damage, determine whether we should use strength or not
        if (doesntUseStrengthAttribute(event.cause))
            return

        // If the entity that dealt this damage is not an entity that could potentially have a damage stat, we cannot
        // set the very base damage to the attack.
        if (event.damageSource.causingEntity !is LivingEntity)
            return

        val dealer = event.damageSource.causingEntity as LivingEntity

        val attack: AttributeInstance? = dealer.getAttribute(Attribute.ATTACK_DAMAGE)

        // Do they have an attack damage stat? If they don't, we cannot determine an attack to set.
        if (attack == null)
            return

        // Set the damage
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, attack.value)
    }

    /*
     * It is annoying that baby enemies deal the same damage as normal ones since they are annoying to hit.
     * Apply an -80% damage reduction if an entity is a baby.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @Suppress("unused")
    private fun onBabyEntityDealtDamage(event: EntityDamageByEntityEvent) {
        // Only look for entities that can be a baby

        if (event.damager !is Ageable)
            return

        val ageable = event.damager as Ageable

        // Only look for babies
        if (ageable.isAdult)
            return

        // Apply damage debuff
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage * .2)
    }

    /*
     * Handle simple damage boosting effects. For now, this is just the strength/weakness potion effect.
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    @Suppress("unused")
    private fun onDamageDealtWithStrengthOrWeakness(event: CustomEntityDamageByEntityEvent) {

        if (doesntUseStrengthAttribute(event.vanillaCause))
            return

        if (event.dealer !is LivingEntity)
            return

        val dealer = event.dealer as LivingEntity

        val strength: PotionEffect? = dealer.getPotionEffect(PotionEffectType.STRENGTH)
        val weakness: PotionEffect? = dealer.getPotionEffect(PotionEffectType.WEAKNESS)
        if (strength == null && weakness == null)
            return

        // Start at a 1.0 multiplier. Increase it for strength, and decrease it for weakness.
        var multiplier = 1.0
        if (strength != null)
            multiplier += (strength.amplifier + 1) * DAMAGE_PERCENT_PER_LEVEL_STRENGTH_EFFECT / 100.0
        if (weakness != null)
            multiplier -= (weakness.amplifier + 1) * DAMAGE_PERCENT_PER_LEVEL_WEAKNESS_EFFECT / 100.0

        // Clamp the value to never go below 5% damage.
        multiplier = max(.05, multiplier)
        event.multiplyDamage(multiplier)
    }

    /*
     * Currently, bows have a strength stat that can work as a melee stat which is not intended due
     * to the constraints of minecraft's attribute system. If an entity is holding a bow and does melee damage,
     * they should be severely nerfed.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onMeleeDamageHoldingBow(event: EntityDamageByEntityEvent) {

        if (event.damager !is LivingEntity)
            return

        val dealer = event.damager as LivingEntity
        if (dealer.equipment == null)
            return

        // Is this a melee attack?
        if (event.cause != DamageCause.ENTITY_ATTACK)
            return

        // 95% damage reduction for direct damage with bows.
        val hand: ItemStack = dealer.equipment!!.itemInMainHand
        val handBlueprint = blueprint(hand)
        if (hand.type != Material.AIR) {
            if (SMPRPG.getService(ItemService::class.java).getBlueprint(hand).getItemClassification().isBow) {
                event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage * .05)
                dealer.sendMessage(ComponentUtils.error("That's not how you use this weapon..."))
                dealer.world.playSound(dealer.location, Sound.ENTITY_ENDERMAN_HURT, 1f, 1.25f)
                return
            }
        }

        // 95% damage reduction for abusing bow attributes or trying to dual wield weapons.
        val offHandBlueprint = blueprint(dealer.equipment!!.itemInOffHand)
        if (isTryingToBowStackExploit(dealer) || handBlueprint.getItemClassification().isWeapon && offHandBlueprint.getItemClassification().isWeapon) {
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, event.damage * .05)
            dealer.sendMessage(ComponentUtils.error("You seem to be struggling trying to deal damage with the items you are holding..."))
            dealer.world.playSound(dealer.location, Sound.ENTITY_ENDERMAN_HURT, 1f, 1.25f)
        }
    }

    /**
     * Handle the "armor" mechanic. Currently, you just get a small i-frame bonus when you have armor.
     * @param event The [EntityDamageByEntityEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityDamagedEntityWithArmor(event: EntityDamageByEntityEvent) {

        if (event.getEntity() !is LivingEntity)
            return

        val damaged = event.entity as LivingEntity

        // If they currently have i-frames, don't recalculate them.
        if (damaged.noDamageTicks > 0)
            return

        val leveled = SMPRPG.getService(EntityService::class.java).getEntityInstance(damaged)

        val armor: AttributeInstance? = damaged.getAttribute(Attribute.ARMOR)
        var iframeTicks = 0

        // Armor changes how many iframes we get for this attack
        if (armor != null)
            iframeTicks = armor.value.toInt() * 2

        val noDamageTicks = leveled.getInvincibilityTicks() + iframeTicks

        damaged.maximumNoDamageTicks = noDamageTicks
    }

    /*
     * PROJECTILE/BOW RELATED DAMAGE EVENTS
     */
    /**
     * Used to tag arrows with the proper damage value based on the attack damage stat of the shooter.
     * @param event The [EntityShootBowEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onEntityShootBow(event: EntityShootBowEvent) {

        val attackDamage = event.getEntity().getAttribute(Attribute.ATTACK_DAMAGE)
        // This entity doesn't have an attack damage attribute, we can't do anything.
        if (attackDamage == null)
            return

        // Using the attack of the entity, set a base damage value to use.
        var arrowDamage = attackDamage.value

        // Modify it by the force of the event.
        arrowDamage *= event.force.toDouble()

        // If this wasn't a player, consider difficulty and give the arrow a 2x boost to nullify velocity falloff since entities don't shoot arrows that travel at max speed
        if (event.getEntity() !is Player) arrowDamage =
            getDifficultyAdjustedDamage(event.getEntity().world, arrowDamage) * AI_BOW_FORCE_FACTOR

        // Punish bow stacking.
        var isDualWieldingWeapons = false
        if (event.getEntity().equipment != null) {
            val handBlueprint = blueprint(event.getEntity().equipment!!.itemInMainHand)
            val offhandBlueprint = blueprint(event.getEntity().equipment!!.itemInOffHand)
            isDualWieldingWeapons =
                handBlueprint.getItemClassification().isWeapon && offhandBlueprint.getItemClassification().isWeapon
        }

        if (isTryingToBowStackExploit(event.getEntity()) || isDualWieldingWeapons) {
            arrowDamage *= .05
            event.getEntity()
                .sendMessage(ComponentUtils.error("You seem to be struggling shooting arrows correctly with the items you are holding..."))
            event.getEntity().world
                .playSound(event.getEntity().location, Sound.ENTITY_ENDERMAN_HURT, 1f, 1.25f)
        }

        // Set the damage.
        setBaseProjectileDamage(event.projectile, arrowDamage)
    }

    /**
     * Used to tag tridents with the proper damage value based on the attack damage stat of the shooter.
     * @param event The [ProjectileLaunchEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onEntityThrowTrident(event: ProjectileLaunchEvent) {

        val projectile = event.getEntity()
        if (projectile !is Trident)
            return

        if (projectile.shooter !is LivingEntity)
            return

        // Players always crit tridents.
        val shooter = projectile.shooter as LivingEntity
        if (shooter is Player)
            projectile.isCritical = true

        val attackDamage: AttributeInstance? = shooter.getAttribute(Attribute.ATTACK_DAMAGE)
        // This entity doesn't have an attack damage attribute, we can't do anything.
        if (attackDamage == null)
            return

        // Using the attack of the entity, set a base damage value to use.
        var tridentDamage = attackDamage.value

        // If this wasn't a player, consider difficulty and give the arrow a 2x boost to nullify velocity falloff since entities don't shoot arrows that travel at max speed
        if (shooter !is Player)
            tridentDamage = getDifficultyAdjustedDamage(shooter.world, tridentDamage)

        // Punish bow stacking.
        var isDualWieldingWeapons = false
        val equipment = shooter.equipment
        if (equipment != null) {
            val handBlueprint = blueprint(equipment.itemInMainHand)
            val offhandBlueprint = blueprint(equipment.itemInOffHand)
            isDualWieldingWeapons =
                handBlueprint.getItemClassification().isWeapon && offhandBlueprint.getItemClassification().isWeapon
        }

        if (isTryingToBowStackExploit(shooter) || isDualWieldingWeapons) {
            tridentDamage *= .05
            shooter.sendMessage(ComponentUtils.error("You seem to be struggling throwing tridents correctly with the items you are holding..."))
            shooter.world.playSound(shooter.location, Sound.ENTITY_ENDERMAN_HURT, 1f, 1.25f)
        }

        // Set the damage.
        setBaseProjectileDamage(projectile, tridentDamage)
    }

    /**
     * As soon as we possibly can, intercept events where an arrow is damaging an entity.
     * We need to set the base damage of this event to the arrow's base damage and apply
     * a multiplier on it based on arrow velocity.
     * @param event The [EntityDamageByEntityEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    @Suppress("unused")
    private fun onArrowDamageReceived(event: EntityDamageByEntityEvent) {
        // Ignore events not involving an arrow harming something.

        if (event.damager !is AbstractArrow)
            return

        val arrow = event.damager as AbstractArrow

        // Send ding noise to player if it was a non PVP interaction
        if (event.damageSource.causingEntity is Player && event.entity !is Player) {
            val player = event.damageSource.causingEntity as Player
            player.playSound(
                player.location,
                Sound.ENTITY_ARROW_HIT_PLAYER,
                .75f,
                .8f
            )
        }

        // Retrieve the base damage of this arrow assuming this arrow is at max velocity.
        val baseArrowDamage = getBaseProjectileDamage(arrow)

        // Convert the velocity to m/s by using the tick rate of the server
        val arrowVelocity: Double = arrow.velocity.length() * TickTime.TPS
        val arrowForce: Double = arrowVelocity / MAX_ARROW_DAMAGE_VELOCITY
        val newArrowDamage = baseArrowDamage * arrowForce

        // Multiply the base damage of this event by how fast this arrow is going compared to the "max" velocity
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, newArrowDamage)
    }

    /*
     * When entities launch projectiles, the attack damage stat of the entity needs to be
     * transferred to the projectile, so we can listen for it when it deals damage.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityNonArrowProjectileLaunch(event: ProjectileLaunchEvent) {
        // Ignore arrows. This is handled in its own case.

        if (event.getEntity() is AbstractArrow)
            return

        // See if there was an entity that launched this projectile that can have attributes.
        if (event.getEntity().shooter !is LivingEntity)
            return
        val shooter = event.getEntity().shooter as LivingEntity

        // See if they have an attack damage stat
        val attack: AttributeInstance? = shooter.getAttribute(Attribute.ATTACK_DAMAGE)
        if (attack == null)
            return

        // This projectile should do the damage to the attack stat.
        setBaseProjectileDamage(event.getEntity(), attack.value)
    }

    /*
     * Used to listen for when damage tagged projectile is dealing damage to an entity.
     * This method ignores arrows as that is handled somewhere else.
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityDamagedByNonArrowProjectile(event: EntityDamageByEntityEvent) {
        // We only want to listen to projectile damage events.

        if (event.cause != DamageCause.PROJECTILE)
            return

        if (event.damager !is Projectile)
            return
        val projectile = event.damager as Projectile

        // Do not handle arrows. That is done somewhere else.
        if (projectile is AbstractArrow)
            return

        // Set the damage of the event to the damage that the projectile is supposed to do
        event.setDamage(EntityDamageEvent.DamageModifier.BASE, getBaseProjectileDamage(projectile))
    }

    /*
     * MISC ENTITY DAMAGE SOURCE EVENTS (WARDEN BEAMS, CREEPER EXPLOSIONS ETC)
     */
    /*
     * MAIN DAMAGE CALCULATIONS
     */
    /**
     * Hook into when two entities are damaging each other, and construct a custom event where
     * the entity dealing damage is instead set to the entity that *caused* the damage to occur.
     * This makes it much easier to intercept events such as a player shooting a zombie with
     * a bow, as it does the work of backtracking a projectile shooter back to the player.
     * This should also go last, so that events can modify vanilla interactions first if needed.
     * Our plugin should ideally rely on the custom event to prevent mismatches.
     * @param event The [EntityDamageByEntityEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @Suppress("unused")
    private fun onVanillaDamageEntityVsEntity(event: EntityDamageByEntityEvent) {
        // Handle the case where the source is a projectile. If it is, there is potential that
        // we are getting shot by a bow.

        val damaged = event.getEntity()
        var dealer = event.damager

        var projectile: Projectile? = null

        // Handle the case where a projectile was thrown by an entity and configure the correct damage dealer
        if (event.cause == DamageCause.PROJECTILE) {
            projectile = dealer as Projectile

            // If this projectile sourced from something else, use that instead
            if (projectile.shooter is LivingEntity)
                dealer = projectile.shooter as Entity
        }

        // Call the event and check if it is canceled for any reason.
        val eventWrapper = CustomEntityDamageByEntityEvent(event, damaged, dealer, projectile)
        eventWrapper.callEvent()
        if (eventWrapper.isCancelled)
            return

        event.setDamage(EntityDamageEvent.DamageModifier.BASE, eventWrapper.finalDamage)
    }

    /**
     * Handle the effectiveness of defense while wearing armor.
     * Analyze the total defense of the entity who is taking damage, multiply the damage
     * by their resistance multiplier.
     * Since this is a multiplicative operation, this should run almost last.
     * @param event The [CustomEntityDamageByEntityEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onEntityDamageEntityWithDefense(event: CustomEntityDamageByEntityEvent) {
        // Using the defense of the receiver, reduce the damage

        if (event.damaged is Attributable) {
            // Apply the entity's defense attribute
            val leveledEntity =
                SMPRPG.getService(EntityService::class.java).getEntityInstance(event.damaged)
            event.multiplyDamage(calculateDefenseDamageMultiplier(leveledEntity.defense.toDouble()))
        }
    }

    /*
     * When players deal melee damage, we need to more aggressively decrease their damage output since Minecraft's
     * vanilla formula is not good enough for inflated damage numbers. The reasoning for this is to reward well-timed
     * hits and to prevent spam clicking DPS abuse on bosses since they don't have i-frames.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlayerAttackWhileOnCooldown(event: CustomEntityDamageByEntityEvent) {
        // Is the entity dealing damage a player? We only care about players.

        if (event.dealer !is Player)
            return
        val player = event.dealer as Player

        // We only care about melee interactions. Events caused by other sources are not affected by cooldown.
        if (event.originalEvent.cause != DamageCause.ENTITY_ATTACK)
            return

        val cooldown: Float = player.attackCooldown

        // Is the player on cooldown? Don't do anything if they are dealing full damage hit.
        // If the player was *close enough* then allow vanilla Minecraft's damage rules for damage reduction.
        if (cooldown >= COOLDOWN_FORGIVENESS_THRESHOLD)
            return

        // Apply the multiplier.
        val multiplier = 0.05 + (1.0 - 0.05) * cooldown.toDouble().pow(2.0)
        event.multiplyDamage(multiplier)
    }

    /*
     * When players deal melee damage, we need to check if it was a because of a sweeping attack.
     * We need to apply their sweeping multiplier to the damage.
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    @Suppress("unused")
    private fun onPlayerPerformSweepingAttack(event: CustomEntityDamageByEntityEvent) {
        // We only care about sweeping interactions.

        if (event.originalEvent.cause != DamageCause.ENTITY_SWEEP_ATTACK)
            return

        if (event.dealer !is LivingEntity) return
        val living = event.dealer as LivingEntity

        // Get the sweeping efficiency. This is quite literally just a multiplier on the damage.
        val sweepingEfficiency = instance.getAttribute(living, AttributeWrapper.SWEEPING)
        if (sweepingEfficiency == null)
            return

        event.multiplyDamage(sweepingEfficiency.getValue())
    }

    /**
     * Handle the incoming damage multiplier due to being on a different difficulty for players.
     * Only the entity receiving damage will be affected if they are a player and on a difficulty that isn't
     * standard.
     * @param event The [CustomEntityDamageByEntityEvent] event that provides us with relevant context.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    @Suppress("unused")
    private fun onEntityDamageEntityOnSpecialDifficulty(event: CustomEntityDamageByEntityEvent) {
        // First make sure the receiver is a player.

        if (event.damaged !is Player)
            return

        // Ignore this event if another player is causing this. PVP is unaffected.
        if (event.dealer is Player)
            return
        val player = event.dealer as Player

        // Multiply their difficulty damage multiplier.
        val multiplier = getDamageMultiplier(SMPRPG.getService(DifficultyService::class.java).getDifficulty(player))
        event.multiplyDamage(multiplier.toDouble())
    }

    companion object {
        /*
     * STATIC CONSTANTS (SETTINGS)
     */
        // How much to decrease damage by depending on the difficulty.
        const val EASY_DAMAGE_MULTIPLIER: Float = .5f
        const val NORMAL_DAMAGE_MULTIPLIER: Float = .75f
        const val HARD_DAMAGE_MULTIPLIER: Float = 1f

        // How effective defense is. The lower the number, the better damage reduction from defense is.
        const val DEFENSE_FACTOR: Int = 100

        // What should be the attack cooldown threshold to use vanilla's logic?
        const val COOLDOWN_FORGIVENESS_THRESHOLD: Float = 0.9f

        // Used to store the amount of damage an arrow entity should do.
        val PROJECTILE_DAMAGE_TAG: NamespacedKey = NamespacedKey("smprpg", "projectile_damage")

        // The base damage to an arrow if it was not assigned one when it is launched. This could happen in the event
        // that we do not set a damage value on an arrow entity when it is fired from any source.
        const val BASE_ARROW_DAMAGE: Double = 5.0

        // The force factor to assume for entities. Entities do not have "bow force" in the same way
        // that players do, so we can make one up here. They struggle to shoot arrows at full velocity but still shoot
        // them with a full bow force of 1.0. We should give them a little boost.
        const val AI_BOW_FORCE_FACTOR: Double = 1.75

        // The velocity an arrow needs to be travelling to deal "maximum" damage. Arrows traveling this fast
        // will deal exactly the amount of damage to the attack damage stat that was applied to it when it was shot.
        const val MAX_ARROW_DAMAGE_VELOCITY: Double = 60.0

        // How much percentage of damage to increase per level of strength an entity has.
        const val DAMAGE_PERCENT_PER_LEVEL_STRENGTH_EFFECT: Double = 30.0

        // How much percentage of damage to decrease per level of weakness an entity has.
        const val DAMAGE_PERCENT_PER_LEVEL_WEAKNESS_EFFECT: Double = 15.0

        // How much defense per level of resistance is applied when an entity has the resistance effect.
        // This will actually end up being implemented in LeveledEntity#getDefense() for convenience.
        const val DEFENSE_PER_LEVEL_RESISTANCE: Int = 150

        /**
         * Checks the state of this entity regarding holding bows. Returns a map for all hand related equipment slots and
         * whether they are holding a bow.
         * @param entity The entity in question.
         * @return A map of equipment slot states regarding holding bows.
         */
        fun holdingBowInHand(entity: LivingEntity): MutableMap<EquipmentSlotGroup, Boolean> {

            val map = HashMap<EquipmentSlotGroup, Boolean>()
            map.put(EquipmentSlotGroup.MAINHAND, false)
            map.put(EquipmentSlotGroup.OFFHAND, false)
            map.put(EquipmentSlotGroup.HAND, false)

            if (entity.equipment == null)
                return map

            // Analyze what's in both hands and update accordingly if a bow is present.
            val mainHand = entity.equipment!!.itemInMainHand
            val offHand = entity.equipment!!.itemInOffHand
            if (SMPRPG.getService(ItemService::class.java).getBlueprint(mainHand).getItemClassification().isBow)
                map.put(EquipmentSlotGroup.MAINHAND, true)
            if (SMPRPG.getService(ItemService::class.java).getBlueprint(offHand).getItemClassification().isBow)
                map.put(EquipmentSlotGroup.OFFHAND, true)

            val wieldingMainHand = map.getOrDefault(EquipmentSlotGroup.MAINHAND, false)
            val wieldingOffhand = map.getOrDefault(EquipmentSlotGroup.OFFHAND, false)

            if (wieldingOffhand || wieldingMainHand)
                map.put(EquipmentSlotGroup.HAND, true)

            return map
        }

        /**
         * The vanilla minecraft attribute system has a significant flaw. Since bows can be offhanded, we need to make sure
         * that the entity isn't dual wielding an attribute increasing weapon in their main hand if they are shooting
         * using their offhand, or dual wielding bows.
         * @param entity The entity that is being state checked.
         * @return True if they satisfy the condition of bow stack exploiting, false if they seem to be clear.
         */
        fun isTryingToBowStackExploit(entity: LivingEntity): Boolean {
            if (entity.equipment == null) return false

            // First check the easy condition, are they dual wielding bows?
            val bowState: MutableMap<EquipmentSlotGroup, Boolean> = holdingBowInHand(entity)
            if (bowState.getOrDefault(EquipmentSlotGroup.MAINHAND, false) && bowState.getOrDefault(EquipmentSlotGroup.OFFHAND, false))
                return true

            // Now check if they are trying to shoot a bow in their offhand but hold a weapon in their man hand.
            if (!bowState.getOrDefault(EquipmentSlotGroup.OFFHAND, false))
                return false

            val main = entity.equipment!!.itemInMainHand
            if (main.type == Material.AIR)
                return false

            // At this point, we know we are off-handing a bow. Are we attempting to hold a stat increasing weapon in our main hand as well?
            val blueprint = SMPRPG.getService(ItemService::class.java).getBlueprint(main)
            return blueprint.getItemClassification().isWeapon

            // We seem to be innocent...
        }

        /**
         * Given a defense attribute value, return the multiplier of some damage to take.
         * For example, if we have 100 defense, we should only take 10% of the damage
         * If we have 10 defense, we should take only 50% of the damage, etc.
         *
         * @param defense The defense to calculate with.
         * @return The multiplier to use.
         */
        fun calculateDefenseDamageMultiplier(defense: Double): Double {
            return DEFENSE_FACTOR.toDouble() / (max(defense, 0.0) + DEFENSE_FACTOR)
        }

        /**
         * Given a defense attribute value, return the percentage of damage to negate.
         *
         * @param defense The defense to calculate with.
         * @return The multiplier to use.
         */
        @JvmStatic
        fun calculateResistancePercentage(defense: Double): Double {
            // Since we know that the damage multiplier for defense is ALWAYS (0-1], we can reverse it

            return 1.0 - calculateDefenseDamageMultiplier(defense)
        }

        /**
         * Given a health and defense value, calculate the effective health of an entity.
         * Effective health can be calculated as HP/DEF% where DEF% is the percentage of damage something takes due to DEF.
         *
         * @param health The health to calculate with.
         * @param defense The defense to calculate with.
         * @return The EHP of the HP and DEF combination.
         */
        @JvmStatic
        fun calculateEffectiveHealth(health: Double, defense: Double): Double {
            return health / calculateDefenseDamageMultiplier(defense)
        }
    }
}
