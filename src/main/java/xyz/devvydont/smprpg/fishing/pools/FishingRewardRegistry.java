package xyz.devvydont.smprpg.fishing.pools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.fishing.SeaBear;
import xyz.devvydont.smprpg.entity.fishing.Shark;
import xyz.devvydont.smprpg.entity.fishing.SnappingTurtle;
import xyz.devvydont.smprpg.fishing.loot.FishingLootBase;
import xyz.devvydont.smprpg.fishing.loot.FishingLootType;
import xyz.devvydont.smprpg.fishing.loot.ItemStackFishingLoot;
import xyz.devvydont.smprpg.fishing.loot.SeaCreatureFishingLoot;
import xyz.devvydont.smprpg.fishing.loot.requirements.BiomeChoiceRequirement;
import xyz.devvydont.smprpg.fishing.loot.requirements.FishingLootRequirement;
import xyz.devvydont.smprpg.fishing.utils.TemperatureReading;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.interfaces.IFishingRod;

import java.util.Collection;

/**
 * Statically contains all the fishing rewards that are possible to retrieve in the plugin.
 * Rewards are not meant to be dynamic, our plugin just needs some point to register what's available.
 * If you want something to be available in the fishing pool, add it here. Just make sure to include
 * any requirements you want to loot to have!
 */
public class FishingRewardRegistry {

    private static final Multimap<FishingLootType, FishingLootBase> REGISTRY;

    // Initializes the registry.
    static {

        ImmutableMultimap.Builder<FishingLootType, FishingLootBase> builder = ImmutableMultimap.builder();

        // Initialize the fish pool.
        builder.putAll(FishingLootType.FISH,

                // Start with fish that can be fished up anywhere. Introduction to the system essentially.
                new ItemStackFishingLoot.Builder(CustomItemType.COD)
                        .withWeight(10)
                        .withMinecraftExperience(1)
                        .withSkillExperience(15)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.SALMON)
                        .withWeight(7)
                        .withMinecraftExperience(5)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.quality(15))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.PUFFERFISH)
                        .withWeight(3)
                        .withMinecraftExperience(10)
                        .withSkillExperience(25)
                        .withRequirement(FishingLootRequirement.quality(40))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CLOWNFISH)
                        .withMinecraftExperience(30)
                        .withSkillExperience(75)
                        .withRequirement(FishingLootRequirement.quality(100))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),


                new ItemStackFishingLoot.Builder(CustomItemType.BLISTERFISH)
                        .withMinecraftExperience(30)
                        .withSkillExperience(15)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.VOIDFIN)
                        .withMinecraftExperience(30)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build()
        );

        // Initialize the treasure pool.
        builder.putAll(FishingLootType.TREASURE,

                new ItemStackFishingLoot.Builder(Material.TURTLE_SCUTE)
                        .withMinimumAmount(1)
                        .withMaximumAmount(3)
                        .withMinecraftExperience(50)
                        .withSkillExperience(100)
                        .withWeight(2)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.DIAMOND)
                        .withMinecraftExperience(10)
                        .withSkillExperience(250)
                        .withWeight(2)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.NAUTILUS_SHELL)
                        .withMinecraftExperience(50)
                        .withSkillExperience(500)
                        .withRequirement(FishingLootRequirement.quality(100))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withWeight(2)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.HEART_OF_THE_SEA)
                        .withRequirement(FishingLootRequirement.quality(100))
                        .withMinecraftExperience(50)
                        .withSkillExperience(750)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.EXPERIENCE_BOTTLE)
                        .withSkillExperience(500)
                        .withMinecraftExperience(50)
                        .withRequirement(FishingLootRequirement.quality(10))
                        .withWeight(2)
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.LARGE_EXPERIENCE_BOTTLE)
                        .withSkillExperience(750)
                        .withMinecraftExperience(75)
                        .withRequirement(FishingLootRequirement.quality(75))
                        .build(),


                new ItemStackFishingLoot.Builder(CustomItemType.HEFTY_EXPERIENCE_BOTTLE)
                        .withSkillExperience(1000)
                        .withMinecraftExperience(100)
                        .withRequirement(FishingLootRequirement.quality(200))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.GIGANTIC_EXPERIENCE_BOTTLE)
                        .withSkillExperience(1250)
                        .withMinecraftExperience(125)
                        .withRequirement(FishingLootRequirement.quality(500))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.COLOSSAL_EXPERIENCE_BOTTLE)
                        .withSkillExperience(1500)
                        .withMinecraftExperience(150)
                        .withRequirement(FishingLootRequirement.quality(1000))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CAVIAR)
                        .withRequirement(FishingLootRequirement.quality(250))
                        .withMinecraftExperience(75)
                        .withSkillExperience(1000)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.NETHERITE_INGOT)
                        .withMinecraftExperience(50)
                        .withSkillExperience(200)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.END_CRYSTAL)
                        .withMinecraftExperience(50)
                        .withSkillExperience(200)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build()
        );

        // Initialize the sea creature pool.
        builder.putAll(FishingLootType.CREATURE,

                // The minnow can always be caught, no matter what. Can't leave a pool potentially empty.
                new SeaCreatureFishingLoot.Builder(CustomEntityType.MINNOW)
                        .withMinecraftExperience(10)
                        .withSkillExperience(100)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new SeaCreatureFishingLoot.Builder(CustomEntityType.SNAPPING_TURTLE)
                        .withMinecraftExperience(25)
                        .withSkillExperience(200)
                        .withRequirement(FishingLootRequirement.quality(SnappingTurtle.REQUIREMENT))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new SeaCreatureFishingLoot.Builder(CustomEntityType.SEA_BEAR)
                        .withMinecraftExperience(50)
                        .withSkillExperience(500)
                        .withRequirement(FishingLootRequirement.quality(SeaBear.REQUIREMENT))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new SeaCreatureFishingLoot.Builder(CustomEntityType.SHARK)
                        .withMinecraftExperience(75)
                        .withSkillExperience(600)
                        .withRequirement(FishingLootRequirement.quality(Shark.RATING_REQUIREMENT))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new SeaCreatureFishingLoot.Builder(CustomEntityType.CINDERLING)
                        .withMinecraftExperience(50)
                        .withSkillExperience(250)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new SeaCreatureFishingLoot.Builder(CustomEntityType.ECHO_RAY)
                        .withMinecraftExperience(50)
                        .withSkillExperience(500)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build()
        );

        // Initialize the junk pool.
        builder.putAll(FishingLootType.JUNK,

                new ItemStackFishingLoot.Builder(Material.LILY_PAD)
                        .withWeight(10)
                        .withMaximumAmount(5)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.BOWL)
                        .withWeight(3)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.LEATHER)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.ROTTEN_FLESH)
                        .withWeight(3)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.STICK)
                        .withWeight(3)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.STRING)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.BONE)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.COAL)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.BLAZE_POWDER)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.TRIPWIRE_HOOK)
                        .withWeight(2)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .build(),

                new ItemStackFishingLoot.Builder(Material.INK_SAC)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.PREMIUM_MAGMA_CREAM)
                        .withMinecraftExperience(3)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.GOLD_BLOCK)
                        .withMinecraftExperience(3)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.CHORUS_FLOWER)
                        .withMinecraftExperience(5)
                        .withSkillExperience(30)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(Material.ENDER_PEARL)
                        .withMinecraftExperience(5)
                        .withSkillExperience(30)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build()
        );

        REGISTRY = builder.build();
    }

    public static Multimap<FishingLootType, FishingLootBase> getRegisteredRewards() {
        return REGISTRY;
    }

    public static Collection<FishingLootBase> getRegisteredRewards(FishingLootType type) {
        return REGISTRY.get(type);
    }

}
