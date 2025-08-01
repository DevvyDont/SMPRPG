package xyz.devvydont.smprpg.items;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.blueprints.boss.DiamondToolRod;
import xyz.devvydont.smprpg.items.blueprints.boss.InfernoArrow;
import xyz.devvydont.smprpg.items.blueprints.boss.NeptunesConch;
import xyz.devvydont.smprpg.items.blueprints.charms.LuckyCharm;
import xyz.devvydont.smprpg.items.blueprints.charms.SpeedCharm;
import xyz.devvydont.smprpg.items.blueprints.charms.StrengthCharm;
import xyz.devvydont.smprpg.items.blueprints.debug.*;
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin;
import xyz.devvydont.smprpg.items.blueprints.equipment.*;
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint;
import xyz.devvydont.smprpg.items.blueprints.food.*;
import xyz.devvydont.smprpg.items.blueprints.misc.DeathCertificate;
import xyz.devvydont.smprpg.items.blueprints.misc.MossySkull;
import xyz.devvydont.smprpg.items.blueprints.potion.ExperienceBottle;
import xyz.devvydont.smprpg.items.blueprints.reforge.VoidRelic;
import xyz.devvydont.smprpg.items.blueprints.resources.EmptyBlueprint;
import xyz.devvydont.smprpg.items.blueprints.resources.SellableResource;
import xyz.devvydont.smprpg.items.blueprints.resources.crafting.DraconicCrystal;
import xyz.devvydont.smprpg.items.blueprints.resources.crafting.WarpCatalyst;
import xyz.devvydont.smprpg.items.blueprints.resources.farming.MelonBlueprintFamily;
import xyz.devvydont.smprpg.items.blueprints.resources.farming.SugarCaneBlueprintFamily;
import xyz.devvydont.smprpg.items.blueprints.resources.mining.*;
import xyz.devvydont.smprpg.items.blueprints.resources.mob.*;
import xyz.devvydont.smprpg.items.blueprints.reusable.SimpleTexturedItem;
import xyz.devvydont.smprpg.items.blueprints.sets.amethyst.AmethystBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.amethyst.AmethystChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.amethyst.AmethystHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.amethyst.AmethystLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.araxys.*;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.cobblestone.CobblestoneBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.cobblestone.CobblestoneChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.cobblestone.CobblestoneHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.cobblestone.CobblestoneLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.copper.*;
import xyz.devvydont.smprpg.items.blueprints.sets.diamond.DiamondBow;
import xyz.devvydont.smprpg.items.blueprints.sets.elderflame.*;
import xyz.devvydont.smprpg.items.blueprints.sets.emberclad.*;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.exiled.ExiledAxe;
import xyz.devvydont.smprpg.items.blueprints.sets.exiled.ExiledCrossbow;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.*;
import xyz.devvydont.smprpg.items.blueprints.sets.forsaken.*;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.inferno.*;
import xyz.devvydont.smprpg.items.blueprints.sets.infinity.*;
import xyz.devvydont.smprpg.items.blueprints.sets.iron.IronBow;
import xyz.devvydont.smprpg.items.blueprints.sets.mystbloom.*;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.neofrontier.NeoFrontierBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.neofrontier.NeoFrontierChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.neofrontier.NeoFrontierHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.neofrontier.NeoFrontierLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.neptune.*;
import xyz.devvydont.smprpg.items.blueprints.sets.netherite.NetheriteBow;
import xyz.devvydont.smprpg.items.blueprints.sets.phantom.EvoriDreamwings;
import xyz.devvydont.smprpg.items.blueprints.sets.phantom.PhantomWings;
import xyz.devvydont.smprpg.items.blueprints.sets.prelude.PreludeBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.prelude.PreludeChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.prelude.PreludeHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.prelude.PreludeLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.protocol.ProtocolArmorSet;
import xyz.devvydont.smprpg.items.blueprints.sets.protocol.ProtocolHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.quartz.QuartzBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.quartz.QuartzChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.quartz.QuartzHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.quartz.QuartzLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.radiant.RadiantBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.radiant.RadiantChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.radiant.RadiantHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.radiant.RadiantLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.reaver.*;
import xyz.devvydont.smprpg.items.blueprints.sets.redstone.RedstoneBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.redstone.RedstoneChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.redstone.RedstoneHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.redstone.RedstoneLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.singularity.SingularityBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.singularity.SingularityChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.singularity.SingularityHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.singularity.SingularityLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.slimy.SlimyBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.slimy.SlimyChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.slimy.SlimyHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.slimy.SlimyLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.smite.SmiteBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.smite.SmiteChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.smite.SmiteHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.smite.SmiteLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.snowfall.SnowfallBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.snowfall.SnowfallChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.snowfall.SnowfallHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.snowfall.SnowfallLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.special.MagmaHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.special.SpaceHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.special.SquidHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.undead.UndeadBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.undead.UndeadChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.undead.UndeadHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.undead.UndeadLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.unstable.UnstableBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.unstable.UnstableChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.unstable.UnstableHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.unstable.UnstableLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.valiant.ValiantBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.valiant.ValiantChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.valiant.ValiantHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.valiant.ValiantLeggings;
import xyz.devvydont.smprpg.items.blueprints.storage.*;
import xyz.devvydont.smprpg.items.interfaces.ICustomTextured;

public enum CustomItemType {

    // COINS
    COPPER_COIN(   "Copper Coin",    Material.FIREWORK_STAR, CustomItemCoin.class),                                // 1 coin
    SILVER_COIN(   "Silver Coin",    Material.FIREWORK_STAR, ItemRarity.UNCOMMON,     CustomItemCoin.class),       // 100 coins
    GOLD_COIN(     "Gold Coin",      Material.FIREWORK_STAR, ItemRarity.RARE,         CustomItemCoin.class),       // 10K coins
    PLATINUM_COIN( "Platinum Coin",  Material.FIREWORK_STAR, ItemRarity.EPIC,         CustomItemCoin.class),       // 1M coins
//    EMERALD_COIN(  "Emerald Coin",   Material.FIREWORK_STAR, ItemRarity.LEGENDARY,    CustomItemCoin.class),       // 10k coins
//    AMETHYST_COIN( "Amethyst Coin",  Material.FIREWORK_STAR, ItemRarity.MYTHIC,       CustomItemCoin.class),       // 100k coins
    ENCHANTED_COIN("Enchanted Coin", Material.FIREWORK_STAR, ItemRarity.LEGENDARY, true, CustomItemCoin.class), // 100M coins

    // NEO_FRONTIER SET
    NEO_FRONTIER_HELMET("Neo Frontier Helmet",         Material.IRON_HELMET,        NeoFrontierHelmet.class),
    NEO_FRONTIER_CHESTPLATE("Neo Frontier Chestplate", Material.LEATHER_CHESTPLATE, NeoFrontierChestplate.class),
    NEO_FRONTIER_LEGGINGS("Neo Frontier Leggings",     Material.IRON_LEGGINGS,      NeoFrontierLeggings.class),
    NEO_FRONTIER_BOOTS("Neo Frontier Boots",           Material.LEATHER_BOOTS,      NeoFrontierBoots.class),

    // COPPER SET
    COPPER_PICKAXE("Copper Pickaxe",  Material.WOODEN_PICKAXE, CopperPickaxe.class),
    COPPER_AXE(    "Copper Axe",      Material.WOODEN_AXE,     CopperAxe.class),
    COPPER_HOE(    "Copper Hoe",      Material.WOODEN_HOE,     CopperHoe.class),
    COPPER_SHOVEL( "Copper Shovel",   Material.WOODEN_SHOVEL,  CopperShovel.class),
    COPPER_SWORD(  "Copper Sword",    Material.WOODEN_SWORD,   CopperSword.class),
    COPPER_BOW(    "Copper Bow",      Material.BOW,            CopperBow.class),

    COPPER_HELMET(    "Copper Helmet",     Material.LEATHER_HELMET,     CopperHelmet.class),
    COPPER_CHESTPLATE("Copper Chestplate", Material.LEATHER_CHESTPLATE, CopperChestplate.class),
    COPPER_LEGGINGS(  "Copper Leggings",   Material.LEATHER_LEGGINGS,   CopperLeggings.class),
    COPPER_BOOTS(     "Copper Boots",      Material.LEATHER_BOOTS,      CopperBoots.class),

    // COBBLESTONE SET
    COBBLESTONE_HELMET("Cobblestone Helmet",         Material.LEATHER_HELMET,        ItemRarity.UNCOMMON, CobblestoneHelmet.class),
    COBBLESTONE_CHESTPLATE("Cobblestone Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.UNCOMMON, CobblestoneChestplate.class),
    COBBLESTONE_LEGGINGS("Cobblestone Leggings",     Material.LEATHER_LEGGINGS,   ItemRarity.UNCOMMON, CobblestoneLeggings.class),
    COBBLESTONE_BOOTS("Cobblestone Boots",           Material.LEATHER_BOOTS,      ItemRarity.UNCOMMON, CobblestoneBoots.class),

    // SAKURA SET
    SAKURA_HELMET("Sakura Helmet",         Material.CHERRY_LEAVES,      ItemRarity.UNCOMMON, SakuraHelmet.class),
    SAKURA_CHESTPLATE("Sakura Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.UNCOMMON, SakuraChestplate.class),
    SAKURA_LEGGINGS("Sakura Leggings",     Material.IRON_LEGGINGS,      ItemRarity.UNCOMMON, SakuraLeggings.class),
    SAKURA_BOOTS("Sakura Boots",           Material.LEATHER_BOOTS,      ItemRarity.UNCOMMON, SakuraBoots.class),

    // UNDEAD SET
    UNDEAD_HELMET(    "Undead Helmet",         Material.LEATHER_HELMET,     UndeadHelmet.class),
    UNDEAD_CHESTPLATE("Undead Chestplate",     Material.LEATHER_CHESTPLATE, UndeadChestplate.class),
    UNDEAD_LEGGINGS(  "Undead Leggings",       Material.LEATHER_LEGGINGS,   UndeadLeggings.class),
    UNDEAD_BOOTS(     "Undead Boots",          Material.LEATHER_BOOTS,      UndeadBoots.class),

    // BONE SET
    BONE_HELMET(    "Bone Helmet",         Material.CHAINMAIL_HELMET,   BoneHelmet.class),
    BONE_CHESTPLATE("Bone Chestplate",     Material.LEATHER_CHESTPLATE, BoneChestplate.class),
    BONE_LEGGINGS(  "Bone Leggings",       Material.CHAINMAIL_LEGGINGS, BoneLeggings.class),
    BONE_BOOTS(     "Bone Boots",          Material.LEATHER_BOOTS,      BoneBoots.class),

    // AMETHYST
    AMETHYST_HELMET(    "Amethyst Helmet",     Material.IRON_HELMET   ,  ItemRarity.RARE, AmethystHelmet.class),
    AMETHYST_CHESTPLATE("Amethyst Chestplate", Material.IRON_CHESTPLATE, ItemRarity.RARE, AmethystChestplate.class),
    AMETHYST_LEGGINGS(  "Amethyst Leggings",   Material.IRON_LEGGINGS,   ItemRarity.RARE, AmethystLeggings.class),
    AMETHYST_BOOTS(     "Amethyst Boots",      Material.IRON_BOOTS,      ItemRarity.RARE, AmethystBoots.class),

    // SMITE SET
    SMITE_HELMET(    "Smite Helmet",         Material.CHAINMAIL_HELMET,     ItemRarity.RARE, SmiteHelmet.class),
    SMITE_CHESTPLATE("Smite Chestplate",     Material.CHAINMAIL_CHESTPLATE, ItemRarity.RARE, SmiteChestplate.class),
    SMITE_LEGGINGS(  "Smite Leggings",       Material.CHAINMAIL_LEGGINGS,   ItemRarity.RARE, SmiteLeggings.class),
    SMITE_BOOTS(     "Smite Boots",          Material.CHAINMAIL_BOOTS,      ItemRarity.RARE, SmiteBoots.class),

    // SLIMY SET
    SLIMY_HELMET("Slimy Helmet",         Material.SLIME_BLOCK   ,     ItemRarity.RARE, SlimyHelmet.class),
    SLIMY_CHESTPLATE("Slimy Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.RARE, SlimyChestplate.class),
    SLIMY_LEGGINGS("Slimy Leggings",     Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, SlimyLeggings.class),
    SLIMY_BOOTS("Slimy Boots",           Material.LEATHER_BOOTS,      ItemRarity.RARE, SlimyBoots.class),

    // REDSTONE
    REDSTONE_HELMET(    "Redstone Helmet",     Material.TARGET,             ItemRarity.RARE, RedstoneHelmet.class),
    REDSTONE_CHESTPLATE("Redstone Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.RARE, RedstoneChestplate.class),
    REDSTONE_LEGGINGS(  "Redstone Leggings",   Material.LEATHER_LEGGINGS,   ItemRarity.RARE, RedstoneLeggings.class),
    REDSTONE_BOOTS(     "Redstone Boots",      Material.LEATHER_BOOTS,      ItemRarity.RARE, RedstoneBoots.class),

    // MYSTBLOOM SET
    MYSTBLOOM_HELMET("Mystbloom Helmet",         Material.LEATHER_HELMET,     ItemRarity.EPIC, MystbloomHelmet.class),
    MYSTBLOOM_CHESTPLATE("Mystbloom Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.EPIC, MystbloomChestplate.class),
    MYSTBLOOM_LEGGINGS("Mystbloom Leggings",     Material.LEATHER_LEGGINGS,   ItemRarity.EPIC, MystbloomLeggings.class),
    MYSTBLOOM_BOOTS("Mystbloom Boots",           Material.LEATHER_BOOTS,      ItemRarity.EPIC, MystbloomBoots.class),
    MYSTBLOOM_KUNAI("Mystbloom Kunai",           Material.IRON_SWORD,         ItemRarity.EPIC, MystbloomKunai.class),

    // LUXE
    LUXE_HELMET("Luxe Helmet",         Material.IRON_HELMET   ,     ItemRarity.EPIC, LuxeHelmet.class),
    LUXE_CHESTPLATE("Luxe Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.EPIC, LuxeChestplate.class),
    LUXE_LEGGINGS("Luxe Leggings",     Material.IRON_LEGGINGS,      ItemRarity.EPIC, LuxeLeggings.class),
    LUXE_BOOTS("Luxe Boots",           Material.LEATHER_BOOTS,      ItemRarity.EPIC, LuxeBoots.class),

    // NEPTUNE SET
    NEPTUNE_HELMET("Neptune Helmet",         Material.ICE,                ItemRarity.LEGENDARY, NeptuneHelmet.class),
    NEPTUNE_CHESTPLATE("Neptune Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.LEGENDARY, NeptuneChestplate.class),
    NEPTUNE_LEGGINGS("Neptune Leggings",     Material.LEATHER_LEGGINGS,   ItemRarity.LEGENDARY, NeptuneLeggings.class),
    NEPTUNE_BOOTS("Neptune Boots",           Material.LEATHER_BOOTS,      ItemRarity.LEGENDARY, NeptuneBoots.class),
    NEPTUNE_TRIDENT("Neptune's Trident",     Material.TRIDENT,            ItemRarity.LEGENDARY, NeptuneTrident.class),
    NEPTUNE_BOW("Neptune's Shortbow",        Material.BOW,                ItemRarity.LEGENDARY, NeptuneBow.class),

    // QUARTZ
    QUARTZ_HELMET("Quartz Helmet",         Material.IRON_HELMET,        ItemRarity.RARE, QuartzHelmet.class),
    QUARTZ_CHESTPLATE("Quartz Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.RARE, QuartzChestplate.class),
    QUARTZ_LEGGINGS("Quartz Leggings",     Material.IRON_LEGGINGS,      ItemRarity.RARE, QuartzLeggings.class),
    QUARTZ_BOOTS("Quartz Boots",           Material.LEATHER_BOOTS,      ItemRarity.RARE, QuartzBoots.class),

    // EMERALD SET
    EMERALD_HELMET(    "Emerald Helmet",         Material.DIAMOND_HELMET,     ItemRarity.RARE, EmeraldHelmet.class),
    EMERALD_CHESTPLATE("Emerald Chestplate",     Material.DIAMOND_CHESTPLATE, ItemRarity.RARE, EmeraldChestplate.class),
    EMERALD_LEGGINGS(  "Emerald Leggings",       Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, EmeraldLeggings.class),
    EMERALD_BOOTS(     "Emerald Boots",          Material.DIAMOND_BOOTS,      ItemRarity.RARE, EmeraldBoots.class),

    // REAVER
    REAVER_HELMET("Reaver Helmet",         Material.BLACK_STAINED_GLASS, ItemRarity.EPIC, ReaverHelmet.class),
    REAVER_CHESTPLATE("Reaver Chestplate", Material.LEATHER_CHESTPLATE,  ItemRarity.EPIC, ReaverChestplate.class),
    REAVER_LEGGINGS("Reaver Leggings",     Material.NETHERITE_LEGGINGS,  ItemRarity.EPIC, ReaverLeggings.class),
    REAVER_BOOTS("Reaver Boots",           Material.LEATHER_BOOTS,       ItemRarity.EPIC, ReaverBoots.class),
    REAVER_KNIFE("Reaver Knife",           Material.NETHERITE_SWORD,     ItemRarity.EPIC, ReaverKnife.class),

    // RADIANT
    RADIANT_HELMET(    "Radiant Helmet",     Material.IRON_HELMET,      ItemRarity.EPIC, RadiantHelmet.class),
    RADIANT_CHESTPLATE("Radiant Chestplate", Material.IRON_CHESTPLATE,  ItemRarity.EPIC, RadiantChestplate.class),
    RADIANT_LEGGINGS(  "Radiant Leggings",   Material.IRON_LEGGINGS,    ItemRarity.EPIC, RadiantLeggings.class),
    RADIANT_BOOTS(     "Radiant Boots",      Material.IRON_BOOTS,       ItemRarity.EPIC, RadiantBoots.class),

    // BEDROCK
    BEDROCK_HELMET("Bedrock Helmet",         Material.BEDROCK,            ItemRarity.EPIC, BedrockHelmet.class),
    BEDROCK_CHESTPLATE("Bedrock Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.EPIC, BedrockChestplate.class),
    BEDROCK_LEGGINGS("Bedrock Leggings",     Material.NETHERITE_LEGGINGS, ItemRarity.EPIC, BedrockLeggings.class),
    BEDROCK_BOOTS("Bedrock Boots",           Material.LEATHER_BOOTS,      ItemRarity.EPIC, BedrockBoots.class),

    // FORSAKEN
    FORSAKEN_HELMET(    "Forsaken Helmet",     Material.NETHERITE_HELMET,    ItemRarity.LEGENDARY, ForsakenHelmet.class),
    FORSAKEN_CHESTPLATE("Forsaken Chestplate", Material.LEATHER_CHESTPLATE,  ItemRarity.LEGENDARY, ForsakenChestplate.class),
    FORSAKEN_LEGGINGS(  "Forsaken Leggings",   Material.NETHERITE_LEGGINGS,  ItemRarity.LEGENDARY, ForsakenLeggings.class),
    FORSAKEN_BOOTS(     "Forsaken Boots",      Material.LEATHER_BOOTS,       ItemRarity.LEGENDARY, ForsakenBoots.class),
    FORSAKEN_CUTLASS(     "Forsaken Cutlass",  Material.NETHERITE_SWORD,     ItemRarity.LEGENDARY, ForsakenCutlass.class),
    DESOLATED_STONE(     "Desolated Stone", ItemRarity.LEGENDARY, DesolatedStone.class),

    // CYRAX
    CYRAX_HELMET(    "Cyrax Helmet",     Material.GOLDEN_HELMET,     ItemRarity.EPIC, CryaxHelmet.class),
    CYRAX_CHESTPLATE("Cyrax Chestplate", Material.GOLDEN_CHESTPLATE, ItemRarity.EPIC, CryaxChestplate.class),
    CYRAX_LEGGINGS(  "Cyrax Leggings",   Material.GOLDEN_LEGGINGS,   ItemRarity.EPIC, CryaxLeggings.class),
    CYRAX_BOOTS(     "Cyrax Boots",      Material.GOLDEN_BOOTS,      ItemRarity.EPIC, CryaxBoots.class),
    CYRAX_BOW(       "Cyrax Bow",        Material.BOW,               ItemRarity.EPIC, CryaxBow.class),

    BOILING_PICKAXE(       "Boiling Pickaxe",        Material.NETHERITE_PICKAXE,        ItemRarity.EPIC, BoilingPickaxe.class),

    OBSIDIAN_TOOL_ROD("Obsidian Tool Rod", Material.STICK, ItemRarity.RARE, true, ObsidianToolRod.class),
    BOILING_INGOT("Boiling Ingot", Material.GOLD_INGOT, ItemRarity.RARE, true, BoilingIngot.class),

    INFERNO_ARROW("Inferno Arrow", Material.SPECTRAL_ARROW, ItemRarity.EPIC, InfernoArrow.class),

    // INFERNO
    INFERNO_HELMET("Inferno Helmet",         Material.GOLDEN_HELMET,     ItemRarity.LEGENDARY, InfernoHelmet.class),
    INFERNO_CHESTPLATE("Inferno Chestplate", Material.GOLDEN_CHESTPLATE, ItemRarity.LEGENDARY, InfernoChestplate.class),
    INFERNO_LEGGINGS("Inferno Leggings",     Material.GOLDEN_LEGGINGS,   ItemRarity.LEGENDARY, InfernoLeggings.class),
    INFERNO_BOOTS("Inferno Boots",           Material.GOLDEN_BOOTS,      ItemRarity.LEGENDARY, InfernoBoots.class),
    INFERNO_SABER("Inferno Saber",           Material.NETHERITE_SWORD,   ItemRarity.LEGENDARY, InfernoSaber.class),
    INFERNO_SHORTBOW("Inferno Shortbow",     Material.BOW,               ItemRarity.LEGENDARY, InfernoShortbow.class),

    SCORCHING_STRING("Scorching String", Material.STRING, ItemRarity.RARE, true, ScorchingString.class),
    INFERNO_RESIDUE("Inferno Residue", Material.ORANGE_DYE, ItemRarity.RARE, true, InfernoResidue.class),
    INFERNO_REMNANT("Inferno Remnant", ItemRarity.EPIC, InfernoRemnant.class),
    SMOLDERING_CORE("Smoldering Core", ItemRarity.LEGENDARY, SmolderingCoreBlueprint.class),

    // UNSTABLE
    UNSTABLE_HELMET(    "Unstable Helmet",     Material.NETHERITE_HELMET,    ItemRarity.RARE, UnstableHelmet.class),
    UNSTABLE_CHESTPLATE("Unstable Chestplate", Material.LEATHER_CHESTPLATE,  ItemRarity.RARE, UnstableChestplate.class),
    UNSTABLE_LEGGINGS(  "Unstable Leggings",   Material.NETHERITE_LEGGINGS,  ItemRarity.RARE, UnstableLeggings.class),
    UNSTABLE_BOOTS(     "Unstable Boots",      Material.LEATHER_BOOTS,       ItemRarity.RARE, UnstableBoots.class),

    // PROTOCOL
    PROTOCOL_HELMET(    "Protocol 781-A Helmet",     Material.IRON_HELMET,    ItemRarity.EPIC, ProtocolHelmet.class),
    PROTOCOL_CHESTPLATE("Protocol 781-A Chestplate", Material.IRON_CHESTPLATE,ItemRarity.EPIC, ProtocolArmorSet.class),
    PROTOCOL_LEGGINGS(  "Protocol 781-A Leggings",   Material.IRON_LEGGINGS,  ItemRarity.EPIC, ProtocolArmorSet.class),
    PROTOCOL_BOOTS(     "Protocol 781-A Boots",      Material.IRON_BOOTS,     ItemRarity.EPIC, ProtocolArmorSet.class),
    DISPLACEMENT_MATRIX("Displacement Matrix",      Material.NETHER_STAR,     ItemRarity.RARE, true, 60_000),
    WARP_CATALYST("Warp Catalyst", ItemRarity.EPIC, WarpCatalyst.class),

    // ELDERFLAME
    ELDERFLAME_HELMET(    "Elderflame Helmet",     Material.NETHERITE_HELMET,    ItemRarity.LEGENDARY, ElderflameHelmet.class),
    ELDERFLAME_CHESTPLATE("Elderflame Wings",      Material.ELYTRA,              ItemRarity.LEGENDARY, ElderflameChestplate.class),
    ELDERFLAME_LEGGINGS(  "Elderflame Leggings",   Material.NETHERITE_LEGGINGS,  ItemRarity.LEGENDARY, ElderflameLeggings.class),
    ELDERFLAME_BOOTS(     "Elderflame Boots",      Material.LEATHER_BOOTS,       ItemRarity.LEGENDARY, ElderflameBoots.class),
    ELDERFLAME_DAGGER("Elderflame Dagger", Material.DIAMOND_SWORD, ItemRarity.LEGENDARY, ElderflameDagger.class),
    VOID_RELIC("Void Relic", ItemRarity.LEGENDARY, VoidRelic.class),

    // ARAXYS SET
    ARAXYS_HELMET("Araxys Helmet",         Material.SPAWNER,              ItemRarity.EPIC, AraxysHelmet.class),
    ARAXYS_CHESTPLATE("Araxys Chestplate", Material.CHAINMAIL_CHESTPLATE, ItemRarity.EPIC, AraxysChestplate.class),
    ARAXYS_LEGGINGS("Araxys Leggings",     Material.CHAINMAIL_LEGGINGS,   ItemRarity.EPIC, AraxysLeggings.class),
    ARAXYS_BOOTS("Araxys Boots",           Material.CHAINMAIL_BOOTS,      ItemRarity.EPIC, AraxysBoots.class),
    ARAXYS_CLAW("Araxys Claw",             Material.SHEARS,               ItemRarity.EPIC, AraxysClaw.class),

    // IMPERIUM
    IMPERIUM_HELMET(    "Imperium Helmet",     Material.IRON_HELMET,      ItemRarity.EPIC, ImperiumHelmet.class),
    IMPERIUM_CHESTPLATE("Imperium Chestplate", Material.IRON_CHESTPLATE,  ItemRarity.EPIC, ImperiumChestplate.class),
    IMPERIUM_LEGGINGS(  "Imperium Leggings",   Material.IRON_LEGGINGS,    ItemRarity.EPIC, ImperiumLeggings.class),
    IMPERIUM_BOOTS(     "Imperium Boots",      Material.IRON_BOOTS,       ItemRarity.EPIC, ImperiumBoots.class),

    // SNOWFALL SET
    SNOWFALL_HELMET(    "Snowfall Helmet",         Material.NETHERITE_HELMET,     ItemRarity.EPIC,  SnowfallHelmet.class),
    SNOWFALL_CHESTPLATE("Snowfall Chestplate",     Material.NETHERITE_CHESTPLATE, ItemRarity.EPIC,  SnowfallChestplate.class),
    SNOWFALL_LEGGINGS(  "Snowfall Leggings",       Material.NETHERITE_LEGGINGS,   ItemRarity.EPIC,  SnowfallLeggings.class),
    SNOWFALL_BOOTS(     "Snowfall Boots",          Material.NETHERITE_BOOTS,      ItemRarity.EPIC,  SnowfallBoots.class),

    // VALIANT SET
    VALIANT_HELMET(    "Valiant Helmet",         Material.NETHERITE_HELMET,     ItemRarity.EPIC,  ValiantHelmet.class),
    VALIANT_CHESTPLATE("Valiant Chestplate",     Material.NETHERITE_CHESTPLATE, ItemRarity.EPIC,  ValiantChestplate.class),
    VALIANT_LEGGINGS(  "Valiant Leggings",       Material.NETHERITE_LEGGINGS,   ItemRarity.EPIC,  ValiantLeggings.class),
    VALIANT_BOOTS(     "Valiant Boots",          Material.NETHERITE_BOOTS,      ItemRarity.EPIC,  ValiantBoots.class),

    // PRELUDE SET
    PRELUDE_HELMET(    "Prelude to Chaos Helmet",         Material.NETHERITE_HELMET,     ItemRarity.LEGENDARY,  PreludeHelmet.class),
    PRELUDE_CHESTPLATE("Prelude to Chaos Chestplate",     Material.NETHERITE_CHESTPLATE, ItemRarity.LEGENDARY,  PreludeChestplate.class),
    PRELUDE_LEGGINGS(  "Prelude to Chaos Leggings",       Material.NETHERITE_LEGGINGS,   ItemRarity.LEGENDARY,  PreludeLeggings.class),
    PRELUDE_BOOTS(     "Prelude to Chaos Boots",          Material.NETHERITE_BOOTS,      ItemRarity.LEGENDARY,  PreludeBoots.class),
    
    // FISHING SETS
    MURKY_HELMET(    "Murky Helmet",     Material.LEATHER_HELMET,     ItemRarity.COMMON, MurkySet.class),
    MURKY_CHESTPLATE("Murky Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.COMMON, MurkySet.class),
    MURKY_LEGGINGS(  "Murky Leggings",   Material.LEATHER_LEGGINGS,   ItemRarity.COMMON, MurkySet.class),
    MURKY_BOOTS(     "Murky Boots",      Material.LEATHER_BOOTS,      ItemRarity.COMMON, MurkySet.class),

    // FISHING SETS
    MINNOW_HELMET(    "Minnow Helmet",     Material.LEATHER_HELMET,     ItemRarity.UNCOMMON, MinnowSet.class),
    MINNOW_CHESTPLATE("Minnow Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.UNCOMMON, MinnowSet.class),
    MINNOW_LEGGINGS(  "Minnow Leggings",   Material.LEATHER_LEGGINGS,   ItemRarity.UNCOMMON, MinnowSet.class),
    MINNOW_BOOTS(     "Minnow Boots",      Material.LEATHER_BOOTS,      ItemRarity.UNCOMMON, MinnowSet.class),
    MINNOW_SCALE("Minnow Scale", Material.LIGHT_GRAY_DYE, ItemRarity.UNCOMMON, false, 4_500),

    IRON_ROD("Iron Rod", Material.FISHING_ROD, ItemRarity.COMMON, WaterRod.class),
    GOLD_ROD("Gold Rod", Material.FISHING_ROD, ItemRarity.COMMON, WaterRod.class),
    DIAMOND_ROD("Diamond Rod", Material.FISHING_ROD, ItemRarity.UNCOMMON, WaterRod.class),
    PRISMARINE_ROD("Prismarine Rod", Material.FISHING_ROD, ItemRarity.RARE, WaterRod.class),
    NETHERITE_ROD("Netherite Rod", Material.FISHING_ROD, ItemRarity.RARE, LavaRod.class),
    SPITFIRE_ROD("Spitfire Rod", Material.FISHING_ROD, ItemRarity.EPIC, LavaRod.class),

    SHARK_FIN("Shark Fin", Material.PRISMARINE_SHARD, ItemRarity.RARE, true, 5000),
    CINDERITE("Cinderite", Material.BLAZE_POWDER, ItemRarity.RARE, true, 7500),
    ECHO_MEMBRANE("Echo Membrane", Material.PHANTOM_MEMBRANE, ItemRarity.RARE, true, 15000),

    COMET_ROD("Comet Rod", Material.FISHING_ROD, ItemRarity.RARE, VoidRod.class),
    NEBULA_ROD("Nebula Rod", Material.FISHING_ROD, ItemRarity.EPIC, VoidRod.class),

    // ELYTRAS
    PHANTOM_WINGS("Phantom Wings", Material.ELYTRA, ItemRarity.RARE, PhantomWings.class),
    EVORI_DREAMWINGS("Evori Dreamwings", Material.ELYTRA, ItemRarity.EPIC, EvoriDreamwings.class),

    // CHARMS
    SPEED_CHARM("Speed Charm",       Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      SpeedCharm.class),
    STRENGTH_CHARM("Strength Charm", Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      StrengthCharm.class),
    LUCKY_CHARM("Lucky Charm",       Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      LuckyCharm.class),

    // BOWS
    NETHERITE_BOW("Netherite Bow", Material.BOW, ItemRarity.RARE,     NetheriteBow.class),
    DIAMOND_BOW("Diamond Bow",     Material.BOW, ItemRarity.UNCOMMON, DiamondBow.class),
    IRON_BOW(   "Iron Bow",        Material.BOW, ItemRarity.COMMON,   IronBow.class),

    // EXILED SET
    EXILED_CROSSBOW("Exiled Crossbow", Material.CROSSBOW, ItemRarity.EPIC,  ExiledCrossbow.class),
    EXILED_AXE("Exiled Axe", Material.IRON_AXE, ItemRarity.EPIC,  ExiledAxe.class),

    // MISC TOOLS
    SQUID_HELMET("Squid Helmet", ItemRarity.RARE, SquidHelmet.class),
    MAGMA_HELMET("Magma Helmet", ItemRarity.RARE, MagmaHelmet.class),
    GRAPPLING_HOOK("Grappling Hook", Material.FISHING_ROD, ItemRarity.RARE, GrapplingHook.class),
    CRYSTALLIZED_SUGAR("Crystallized Sugar", Material.SUGAR, ItemRarity.RARE, true, CrystallizedSugarBlueprint.class),

    // HEALING WANDS
    HEALING_WAND("Wand of Healing", Material.STICK, ItemRarity.COMMON, true, HealingWandBlueprint.class),
    ALLEVIATION_WAND("Wand of Alleviation", Material.STICK, ItemRarity.UNCOMMON, true, HealingWandBlueprint.class),
    CURING_ROD("Scepter of Curing", Material.STICK, ItemRarity.RARE, true, HealingWandBlueprint.class),
    STAFF_OF_REGENERATION("Staff of Regeneration", Material.STICK, ItemRarity.EPIC, true, HealingWandBlueprint.class),
    STAFF_OF_REJUVENATION("Staff of Rejuvenation", Material.STICK, ItemRarity.LEGENDARY, true, HealingWandBlueprint.class),

    SMALL_BACKPACK("Small Backpack", Material.FIREWORK_STAR, ItemRarity.COMMON, SmallBackpack.class),
    MEDIUM_BACKPACK("Medium Backpack", Material.FIREWORK_STAR, ItemRarity.UNCOMMON, MediumBackpack.class),
    LARGE_BACKPACK("Large Backpack", Material.FIREWORK_STAR, ItemRarity.RARE, LargeBackpack.class),
    GIGANTIC_BACKPACK("Gigantic Backpack", Material.FIREWORK_STAR, ItemRarity.EPIC, GiganticBackpack.class),
    COLOSSAL_BACKPACK("Colossal Backpack", Material.FIREWORK_STAR, ItemRarity.LEGENDARY, ColossalBackpack.class),

    THERMOMETER("Thermometer", Material.ECHO_SHARD, ItemRarity.RARE, ThermometerBlueprint.class),

    // COMMON OVERWORLD FISH
    COD("Cod",       Material.TROPICAL_FISH, ItemRarity.COMMON, FishBlueprint.class),
    SALMON("Salmon", Material.TROPICAL_FISH, ItemRarity.COMMON, FishBlueprint.class),
    CARP("Carp",     Material.TROPICAL_FISH, ItemRarity.COMMON, FishBlueprint.class),
    GUPPY("Guppy",   Material.TROPICAL_FISH, ItemRarity.COMMON, FishBlueprint.class),

    // UNCOMMON OVERWORLD FISH
    BASS("Bass",             Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),
    CLOWNFISH("Clownfish",   Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),
    PUFFERFISH("Pufferfish", Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),
    CATFISH("Catfish",       Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),
    SNAPPER("Snapper",       Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),

    // RARE OVERWORLD FISH
    PIKE("Pike",                     Material.TROPICAL_FISH, ItemRarity.RARE, FishBlueprint.class),
    STURGEON("Sturgeon",             Material.TROPICAL_FISH, ItemRarity.RARE, FishBlueprint.class),
    BLUE_TANG("Blue Tang",           Material.TROPICAL_FISH, ItemRarity.RARE, FishBlueprint.class),
    BARRACUDA("Barracuda",           Material.TROPICAL_FISH, ItemRarity.RARE, FishBlueprint.class),
    YELLOWFIN_TUNA("Yellowfin Tuna", Material.TROPICAL_FISH, ItemRarity.RARE, FishBlueprint.class),

    // EPIC OVERWORLD FISH
    GOLIATH_GROUPER("Goliath Grouper", Material.TROPICAL_FISH, ItemRarity.EPIC, FishBlueprint.class),
    LEAFY_SEADRAGON("Leafy Seadragon", Material.TROPICAL_FISH, ItemRarity.EPIC, FishBlueprint.class),
    LIONFISH("Lionfish",               Material.TROPICAL_FISH, ItemRarity.EPIC, FishBlueprint.class),

    // LEGENDARY OVERWORLD FISH
    BLUE_MARLIN("Blue Marlin",                 Material.TROPICAL_FISH, ItemRarity.LEGENDARY, FishBlueprint.class),
    FANGTOOTH("Fangtooth",                     Material.TROPICAL_FISH, ItemRarity.LEGENDARY, FishBlueprint.class),
    DEEP_SEA_ANGLERFISH("Deep Sea Anglerfish", Material.TROPICAL_FISH, ItemRarity.LEGENDARY, FishBlueprint.class),

    // COMMON NETHER FISH
    BLISTERFISH("Blisterfish",  Material.TROPICAL_FISH, ItemRarity.COMMON,    FishBlueprint.class),
    IMPLING("Impling",          Material.TROPICAL_FISH, ItemRarity.COMMON,    FishBlueprint.class),

    // UNCOMMON NETHER FISH
    CRIMSONFISH("Crimsonfish",  Material.TROPICAL_FISH, ItemRarity.UNCOMMON,  FishBlueprint.class),

    // RARE NETHER FISH
    BONE_MAW("Bone Maw",        Material.TROPICAL_FISH, ItemRarity.RARE,      FishBlueprint.class),
    SOUL_SCALE("Soul Scale",    Material.TROPICAL_FISH, ItemRarity.RARE,      FishBlueprint.class),

    // EPIC NETHER FISH
    FLAREFIN("Flarefin",        Material.TROPICAL_FISH, ItemRarity.EPIC,      FishBlueprint.class),
    GHOST_FISH("Ghost Fish",    Material.TROPICAL_FISH, ItemRarity.EPIC,      FishBlueprint.class),

    // LEGENDARY NETHER FISH
    DEVIL_RAY("Devil Ray",      Material.TROPICAL_FISH, ItemRarity.LEGENDARY, FishBlueprint.class),

    // COMMON END FISH
    VOIDFIN("Voidfin",                              Material.TROPICAL_FISH, ItemRarity.COMMON,     FishBlueprint.class),
    ORBLING("Orbling",                              Material.TROPICAL_FISH, ItemRarity.COMMON,     FishBlueprint.class),

    // UNCOMMON END FISH
    WARPER("Warper",                                Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),
    BLOBFISH("Blobfish",                            Material.TROPICAL_FISH, ItemRarity.UNCOMMON,     FishBlueprint.class),

    // RARE END FISH
    GOBLIN_SHARK("Goblin Shark",                    Material.TROPICAL_FISH, ItemRarity.RARE,     FishBlueprint.class),

    // EPIC END FISH
    STARSURFER("Star Surfer",                       Material.TROPICAL_FISH, ItemRarity.EPIC,     FishBlueprint.class),
    ABYSSAL_SQUID("Abyssal Squid",                  Material.TROPICAL_FISH, ItemRarity.EPIC,     FishBlueprint.class),

    // LEGENDARY END FISH
    TWILIGHT_ANGLERFISH("Twilight Angler Fish",    Material.TROPICAL_FISH, ItemRarity.LEGENDARY,     FishBlueprint.class),
    COSMIC_CUTTLEFISH("Cosmic Cuttlefish",          Material.TROPICAL_FISH, ItemRarity.LEGENDARY,     FishBlueprint.class),

    // FISH STUFF
    CAVIAR("Caviar", Material.PHANTOM_MEMBRANE, ItemRarity.EPIC, true, 25_000),

    // FOOD
    STALE_BREAD("Stale Bread", Material.BREAD, ItemRarity.COMMON, StaleBread.class),
    POTATO_CHIP("Potato Chips", Material.RAW_GOLD, ItemRarity.COMMON, PotatoChip.class),
    COTTON_CANDY("Cotton Candy", Material.PINK_DYE, ItemRarity.COMMON, CottonCandy.class),
    SOGGY_LETTUCE("Soggy Lettuce", Material.GREEN_DYE, ItemRarity.COMMON, SoggyLettuce.class),
    STOLEN_APPLE("Stolen Apple", Material.APPLE, ItemRarity.COMMON, StolenApples.class),
    PHANTOM_CURRY("Phantom Curry", Material.RAW_COPPER, ItemRarity.COMMON, PhantomCurry.class),
    SHADOW_BREW("Shadow Brew", Material.FIREWORK_STAR, ItemRarity.COMMON, ShadowBrew.class),
    CHILI_PEPPER("Chili Pepper", Material.GOLDEN_CARROT, ItemRarity.COMMON, ChiliPepper.class),
    ENDERIOS("Enderios", Material.DISC_FRAGMENT_5, ItemRarity.COMMON, EnderiosBlueprint.class),
    BREADBOARD("Breadboard", Material.PAPER, ItemRarity.COMMON, BreadboardBlueprint.class),
    CHARRED_CRISP("Charred Crisp", Material.DRIED_KELP, ItemRarity.COMMON, CharredCrispBlueprint.class),

    // EXP BOTTLES
    EXPERIENCE_BOTTLE("Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemRarity.COMMON, ExperienceBottle.class),  // normal lapis
    LARGE_EXPERIENCE_BOTTLE("Large Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemRarity.UNCOMMON, ExperienceBottle.class),  // block of lapis
    HEFTY_EXPERIENCE_BOTTLE("Hefty Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemRarity.RARE, ExperienceBottle.class),  // ench lapis
    GIGANTIC_EXPERIENCE_BOTTLE("Gigantic Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemRarity.EPIC, ExperienceBottle.class),  // ench block of lapis
    COLOSSAL_EXPERIENCE_BOTTLE("Colossal Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemRarity.LEGENDARY, ExperienceBottle.class),  // lapis singularity

    // COMPRESSED MINING MATERIALS

    // Cobblestone
    COMPRESSED_COBBLESTONE("Compressed Cobblestone", Material.COBBLESTONE, ItemRarity.COMMON, true, CobblestoneFamilyBlueprint.class),
    DOUBLE_COMPRESSED_COBBLESTONE("Double Compressed Cobblestone", Material.COBBLESTONE, ItemRarity.UNCOMMON, true, CobblestoneFamilyBlueprint.class),
    ENCHANTED_COBBLESTONE("Enchanted Cobblestone", Material.COBBLESTONE, ItemRarity.UNCOMMON, true, CobblestoneFamilyBlueprint.class),
    COBBLESTONE_SINGULARITY("Cobblestone Singularity", Material.BEDROCK, ItemRarity.RARE, true, CobblestoneFamilyBlueprint.class),

    // Deepslate
    COMPRESSED_DEEPSLATE( "Compressed Deepslate",                Material.COBBLED_DEEPSLATE, ItemRarity.COMMON,  true, DeepslateFamilyBlueprint.class),
    DOUBLE_COMPRESSED_DEEPSLATE( "Double Compressed Deepslate",  Material.COBBLED_DEEPSLATE, ItemRarity.UNCOMMON,      true, DeepslateFamilyBlueprint.class),
    ENCHANTED_DEEPSLATE(  "Enchanted Deepslate",                 Material.COBBLED_DEEPSLATE, ItemRarity.UNCOMMON,      true, DeepslateFamilyBlueprint.class),
    DEEPSLATE_SINGULARITY("Deepslate Singularity",               Material.BEDROCK,           ItemRarity.RARE, true, DeepslateFamilyBlueprint.class),

    COMPRESSED_OBSIDIAN("Compressed Obsidian", Material.OBSIDIAN, ItemRarity.UNCOMMON, true, ObsidianFamilyBlueprint.class),
    ENCHANTED_OBSIDIAN("Enchanted Obsidian", Material.OBSIDIAN, ItemRarity.RARE, true, ObsidianFamilyBlueprint.class),

    // COAL
    ENCHANTED_COAL("Enchanted Coal",                Material.COAL,       ItemRarity.RARE,      true, CoalFamilyBlueprint.class),
    ENCHANTED_COAL_BLOCK("Enchanted Block of Coal", Material.COAL_BLOCK, ItemRarity.EPIC,      true, CoalFamilyBlueprint.class),
    COAL_SINGULARITY("Coal Singularity",            Material.COAL,       ItemRarity.LEGENDARY, true, CoalFamilyBlueprint.class),

    // CHARCOAL
    COMPRESSED_CHARCOAL("Compressed Charcoal", Material.CHARCOAL, ItemRarity.UNCOMMON, true, CharcoalFamilyBlueprint.class),
    ENCHANTED_CHARCOAL( "Enchanted Charcoal",  Material.CHARCOAL, ItemRarity.RARE,     true, CharcoalFamilyBlueprint.class),

    // FLINT
    COMPRESSED_FLINT("Compressed Flint", Material.FLINT, ItemRarity.UNCOMMON, true, FlintFamilyBlueprint.class),
    ENCHANTED_FLINT( "Enchanted Flint",  Material.FLINT, ItemRarity.RARE,     true, FlintFamilyBlueprint.class),

    // COPPER
    ENCHANTED_COPPER(      "Enchanted Copper",          Material.COPPER_INGOT, ItemRarity.RARE,      true, CopperFamilyBlueprint.class),
    ENCHANTED_COPPER_BLOCK("Enchanted Block of Copper", Material.COPPER_BLOCK, ItemRarity.EPIC,      true, CopperFamilyBlueprint.class),
    COPPER_SINGULARITY(    "Copper Singularity",        Material.COPPER_INGOT, ItemRarity.LEGENDARY, true, CopperFamilyBlueprint.class),

    // IRON
    ENCHANTED_IRON(      "Enchanted Iron",          Material.IRON_INGOT, ItemRarity.RARE,      true, IronFamilyBlueprint.class),
    ENCHANTED_IRON_BLOCK("Enchanted Block of Iron", Material.IRON_BLOCK, ItemRarity.EPIC,      true, IronFamilyBlueprint.class),
    IRON_SINGULARITY(    "Iron Singularity",        Material.IRON_INGOT, ItemRarity.LEGENDARY, true, IronFamilyBlueprint.class),

    // LAPIS
    ENCHANTED_LAPIS(      "Enchanted Lapis Lazuli",          Material.LAPIS_LAZULI, ItemRarity.RARE,      true, LapisFamilyBlueprint.class),
    ENCHANTED_LAPIS_BLOCK("Enchanted Block of Lapis Lazuli", Material.LAPIS_BLOCK,  ItemRarity.EPIC,      true, LapisFamilyBlueprint.class),
    LAPIS_SINGULARITY(    "Lapis Lazuli Singularity",        Material.LAPIS_LAZULI, ItemRarity.LEGENDARY, true, LapisFamilyBlueprint.class),

    // REDSTONE
    ENCHANTED_REDSTONE(      "Enchanted Redstone",          Material.REDSTONE,       ItemRarity.RARE,      true, RedstoneFamilyBlueprint.class),
    ENCHANTED_REDSTONE_BLOCK("Enchanted Block of Redstone", Material.REDSTONE_BLOCK, ItemRarity.EPIC, true, RedstoneFamilyBlueprint.class),
    REDSTONE_SINGULARITY(    "Redstone Singularity",        Material.REDSTONE,       ItemRarity.LEGENDARY,    true, RedstoneFamilyBlueprint.class),

    // GLOWSTONE
    ENCHANTED_GLOWSTONE(      "Enchanted Glowstone",          Material.GLOWSTONE_DUST, ItemRarity.RARE,      true, GlowstoneFamilyBlueprint.class),
    ENCHANTED_GLOWSTONE_BLOCK("Enchanted Block of Glowstone", Material.GLOWSTONE,      ItemRarity.EPIC,      true, GlowstoneFamilyBlueprint.class),
    GLOWSTONE_SINGULARITY(    "Glowstone Singularity",        Material.GLOWSTONE_DUST, ItemRarity.LEGENDARY, true, GlowstoneFamilyBlueprint.class),

    // AMETHYST
    ENCHANTED_AMETHYST(      "Enchanted Amethyst",          Material.AMETHYST_SHARD, ItemRarity.UNCOMMON,      true, AmethystFamilyBlueprint.class),
    ENCHANTED_AMETHYST_BLOCK("Enchanted Block of Amethyst", Material.AMETHYST_BLOCK, ItemRarity.RARE,      true, AmethystFamilyBlueprint.class),
    AMETHYST_SINGULARITY(    "Amethyst Singularity",        Material.AMETHYST_SHARD, ItemRarity.EPIC, true, AmethystFamilyBlueprint.class),

    // GOLD
    ENCHANTED_GOLD(      "Enchanted Gold",          Material.GOLD_INGOT, ItemRarity.RARE,      true, GoldFamilyBlueprint.class),
    ENCHANTED_GOLD_BLOCK("Enchanted Block of Gold", Material.GOLD_BLOCK, ItemRarity.EPIC,      true, GoldFamilyBlueprint.class),
    GOLD_SINGULARITY(    "Gold Singularity",        Material.GOLD_INGOT, ItemRarity.LEGENDARY, true, GoldFamilyBlueprint.class),

    // DIAMOND
    ENCHANTED_DIAMOND(      "Enchanted Diamond",          Material.DIAMOND,       ItemRarity.EPIC,      true, DiamondFamilyBlueprint.class),
    ENCHANTED_DIAMOND_BLOCK("Enchanted Block of Diamond", Material.DIAMOND_BLOCK, ItemRarity.LEGENDARY, true, DiamondFamilyBlueprint.class),
    DIAMOND_SINGULARITY(    "Diamond Singularity",        Material.DIAMOND,       ItemRarity.MYTHIC,    true, DiamondFamilyBlueprint.class),

    // EMERALD
    ENCHANTED_EMERALD(      "Enchanted Emerald",          Material.EMERALD,       ItemRarity.EPIC,      true, EmeraldFamilyBlueprint.class),
    ENCHANTED_EMERALD_BLOCK("Enchanted Block of Emerald", Material.EMERALD_BLOCK, ItemRarity.LEGENDARY, true, EmeraldFamilyBlueprint.class),
    EMERALD_SINGULARITY(    "Emerald Singularity",        Material.EMERALD,       ItemRarity.MYTHIC,    true, EmeraldFamilyBlueprint.class),

    // QUARTZ
    ENCHANTED_QUARTZ(      "Enchanted Quartz",          Material.QUARTZ,       ItemRarity.RARE,      true, QuartzFamilyBlueprint.class),
    ENCHANTED_QUARTZ_BLOCK("Enchanted Block of Quartz", Material.QUARTZ_BLOCK, ItemRarity.EPIC,      true, QuartzFamilyBlueprint.class),
    QUARTZ_SINGULARITY(    "Quartz Singularity",        Material.QUARTZ,       ItemRarity.LEGENDARY, true, QuartzFamilyBlueprint.class),

    // NETHERITE
    ENCHANTED_NETHERITE(      "Enchanted Netherite",          Material.NETHERITE_INGOT, ItemRarity.LEGENDARY, true, NetheriteFamilyBlueprint.class),
    ENCHANTED_NETHERITE_BLOCK("Enchanted Block of Netherite", Material.NETHERITE_BLOCK, ItemRarity.MYTHIC,    true, NetheriteFamilyBlueprint.class),
    NETHERITE_SINGULARITY(    "Netherite Singularity",        Material.NETHERITE_INGOT, ItemRarity.DIVINE,    true, NetheriteFamilyBlueprint.class),

    // MOB DROPS (OVERWORLD)

    // ROTTEN FLESH
    PREMIUM_FLESH("Premium Flesh", Material.ROTTEN_FLESH, ItemRarity.UNCOMMON, true, FleshFamilyBlueprint.class),
    ENCHANTED_FLESH("Enchanted Flesh", Material.ROTTEN_FLESH, ItemRarity.RARE, true, FleshFamilyBlueprint.class),

    // BONE
    PREMIUM_BONE("Premium Bone", Material.BONE, ItemRarity.UNCOMMON, true, BoneFamilyBlueprint.class),
    ENCHANTED_BONE("Enchanted Bone", Material.BONE, ItemRarity.RARE, true, BoneFamilyBlueprint.class),

    // STRING
    PREMIUM_STRING("Premium String", Material.STRING, ItemRarity.UNCOMMON, true, StringFamilyBlueprint.class),
    ENCHANTED_STRING("Enchanted String", Material.STRING, ItemRarity.RARE, true, StringFamilyBlueprint.class),

    // SPIDER EYE
    PREMIUM_SPIDER_EYE("Premium Spider Eye", Material.SPIDER_EYE, ItemRarity.UNCOMMON, true, SpiderEyeFamilyBlueprint.class),
    ENCHANTED_SPIDER_EYE("Enchanted Spider Eye", Material.SPIDER_EYE, ItemRarity.RARE, true, SpiderEyeFamilyBlueprint.class),

    // SLIME BALL
    PREMIUM_SLIME("Premium Slime Ball", Material.SLIME_BALL, ItemRarity.UNCOMMON, true, SlimeFamilyBlueprint.class),
    ENCHANTED_SLIME("Enchanted Slime Ball", Material.SLIME_BALL, ItemRarity.RARE, true, SlimeFamilyBlueprint.class),

    // GUNPOWDER
    PREMIUM_GUNPOWDER("Premium Gunpowder", Material.GUNPOWDER, ItemRarity.UNCOMMON, true, GunpowderFamilyBlueprint.class),
    ENCHANTED_GUNPOWDER("Enchanted Gunpowder", Material.GUNPOWDER, ItemRarity.RARE, true, GunpowderFamilyBlueprint.class),

    // MEMBRANES
    PREMIUM_MEMBRANE("Premium Membrane", Material.PHANTOM_MEMBRANE, ItemRarity.UNCOMMON, true, PhantomMembraneFamilyBlueprint.class),
    ENCHANTED_MEMBRANE("Enchanted Membrane", Material.PHANTOM_MEMBRANE, ItemRarity.RARE, true, PhantomMembraneFamilyBlueprint.class),

    // INK SAC
    PREMIUM_INK_SAC("Premium Ink Sac", Material.INK_SAC, ItemRarity.UNCOMMON, true, InkSacFamilyBlueprint.class),
    ENCHANTED_INK_SAC("Enchanted Ink Sac", Material.INK_SAC, ItemRarity.RARE, true, InkSacFamilyBlueprint.class),

    // PRISMARINE SHARD
    PREMIUM_PRISMARINE_SHARD("Premium Prismarine Shard", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, true, PrismarineShardFamilyBlueprint.class),
    ENCHANTED_PRISMARINE_SHARD("Enchanted Prismarine Shard", Material.PRISMARINE_SHARD, ItemRarity.RARE, true, PrismarineShardFamilyBlueprint.class),

    // PRISMARINE CRYSTAL
    PREMIUM_PRISMARINE_CRYSTAL("Premium Prismarine Crystals", Material.PRISMARINE_CRYSTALS, ItemRarity.UNCOMMON, true, PrismarineCrystalsFamilyBlueprint.class),
    ENCHANTED_PRISMARINE_CRYSTAL("Enchanted Prismarine Crystals", Material.PRISMARINE_CRYSTALS, ItemRarity.RARE, true, PrismarineCrystalsFamilyBlueprint.class),

    // NAUTILUS SHELL
    PREMIUM_NAUTILUS_SHELL("Premium Nautilus Shell", Material.NAUTILUS_SHELL, ItemRarity.RARE, true, NautilisShellFamilyBlueprint.class),
    ENCHANTED_NAUTILUS_SHELL("Enchanted Nautilus Shell", Material.NAUTILUS_SHELL, ItemRarity.EPIC, true, NautilisShellFamilyBlueprint.class),

    // ECHO SHARD
    PREMIUM_ECHO_SHARD("Premium Echo Shard", Material.ECHO_SHARD, ItemRarity.EPIC, true, EchoShardFamilyBlueprint.class),
    ENCHANTED_ECHO_SHARD("Enchanted Echo Shard", Material.ECHO_SHARD, ItemRarity.LEGENDARY, true, EchoShardFamilyBlueprint.class),

    // NETHER MOBS

    // BLAZE
    PREMIUM_BLAZE_ROD("Premium Blaze Rod", Material.BLAZE_ROD, ItemRarity.UNCOMMON, true, BlazeRodFamilyBlueprint.class),
    ENCHANTED_BLAZE_ROD("Enchanted Blaze Rod", Material.BLAZE_ROD, ItemRarity.RARE, true, BlazeRodFamilyBlueprint.class),

    // NETHER STAR
    PREMIUM_NETHER_STAR("Premium Nether Star", Material.NETHER_STAR, ItemRarity.LEGENDARY, true, NetherStarFamilyBlueprint.class),
    ENCHANTED_NETHER_STAR("Enchanted Nether Star", Material.NETHER_STAR, ItemRarity.MYTHIC, true, NetherStarFamilyBlueprint.class),

    // MAGMA
    PREMIUM_MAGMA_CREAM("Premium Magma Cream", Material.MAGMA_CREAM, ItemRarity.UNCOMMON, true, MagmaCreamFamilyBlueprint.class),
    ENCHANTED_MAGMA_CREAM("Enchanted Magma Cream", Material.MAGMA_CREAM, ItemRarity.RARE, true, MagmaCreamFamilyBlueprint.class),

    // END MOBS

    // ENDER PEARL
    PREMIUM_ENDER_PEARL("Premium Ender Pearl", Material.ENDER_PEARL, ItemRarity.RARE, true, EnderPearlFamilyBlueprint.class),
    ENCHANTED_ENDER_PEARL("Enchanted Ender Pearl", Material.ENDER_PEARL, ItemRarity.EPIC, true, EnderPearlFamilyBlueprint.class),

    // SHULKER
    PREMIUM_SHULKER_SHELL("Premium Shulker Shell", Material.SHULKER_SHELL, ItemRarity.RARE, true, ShulkerFamilyBlueprint.class),
    ENCHANTED_SHULKER_SHELL("Enchanted Shulker Shell", Material.SHULKER_SHELL, ItemRarity.EPIC, true, ShulkerFamilyBlueprint.class),

    // GUARDIAN
    ENCHANTED_MILK_BUCKET("Enchanted Milk Bucket", Material.MILK_BUCKET, ItemRarity.RARE, true, EnchantedMilkBucket.class),
    DIAMOND_TOOL_ROD("Diamond Tool Rod", Material.BREEZE_ROD, ItemRarity.UNCOMMON, DiamondToolRod.class),
    NEPTUNES_CONCH("Neptune's Conch Shell", Material.NAUTILUS_SHELL, ItemRarity.EPIC, true, NeptunesConch.class),
    PLUTO_FRAGMENT("Pluto Fragment", Material.PRISMARINE_SHARD, ItemRarity.RARE, true, 4000),
    PLUTOS_ARTIFACT("Pluto's Artifact", ItemRarity.EPIC, PlutosArtifact.class),
    JUPITER_CRYSTAL("Jupiter Crystal", Material.PRISMARINE_CRYSTALS, ItemRarity.RARE, true, 1000),
    JUPITERS_ARTIFACT("Jupiter's Artifact", ItemRarity.EPIC, JupiterArtifact.class),
    IRIDESCENT_LENS("Iridescent Lens", ItemRarity.LEGENDARY, IridescentLens.class),
    HYPNOTIC_EYE("Hypnotic Eye", ItemRarity.LEGENDARY, HypnoticEye.class),

    MOSSY_SKULL("Mossy Skull", ItemRarity.EPIC, MossySkull.class),
    GILDED_SKULL("Gilded Skull", ItemRarity.LEGENDARY, GildedSkull.class),

    // DRAGON
    DRAGON_SCALES(   "Dragon Scales",    Material.PHANTOM_MEMBRANE, ItemRarity.RARE, true, 50_000),
    DRACONIC_CRYSTAL("Draconic Crystal", ItemRarity.EPIC, DraconicCrystal.class),
    TRANSMISSION_WAND("Transmission Wand", Material.PRISMARINE_SHARD, ItemRarity.EPIC, TransmissionWand.class),
    MAGIC_MIRROR("Magic Mirror", Material.RECOVERY_COMPASS, ItemRarity.EPIC, MagicMirror.class),
    SLUMBER_SHARD("Slumber Shard", Material.PRISMARINE_SHARD, ItemRarity.EPIC, MagicMirrorShard.class),
    CINDER_SHARD("Cinder Shard", Material.RESIN_BRICK, ItemRarity.EPIC, MagicMirrorShard.class),
    VOID_SHARD("Void Shard", Material.ECHO_SHARD, ItemRarity.EPIC, MagicMirrorShard.class),

    SUMMONING_CRYSTAL("Summoning Crystal", Material.END_CRYSTAL, ItemRarity.EPIC, true, 50_000),

    // PASSIVE MOBS
    PREMIUM_PORKCHOP("Premium Porkchop", Material.COOKED_PORKCHOP, ItemRarity.UNCOMMON, true, PorkchopFamilyBlueprint.class),
    ENCHANTED_PORKCHOP("Enchanted Porkchop", Material.COOKED_PORKCHOP, ItemRarity.RARE, true, PorkchopFamilyBlueprint.class),

    PREMIUM_STEAK("Premium Steak", Material.COOKED_BEEF, ItemRarity.UNCOMMON, true, SteakFamilyBlueprint.class),
    ENCHANTED_STEAK("Enchanted Steak", Material.COOKED_BEEF, ItemRarity.RARE, true, SteakFamilyBlueprint.class),

    PREMIUM_LEATHER("Premium Leather", Material.LEATHER, ItemRarity.UNCOMMON, true, LeatherFamilyBlueprint.class),
    ENCHANTED_LEATHER("Enchanted Leather", Material.LEATHER, ItemRarity.RARE, true, LeatherFamilyBlueprint.class),

    PREMIUM_RABBIT_HIDE("Premium Rabbit Hide", Material.RABBIT_HIDE, ItemRarity.UNCOMMON, true, RabbitHideFamilyBlueprint.class),
    ENCHANTED_RABBIT_HIDE("Enchanted Rabbit Hide", Material.RABBIT_HIDE, ItemRarity.RARE, true, RabbitHideFamilyBlueprint.class),

    PREMIUM_MUTTON("Premium Mutton", Material.COOKED_MUTTON, ItemRarity.UNCOMMON, true, MuttonFamilyBlueprint.class),
    ENCHANTED_MUTTON("Enchanted Mutton", Material.COOKED_MUTTON, ItemRarity.RARE, true, MuttonFamilyBlueprint.class),

    PREMIUM_CHICKEN("Premium Chicken", Material.COOKED_CHICKEN, ItemRarity.UNCOMMON, true, ChickenFamilyBlueprint.class),
    ENCHANTED_CHICKEN("Enchanted Chicken", Material.COOKED_CHICKEN, ItemRarity.RARE, true, ChickenFamilyBlueprint.class),

    PREMIUM_FEATHER("Premium Feather", Material.FEATHER, ItemRarity.UNCOMMON, true, FeatherFamilyBlueprint.class),
    ENCHANTED_FEATHER("Enchanted Feather", Material.FEATHER, ItemRarity.RARE, true, FeatherFamilyBlueprint.class),

    PREMIUM_SUGAR("Premium Sugar", Material.SUGAR, ItemRarity.UNCOMMON, true, SugarCaneBlueprintFamily.class),
    PREMIUM_SUGAR_CANE("Premium Sugar Cane", Material.SUGAR_CANE, ItemRarity.UNCOMMON, true, SugarCaneBlueprintFamily.class),
    ENCHANTED_SUGAR("Enchanted Sugar", Material.SUGAR, ItemRarity.RARE, true, SugarCaneBlueprintFamily.class),
    ENCHANTED_SUGAR_CANE("Enchanted Sugar Cane", Material.SUGAR_CANE, ItemRarity.RARE, true, SugarCaneBlueprintFamily.class),
    SUGAR_SINGULARITY("Sugar Singularity", Material.SUGAR_CANE, ItemRarity.EPIC, true, SugarCaneBlueprintFamily.class),

    PREMIUM_MELON_SLICE("Premium Melon Slice", Material.MELON_SLICE, ItemRarity.UNCOMMON, true, MelonBlueprintFamily.class),
    PREMIUM_MELON("Premium Melon", Material.MELON, ItemRarity.UNCOMMON, true, MelonBlueprintFamily.class),
    ENCHANTED_MELON_SLICE("Enchanted Melon Slice", Material.MELON_SLICE, ItemRarity.RARE, true, MelonBlueprintFamily.class),
    ENCHANTED_MELON("Enchanted Melon", Material.MELON, ItemRarity.EPIC, true, MelonBlueprintFamily.class),
    MELON_SLICE_SINGULARITY("Melon Slice Singularity", Material.GLISTERING_MELON_SLICE, ItemRarity.LEGENDARY, true, MelonBlueprintFamily.class),

    // SINGULARITY SET
    SINGULARITY_HELMET(    "Singularity Helmet",     Material.CRYING_OBSIDIAN,      ItemRarity.MYTHIC, SingularityHelmet.class),
    SINGULARITY_CHESTPLATE("Singularity Chestplate", Material.NETHERITE_CHESTPLATE, ItemRarity.MYTHIC, SingularityChestplate.class),
    SINGULARITY_LEGGINGS(  "Singularity Leggings",   Material.NETHERITE_LEGGINGS,   ItemRarity.MYTHIC, SingularityLeggings.class),
    SINGULARITY_BOOTS(     "Singularity Boots",      Material.NETHERITE_BOOTS,      ItemRarity.MYTHIC, SingularityBoots.class),

    DEATH_CERTIFICATE("Death Certificate", Material.PAPER, ItemRarity.SPECIAL, DeathCertificate.class),

    // ADMIN UTILITY
    INFINILYTRA("Infini-lytra", Material.ELYTRA, ItemRarity.SPECIAL, InfinilytraBlueprint.class),
    INFINIROCKET("Infini-rocket", Material.FIREWORK_ROCKET, ItemRarity.SPECIAL, InfinirocketBlueprint.class),
    INFINIFOOD("Infini-food", Material.COOKED_BEEF, ItemRarity.SPECIAL, InfinifoodBlueprint.class),

    // DEBUG
    // Admin armor, makes you invincible basically
    INFINITY_HELMET(    "Infinity Helmet",     Material.NETHERITE_HELMET,     ItemRarity.TRANSCENDENT, InfinityHelmet.class),
    INFINITY_CHESTPLATE("Infinity Chestplate", Material.NETHERITE_CHESTPLATE, ItemRarity.TRANSCENDENT, InfinityChestplate.class),
    INFINITY_LEGGINGS(  "Infinity Leggings",   Material.NETHERITE_LEGGINGS,   ItemRarity.TRANSCENDENT, InfinityLeggings.class),
    INFINITY_BOOTS(     "Infinity Boots",      Material.NETHERITE_BOOTS,      ItemRarity.TRANSCENDENT, InfinityBoots.class),
    INFINITY_SWORD(     "Infinity Sword",      Material.NETHERITE_SWORD,      ItemRarity.TRANSCENDENT, InfinitySword.class),
    HEARTY_HELMET("Hearty Helmet", Material.NETHERITE_HELMET, ItemRarity.TRANSCENDENT, HeartyHelmet.class),

    GAME_BREAKER("Game Breaker", Material.TNT, ItemRarity.SPECIAL, true, GameBreaker.class),

    BURGER("Burger", Material.PLAYER_HEAD, ItemRarity.SPECIAL, SimpleTexturedItem.class),

    SPACE_HELMET("Space Helmet", Material.RED_STAINED_GLASS, ItemRarity.SPECIAL, true, SpaceHelmet.class),
    SPIDER_REPELLENT("Spider Repellent", Material.POTION, ItemRarity.SPECIAL, true, SpiderRepellentBlueprint.class),

    ITEM_MAGNET("Item Magnet", Material.BRUSH, ItemRarity.SPECIAL, true, ItemMagnet.class),
    ENTITY_DELETER("Entity Deleter", Material.STICK, ItemRarity.SPECIAL, true, EntityDeleter.class),

    ENTITY_ANALYZER("Entity Analyzer", Material.CLOCK, ItemRarity.SPECIAL, true, EntityAnalyzer.class),
    ENTITY_ANALYZER_REPORT("Entity Analyzer Report", Material.PAPER, ItemRarity.SPECIAL, true),

    SPAWNER_EDITING_WAND("Spawner Editor Wand", Material.BREEZE_ROD, ItemRarity.SPECIAL, true, SpawnerEditorBlueprint.class),

    LEGACY_ITEM("Legacy Item", Material.PAPER, ItemRarity.SPECIAL, LegacyItemBlueprint.class),
    DUMMY_SMITHING_RESULT("DUMMY SMITHING RESULT", Material.BARRIER, ItemRarity.SPECIAL)
    ;

    public final String ItemName;
    public final Material DisplayMaterial;
    public final ItemRarity DefaultRarity;
    public final boolean WantGlow;
    public int Worth = 0;
    public final Class<? extends CustomItemBlueprint> Handler;

    /**
     * The default constructor for a custom item.
     * Provides all the options that tweak how this special item behaves.
     * @param name The name of the item.
     * @param material The material this item displays as.
     * @param rarity The default rarity of this item.
     * @param glow If this item should be forced to glow.
     * @param handler The special handler blueprint class for this item.
     */
    CustomItemType(String name, Material material, ItemRarity rarity, boolean glow, Class<? extends CustomItemBlueprint> handler) {
        this.ItemName = name;
        this.DisplayMaterial = material;
        this.DefaultRarity = rarity;
        this.WantGlow = glow;
        this.Handler = handler;
    }

    /**
     * Creates an item type that is defaulted to common rarity with no glow.
     * @param name The name of the item.
     * @param material The material to make the item display as.
     * @param handler Then handler class of the item.
     */
    CustomItemType(String name, Material material, Class<? extends CustomItemBlueprint> handler) {
        this(name, material, ItemRarity.COMMON, false, handler);
    }

    /**
     * Creates an item type that does not glow and has no associated special handler.
     * Great for items that don't have any special logic.
     * @param name The name of the item.
     * @param material The material to make the item display as.
     * @param rarity The default rarity of the item.
     */
    CustomItemType(String name, Material material, ItemRarity rarity) {
        this(name, material, rarity, false, EmptyBlueprint.class);
    }

    /**
     * Creates an item type that needs a glow override but has no special logic.
     * Great for items that don't have any special logic.
     * @param name The name of the item.
     * @param material The material to make the item display as.
     * @param rarity The default rarity of the item.
     */
    CustomItemType(String name, Material material, ItemRarity rarity, boolean WantGlow) {
        this(name, material, rarity, WantGlow, EmptyBlueprint.class);
    }

    CustomItemType(String name, Material material, ItemRarity rarity, Class<? extends CustomItemBlueprint> handler) {
        this(name, material, rarity, false, handler);
    }

    /**
     * Constructor for instantiating an item that is deemed a simple and sellable resource.
     * @param name The name of the item.
     * @param material The material this item displays as.
     * @param rarity The default rarity of the item.
     * @param WantGlow If this item should glow.
     * @param worth The worth of the item.
     */
    CustomItemType(String name, Material material, ItemRarity rarity, boolean WantGlow, int worth) {
        this(name, material, rarity, WantGlow, SellableResource.class);
        this.Worth = worth;
    }

    /**
     * Constructor for instantiating textured heads that are pretending to be items.
     * Since these items have to be player heads, we can force the material to be a head.
     * @param name The name of the item.
     * @param itemRarity The default rarity of the item.
     * @param handler The blueprint handler class.
     * @param <T> A class that extends CustomItemBlueprint but also implements the ICustomTextured interface.
     */
    <T extends CustomItemBlueprint & ICustomTextured> CustomItemType(String name, ItemRarity itemRarity, Class<T> handler) {
        this(name, Material.PLAYER_HEAD, itemRarity, handler);
    }

    public String getKey() {
        return this.toString().toLowerCase();
    }
}
