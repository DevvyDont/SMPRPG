package xyz.devvydont.smprpg.entity.slayer.illager

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.DeathProtection
import io.papermc.paper.datacomponent.item.DyedItemColor
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect
import net.kyori.adventure.key.Key
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.EntityRemoveEvent
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.entity.EntityTransformEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import xyz.devvydont.smprpg.SMPRPG
import xyz.devvydont.smprpg.SMPRPG.Companion.plugin
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.slayer.SlayerBossInstance
import xyz.devvydont.smprpg.entity.slayer.illager.goals.AuraType
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService
import xyz.devvydont.smprpg.services.ItemService.Companion.blueprint
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.persistence.KeyStore
import xyz.devvydont.smprpg.util.time.TickTime

open class IllagerWarlockParent
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: LivingEntity?, entityType: CustomEntityType?) : SlayerBossInstance<Evoker>(entity as Evoker?, entityType) {

    val fireballs = ArrayList<Fireball>()
    var revived : Boolean = false
    var shieldDisplay : ItemDisplay? = null

    open val LOOT_APTITUDE_SCROLL : ItemStack = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.APTITUDE)
    open val LOOT_INSIGHT_SCROLL : ItemStack = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.INSIGHT)
    open val LOOT_BURDEN_SCROLL : ItemStack = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.BURDEN)
    open val LOOT_VIGILANTE_SCROLL : ItemStack = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.VIGILANTE)

    override fun setup() {
        mobTypes.add(MobType.BOSS);
        mobTypes.add(MobType.ILLAGER);
        mobTypes.add(MobType.HUMANOID);

        super.setup()

        // Set up our shield display
        this.shieldDisplay = _entity!!.world.spawnEntity(_entity.location, EntityType.ITEM_DISPLAY) as ItemDisplay

        val haloItem = generate(Material.STICK)
        haloItem.setData<Key>(DataComponentTypes.ITEM_MODEL, Key.key("smprpg:illager_warlock_shield"))
        haloItem.setData(DataComponentTypes.DYED_COLOR, AuraType.DAMAGE.color)
        shieldDisplay!!.setItemStack(haloItem)
        shieldDisplay!!.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND)

        // Setup equipment for boss
        val equipment = _entity.equipment

        val totem = generate(Material.TOTEM_OF_UNDYING)
        totem.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().addEffect(ConsumeEffect.applyStatusEffects(listOf(
            PotionEffect(PotionEffectType.RESISTANCE, 20, 100, false, false)), 1.0f)).build())
        equipment?.setItemInOffHand(totem)
        val helmet = generate(Material.DISPENSER)
        helmet.setData(DataComponentTypes.ITEM_MODEL, HOOD_KEY)
        equipment?.setHelmet(helmet)
        equipment?.setItemInMainHand(generate(CustomItemType.SIMPLE_TOME))

        equipment?.itemInMainHandDropChance = 0f
        equipment?.itemInOffHandDropChance = 0f
        equipment?.helmetDropChance = 0f
        equipment?.chestplateDropChance = 0f
        equipment?.leggingsDropChance = 0f
        equipment?.bootsDropChance = 0f

        _entity.isSilent = true

        // Illager Warlocks can also spawn other quests.
        // This encourages collaborative slayer gameplay.
        _entity.getPersistentDataContainer()
            .set(KeyStore.SLAYER_SPAWN_TYPE, PersistentDataType.STRING, SPAWN_MOB_FLAG)
    }

    override fun getSkillExperienceMultiplier(): Double {
        return 20.0
    }

    /*
    * 5 min
    */
    override fun getTimeLimit(): Long {
        return 60L * 5L
    }

    @EventHandler
    fun onRemoveEntity(event : EntityRemoveFromWorldEvent) {
        if (event.entity == this.entity) {
            this.shieldDisplay!!.remove()
        }
    }

    @EventHandler
    fun onFireballExplode(event : EntityExplodeEvent) {
        if (event.entity in fireballs) {
            event.blockList().clear()
            fireballs.remove(event.entity)
        }
    }

    @EventHandler
    fun onBossResurrect(event : EntityResurrectEvent) {
        val entity = event.entity
        if (entity == this.entity) {
            entity.isInvulnerable = true
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                val maxHp: AttributeInstance? = entity.getAttribute(Attribute.MAX_HEALTH)
                entity.heal(maxHp!!.value)
                entity.isInvulnerable = false
            }, 0L)
            this.revived = true
        }
    }

    @EventHandler
    fun onWarlockTakeDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            if (entity.isInvulnerable) {
                event.isCancelled = true
                return
            }
            entity.location.world.playSound(entity.location, Sound.ENTITY_EVOKER_HURT, 1.0f, 0.8f)
        }
    }

    @EventHandler
    fun onWarlockDeath(event: EntityDeathEvent) {
        val entity = event.entity
        if (entity == this._entity) {
            if (!this.revived) {
                val maxHp: AttributeInstance? = _entity.getAttribute(Attribute.MAX_HEALTH)
                _entity.heal(maxHp!!.value)
                return
            }
            entity.location.world.playSound(entity.location, Sound.ENTITY_EVOKER_DEATH, 1.0f, 0.8f)
        }
    }

    companion object {
        const val SPAWN_MOB_FLAG : String = "illager_warlock"
        val HOOD_KEY = NamespacedKey(SMPRPG.plugin, "illager_warlock_hood")
    }
}