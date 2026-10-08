//package xyz.devvydont.smprpg.block.behaviors
//
//import net.minecraft.server.level.ServerLevel
//import net.minecraft.world.entity.player.Player
//import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks
//import net.momirealms.craftengine.bukkit.api.CraftEngineItems
//import net.momirealms.craftengine.bukkit.block.behavior.CropBlockBehavior
//import net.momirealms.craftengine.core.block.BlockDefinition
//import net.momirealms.craftengine.core.block.CustomBlock
//import net.momirealms.craftengine.core.block.ImmutableBlockState
//import net.momirealms.craftengine.core.block.behavior.BlockBehavior
//import net.momirealms.craftengine.core.block.behavior.BlockBehaviorFactory
//import net.momirealms.craftengine.core.block.properties.Property
//import net.momirealms.craftengine.core.block.property.IntegerProperty
//import net.momirealms.craftengine.core.entity.player.InteractionResult
//import net.momirealms.craftengine.core.item.CustomItem
//import net.momirealms.craftengine.core.item.ItemDefinition
//import net.momirealms.craftengine.core.plugin.context.number.NumberProvider
//import net.momirealms.craftengine.core.plugin.context.number.NumberProviders
//import net.momirealms.craftengine.core.sound.SoundData
//import net.momirealms.craftengine.core.util.MiscUtils
//import net.momirealms.craftengine.core.util.ResourceConfigUtils
//import net.momirealms.craftengine.core.world.Vec3d
//import net.momirealms.craftengine.core.world.context.BlockPlaceContext
//import net.momirealms.craftengine.core.world.context.UseOnContext
//import net.momirealms.craftengine.libraries.nbt.CompoundTag
//import net.momirealms.craftengine.libraries.nbt.IntTag
//import net.momirealms.craftengine.libraries.nbt.Tag
//import org.bukkit.Location
//import org.bukkit.craftbukkit.CraftWorld
//import org.bukkit.event.player.PlayerHarvestBlockEvent
//import org.bukkit.inventory.EquipmentSlot
//import org.bukkit.inventory.ItemStack
//import java.util.*
//import java.util.function.Function
//import net.momirealms.craftengine.core.util.Key as CEKey
//
//class BerryBushBlockBehavior(blockDefinition: BlockDefinition,
//                             val ageProperty: IntegerProperty,
//                             val growSpeed: Float,
//                             val minGrowLight: Int,
//                             val boneMealTarget: Boolean,
//                             val boneMealBonus: NumberProvider,
//                             val firstMatureAge: Int,
//                             val harvestItem: ItemDefinition,
//                             val pickSound: SoundData?): CropBlockBehavior(customBlock, ageProperty, growSpeed, minGrowLight, boneMealTarget, boneMealBonus) {
//
//    override fun useWithoutItem(context: UseOnContext, state: ImmutableBlockState): InteractionResult {
//        val age = state.customBlockState()?.getProperty<Int>("age")
//        if (age != null) {
//            // Hasn't reached the first age of maturity yet.
//            if (age < firstMatureAge) return InteractionResult.PASS
//
//            // Otherwise, we will send out a harvest block event and reset our block back to the age prior to first maturity.
//            val ageTag = CompoundTag(mutableMapOf(Pair("age", IntTag(firstMatureAge - 1) as Tag)))
//            val newState = state.with(ageTag)
//            if (pickSound != null)
//                context.world.playBlockSound(Vec3d.atCenterOf(context.clickedPos), pickSound)
//
//            val itemHarvested = harvestItem.buildItemStack()
//            itemHarvested.amount = (age - firstMatureAge) + 1
//            val world = (context.level.serverWorld() as ServerLevel).level.world
//            val blockLoc = Location(world, context.clickedPos.x.toDouble(), context.clickedPos.y.toDouble(), context.clickedPos.z.toDouble())
//            val blockHarvested = world.getBlockAt(blockLoc)
//            world.dropItemNaturally(blockLoc, itemHarvested)
//            CraftEngineBlocks.place(blockLoc, newState, false)
//            val nms_Player = context.player?.serverPlayer() as Player
//            PlayerHarvestBlockEvent(nms_Player.bukkitEntity as org.bukkit.entity.Player, blockHarvested, EquipmentSlot.HAND, listOf(itemHarvested)).callEvent()
//            return InteractionResult.SUCCESS
//        }
//        return InteractionResult.PASS
//    }
//
//    override fun useOnBlock(context: UseOnContext?, state: ImmutableBlockState?): InteractionResult? {
//        val superRet = super.useOnBlock(context, state)
//        return InteractionResult.TRY_EMPTY_HAND
//    }
//
//    companion object {
//        val FACTORY = Factory()
//
//        class Factory : BlockBehaviorFactory<BlockBehavior> {
//            override fun create(block: CustomBlock, arguments: Map<String, Any>): BerryBushBlockBehavior {
//                val ageProperty = ResourceConfigUtils.requireNonNullOrThrow(
//                    block.getProperty("age"),
//                    "warning.config.block.behavior.crop.missing_age"
//                ) as Property<Int>
//                val minGrowLight = ResourceConfigUtils.getAsInt(arguments.getOrDefault("light-requirement", 9), "light-requirement")
//                val growSpeed = ResourceConfigUtils.getAsFloat(arguments.getOrDefault("grow-speed", 0.125f), "grow-speed")
//                val isBoneMealTarget = ResourceConfigUtils.getAsBoolean(
//                    arguments.getOrDefault("is-bone-meal-target", true),
//                    "is-bone-meal-target"
//                )
//                val boneMealAgeBonus = NumberProviders.fromObject(arguments.getOrDefault("bone-meal-age-bonus", 1))
//
//                val firstMatureAge : Int = arguments["first-mature-age"] as Int
//                val harvestItemVal : String = arguments["harvested-item"] as String
//                val harvestItem : CustomItem<ItemStack> = CraftEngineItems.byId(CEKey.of(harvestItemVal)) as CustomItem<ItemStack>
//                val sounds = MiscUtils.castToMap(arguments["sounds"], true)
//                var pickSound : SoundData? = null
//                if (sounds != null)
//                    pickSound = Optional.ofNullable(sounds["harvest-sound"]).map<SoundData?>(Function { obj ->
//                        SoundData.create(
//                            obj,
//                            SoundData.SoundValue.FIXED_1,
//                            SoundData.SoundValue.ranged(0.9f, 1f)
//                        )
//                    }).orElse(null)
//                return BerryBushBlockBehavior(block, ageProperty, growSpeed, minGrowLight, isBoneMealTarget, boneMealAgeBonus, firstMatureAge, harvestItem, pickSound)
//            }
//        }
//    }
//}