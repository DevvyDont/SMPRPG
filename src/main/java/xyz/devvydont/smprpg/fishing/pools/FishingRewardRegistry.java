package xyz.devvydont.smprpg.fishing.pools;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.block.Biome;
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

    public static final int COMMON_WEIGHT = 100;
    public static final int UNCOMMON_WEIGHT = 35;
    public static final int RARE_WEIGHT = 15;
    public static final int EPIC_WEIGHT = 4;
    public static final int LEGENDARY_WEIGHT = 1;

    private static final Multimap<FishingLootType, FishingLootBase> REGISTRY;

    // Initializes the registry.
    static {

        ImmutableMultimap.Builder<FishingLootType, FishingLootBase> builder = ImmutableMultimap.builder();

        // Initialize the fish pool.
        builder.putAll(FishingLootType.FISH,

                // Common overworld fish
                new ItemStackFishingLoot.Builder(CustomItemType.COD)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(1)
                        .withSkillExperience(10)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.GUPPY)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(2)
                        .withSkillExperience(15)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CARP)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(1)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.SALMON)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(3)
                        .withSkillExperience(50)
                        .withRequirement(FishingLootRequirement.quality(50))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CLOWNFISH)
                        .withMinecraftExperience(15)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withSkillExperience(300)
                        .withRequirement(FishingLootRequirement.quality(200))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.PUFFERFISH)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(10)
                        .withSkillExperience(200)
                        .withRequirement(FishingLootRequirement.quality(100))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BASS)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(10)
                        .withSkillExperience(250)
                        .withRequirement(FishingLootRequirement.quality(120))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.TEMPERATE))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CATFISH)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(15)
                        .withSkillExperience(250)
                        .withRequirement(FishingLootRequirement.quality(75))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.SWAMP))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.SNAPPER)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(10)
                        .withSkillExperience(300)
                        .withRequirement(FishingLootRequirement.quality(150))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.PIKE)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(25)
                        .withSkillExperience(600)
                        .withRequirement(FishingLootRequirement.quality(200))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.RIVER))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.STURGEON)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(35)
                        .withSkillExperience(1000)
                        .withRequirement(FishingLootRequirement.quality(400))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.COLD))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BLUE_TANG)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(30)
                        .withSkillExperience(900)
                        .withRequirement(FishingLootRequirement.quality(300))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BARRACUDA)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(50)
                        .withSkillExperience(1500)
                        .withRequirement(FishingLootRequirement.quality(500))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.YELLOWFIN_TUNA)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(60)
                        .withSkillExperience(2500)
                        .withRequirement(FishingLootRequirement.quality(600))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.GOLIATH_GROUPER)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(100)
                        .withSkillExperience(5000)
                        .withRequirement(FishingLootRequirement.quality(700))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.OCEAN))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.LIONFISH)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(100)
                        .withSkillExperience(2_000)
                        .withRequirement(FishingLootRequirement.quality(400))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.LEAFY_SEADRAGON)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(30)
                        .withSkillExperience(1_000)
                        .withRequirement(FishingLootRequirement.quality(300))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BLUE_MARLIN)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(250)
                        .withSkillExperience(10_000)
                        .withRequirement(FishingLootRequirement.quality(800))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.temperature(TemperatureReading.WARM))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.FANGTOOTH)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(250)
                        .withSkillExperience(10_000)
                        .withRequirement(FishingLootRequirement.quality(850))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biome(Biome.DEEP_OCEAN))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.DEEP_SEA_ANGLERFISH)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(250)
                        .withSkillExperience(10_000)
                        .withRequirement(FishingLootRequirement.quality(900))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biome(Biome.DEEP_DARK))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BLISTERFISH)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(30)
                        .withSkillExperience(15)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.IMPLING)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(45)
                        .withSkillExperience(25)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.CRIMSONFISH)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(80)
                        .withSkillExperience(325)
                        .withRequirement(FishingLootRequirement.quality(100))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .withRequirement(FishingLootRequirement.biome(Biome.CRIMSON_FOREST))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.SOUL_SCALE)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(100)
                        .withSkillExperience(3000)
                        .withRequirement(FishingLootRequirement.quality(220))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .withRequirement(FishingLootRequirement.biome(Biome.SOUL_SAND_VALLEY))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BONE_MAW)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(125)
                        .withSkillExperience(3250)
                        .withRequirement(FishingLootRequirement.quality(220))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .withRequirement(FishingLootRequirement.biome(Biome.BASALT_DELTAS))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.FLAREFIN)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(250)
                        .withSkillExperience(5500)
                        .withRequirement(FishingLootRequirement.quality(575))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.GHOST_FISH)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(300)
                        .withSkillExperience(6750)
                        .withRequirement(FishingLootRequirement.quality(600))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .withRequirement(FishingLootRequirement.biome(Biome.SOUL_SAND_VALLEY))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.DEVIL_RAY)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(750)
                        .withSkillExperience(15000)
                        .withRequirement(FishingLootRequirement.quality(900))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.LAVA))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.VOIDFIN)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(30)
                        .withSkillExperience(20)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.ORBLING)
                        .withWeight(COMMON_WEIGHT)
                        .withMinecraftExperience(45)
                        .withSkillExperience(30)
                        .withRequirement(FishingLootRequirement.quality(200))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.WARPER)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(125)
                        .withSkillExperience(600)
                        .withRequirement(FishingLootRequirement.quality(300))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.BLOBFISH)
                        .withWeight(UNCOMMON_WEIGHT)
                        .withMinecraftExperience(175)
                        .withSkillExperience(750)
                        .withRequirement(FishingLootRequirement.quality(600))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.GOBLIN_SHARK)
                        .withWeight(RARE_WEIGHT)
                        .withMinecraftExperience(300)
                        .withSkillExperience(5000)
                        .withRequirement(FishingLootRequirement.quality(600))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.STARSURFER)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(450)
                        .withSkillExperience(8000)
                        .withRequirement(FishingLootRequirement.quality(700))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.ABYSSAL_SQUID)
                        .withWeight(EPIC_WEIGHT)
                        .withMinecraftExperience(550)
                        .withSkillExperience(8500)
                        .withRequirement(FishingLootRequirement.quality(750))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.TWILIGHT_ANGLERFISH)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(1250)
                        .withSkillExperience(17500)
                        .withRequirement(FishingLootRequirement.quality(800))
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.VOID))
                        .build(),

                new ItemStackFishingLoot.Builder(CustomItemType.COSMIC_CUTTLEFISH)
                        .withWeight(LEGENDARY_WEIGHT)
                        .withMinecraftExperience(1500)
                        .withSkillExperience(22500)
                        .withRequirement(FishingLootRequirement.quality(900))
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

                new ItemStackFishingLoot.Builder(CustomItemType.MOSSY_SKULL)
                        .withRequirement(FishingLootRequirement.quality(200))
                        .withMinecraftExperience(50)
                        .withSkillExperience(1000)
                        .withRequirement(FishingLootRequirement.rod(IFishingRod.FishingFlag.NORMAL))
                        .withRequirement(FishingLootRequirement.biomes(BiomeChoiceRequirement.BiomeGroup.SWAMP))
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
