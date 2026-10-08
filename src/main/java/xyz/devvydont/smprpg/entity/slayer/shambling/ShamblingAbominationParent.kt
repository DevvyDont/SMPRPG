package xyz.devvydont.smprpg.entity.slayer.shambling

import net.kyori.adventure.key.Key
import org.bukkit.Material
import org.bukkit.damage.DamageType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityTransformEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.persistence.PersistentDataType
import xyz.devvydont.smprpg.entity.CustomEntityType
import xyz.devvydont.smprpg.entity.MobType
import xyz.devvydont.smprpg.entity.slayer.SlayerBossInstance
import xyz.devvydont.smprpg.events.CustomEntityDamageByEntityEvent
import xyz.devvydont.smprpg.items.CustomItemType
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured
import xyz.devvydont.smprpg.services.EnchantmentService
import xyz.devvydont.smprpg.services.ItemService.Companion.generate
import xyz.devvydont.smprpg.util.persistence.KeyStore

open class ShamblingAbominationParent
/**
 * An unsafe constructor to use to allow dynamic creation of custom entities.
 * This is specifically used as a casting hack for the CustomEntityType enum in order to dynamically create
 * entities.
 *
 * @param entity     The entity that should map the T type parameter.
 * @param entityType The entity type.
 */
    (entity: LivingEntity?, val entityType: CustomEntityType?) : SlayerBossInstance<Zombie>(entity as Zombie?, entityType) {

    open var attackCooldown = 10
    open var enrageThreshold = 0.5
    open var explosionDamage = 500.0

    override fun setup() {
        mobTypes.add(MobType.BOSS);
        mobTypes.add(MobType.UNDEAD);

        super.setup()

        // Setup equipment for boss
        val equipment = _entity!!.equipment
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.playerProfile = ICustomTextured.getProfile(HEAD_TEXTURE)
        head.setItemMeta(meta)
        equipment?.setHelmet(head)

        equipment?.setChestplate(generate(CustomItemType.SHAMBLING_CHESTPLATE))

        val leggings = generate(CustomItemType.SHAMBLING_LEGGINGS)
        equipment?.setLeggings(leggings)

        val boots = generate(CustomItemType.SHAMBLING_BOOTS)
        equipment?.setBoots(boots)

        equipment?.itemInMainHandDropChance = 0f
        equipment?.itemInOffHandDropChance = 0f
        equipment?.helmetDropChance = 0f
        equipment?.chestplateDropChance = 0f
        equipment?.leggingsDropChance = 0f
        equipment?.bootsDropChance = 0f

        // Shambling Abominations can also spawn other quests.
        // This encourages collaborative slayer gameplay.
        _entity.getPersistentDataContainer()
            .set(KeyStore.SLAYER_SPAWN_TYPE, PersistentDataType.STRING, SPAWN_MOB_FLAG)
    }

    override fun getSkillExperienceMultiplier(): Double {
        return 10.0
    }

    /*
     * 5 min
     */
    override fun getTimeLimit(): Long {
        return 60L * 5L
    }

    override fun getLevel(): Int {
        return when (entityType) {
            CustomEntityType.SHAMBLING_ABOMINATION_2 -> 20
            CustomEntityType.SHAMBLING_ABOMINATION_3 -> 30
            CustomEntityType.SHAMBLING_ABOMINATION_4 -> 40
            CustomEntityType.SHAMBLING_ABOMINATION_5 -> 50
            else -> 10
        }
    }

    override fun getMaxHp(): Double {
        return when (entityType) {
            CustomEntityType.SHAMBLING_ABOMINATION_2 -> 25_000.0
            CustomEntityType.SHAMBLING_ABOMINATION_3 -> 300_000.0
            CustomEntityType.SHAMBLING_ABOMINATION_4 -> 1_500_000.0
            CustomEntityType.SHAMBLING_ABOMINATION_5 -> 10_000_000.0
            else -> 1_000.0
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onTransform(event : EntityTransformEvent) {
        if (event.entity == this.entity)
            event.isCancelled = true
    }

    companion object {
        const val HEAD_TEXTURE : String = "http://textures.minecraft.net/texture/1fc0184473fe882d2895ce7cbc8197bd40ff70bf10d3745de97b6c2a9c5fc78f"
        const val HEAD_TEXTURE_ANGRY : String = "http://textures.minecraft.net/texture/ba7bb34471508b4b20cb35c37a1dd6e8afa7f2d5388822138837caf59c2195d8"
        val enragedAssetId : Key = Key.key("shambling_boss_enrage")

        val LOOT_SMITE_SCROLL : ItemStack = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.SMITE)

        const val SPAWN_MOB_FLAG : String = "shambling_abomination"
    }

    /**
     * Event that fixes damage calculation for the implosion ability.
     */
    @EventHandler(priority = EventPriority.HIGH)  // HIGH priority -- right before difficulty adjustment, but after base damage adjustment.
    fun onImplodeDamage(event : CustomEntityDamageByEntityEvent) {
        val source = event.originalEvent.damageSource
        if (entity == source.causingEntity) {
            if (source.damageType == DamageType.EXPLOSION) {
                event.setDamage(explosionDamage)  // Completely overrides any damage modifiers present
            }
        }
    }
}