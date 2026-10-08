package xyz.devvydont.smprpg.items;

import org.bukkit.Material;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.blueprints.augment.BookOfBounties;
import xyz.devvydont.smprpg.items.blueprints.augment.HotPotatoBook;
import xyz.devvydont.smprpg.items.blueprints.augment.Recombobulator;
import xyz.devvydont.smprpg.items.blueprints.block.SuperSoil;
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineBlueprint;
import xyz.devvydont.smprpg.items.blueprints.block.interactable.ReforgeTable;
import xyz.devvydont.smprpg.items.blueprints.block.runes.*;
import xyz.devvydont.smprpg.items.blueprints.boss.DiamondToolRod;
import xyz.devvydont.smprpg.items.blueprints.boss.InfernoArrow;
import xyz.devvydont.smprpg.items.blueprints.boss.NeptunesConch;
import xyz.devvydont.smprpg.items.blueprints.charms.LuckyCharm;
import xyz.devvydont.smprpg.items.blueprints.consumables.HeartConsumableBlueprint;
import xyz.devvydont.smprpg.items.blueprints.charms.SpeedCharm;
import xyz.devvydont.smprpg.items.blueprints.charms.StrengthCharm;
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineCompressibleBlueprint;
import xyz.devvydont.smprpg.items.blueprints.craftengine.CraftEngineFoodBlueprint;
import xyz.devvydont.smprpg.items.blueprints.crops.*;
import xyz.devvydont.smprpg.items.blueprints.debug.*;
import xyz.devvydont.smprpg.items.blueprints.farming.ProgressiveHoeBlueprint;
import xyz.devvydont.smprpg.items.blueprints.food.ingredients.*;
import xyz.devvydont.smprpg.items.blueprints.misc.DeathCompass;
import xyz.devvydont.smprpg.items.blueprints.resources.farming.*;
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.SpellPowderFamilyBlueprint;
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.drops.*;
import xyz.devvydont.smprpg.items.blueprints.resources.woodcutting.*;
import xyz.devvydont.smprpg.items.blueprints.sets.aetherium.*;
import xyz.devvydont.smprpg.items.blueprints.sets.aetherutil.WingsOfIcarus;
import xyz.devvydont.smprpg.items.blueprints.sets.cobalt.*;
import xyz.devvydont.smprpg.items.blueprints.sets.iron.IronKnife;
import xyz.devvydont.smprpg.items.blueprints.sets.orichalcum.*;
import xyz.devvydont.smprpg.items.blueprints.sets.palladium.*;
import xyz.devvydont.smprpg.items.blueprints.sets.platinum.*;
import xyz.devvydont.smprpg.items.blueprints.sets.warlock.WarlockShoes;
import xyz.devvydont.smprpg.items.blueprints.sets.warlock.WarlockHood;
import xyz.devvydont.smprpg.items.blueprints.sets.warlock.WarlockRobes;
import xyz.devvydont.smprpg.items.blueprints.sets.warlock.WarlockTrousers;
import xyz.devvydont.smprpg.items.blueprints.tomes.*;
import xyz.devvydont.smprpg.items.blueprints.tomes.spells.*;
import xyz.devvydont.smprpg.items.blueprints.tools.augments.RepairCore;
import xyz.devvydont.smprpg.items.blueprints.economy.CustomItemCoin;
import xyz.devvydont.smprpg.items.blueprints.equipment.*;
import xyz.devvydont.smprpg.items.blueprints.storage.EnderPack;
import xyz.devvydont.smprpg.items.blueprints.fishing.FishBlueprint;
import xyz.devvydont.smprpg.items.blueprints.food.*;
import xyz.devvydont.smprpg.items.blueprints.misc.DeathCertificate;
import xyz.devvydont.smprpg.items.blueprints.misc.MossySkull;
import xyz.devvydont.smprpg.items.blueprints.potion.ExperienceBottle;
import xyz.devvydont.smprpg.items.blueprints.reforge.VoidRelic;
import xyz.devvydont.smprpg.items.blueprints.resources.EmptyBlueprint;
import xyz.devvydont.smprpg.items.blueprints.resources.SellableResource;
import xyz.devvydont.smprpg.items.blueprints.resources.crafting.*;
import xyz.devvydont.smprpg.items.blueprints.resources.fishing.AstralFilament;
import xyz.devvydont.smprpg.items.blueprints.resources.fishing.EtherealFiber;
import xyz.devvydont.smprpg.items.blueprints.resources.fishing.HolomokuCrest;
import xyz.devvydont.smprpg.items.blueprints.resources.mining.*;
import xyz.devvydont.smprpg.items.blueprints.resources.mob.*;
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll;
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.NecroticFlesh;
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.NecroticFleshFamilyBlueprint;
import xyz.devvydont.smprpg.items.blueprints.resources.slayer.SpellPowder;
import xyz.devvydont.smprpg.items.blueprints.reusable.SimpleTexturedItem;
import xyz.devvydont.smprpg.items.blueprints.sets.abomination.*;
import xyz.devvydont.smprpg.items.blueprints.sets.amethyst.*;
import xyz.devvydont.smprpg.items.blueprints.sets.breeze.BreezeborneStaff;
import xyz.devvydont.smprpg.items.blueprints.sets.leather.LeatherConicalHat;
import xyz.devvydont.smprpg.items.blueprints.sets.tungsten.*;
import xyz.devvydont.smprpg.items.blueprints.sets.wood.WoodStaff;
import xyz.devvydont.smprpg.items.blueprints.unobtainable.shambling.ShamblingBossBoots;
import xyz.devvydont.smprpg.items.blueprints.unobtainable.shambling.ShamblingBossChestplate;
import xyz.devvydont.smprpg.items.blueprints.unobtainable.shambling.ShamblingBossLeggings;
import xyz.devvydont.smprpg.items.blueprints.wardrobe.WardrobeSlotToken;
import xyz.devvydont.smprpg.items.blueprints.sets.adamantium.*;
import xyz.devvydont.smprpg.items.blueprints.sets.araxys.*;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.bedrock.BedrockLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.bone.BoneLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.bronze.*;
import xyz.devvydont.smprpg.items.blueprints.sets.cobblestone.*;
import xyz.devvydont.smprpg.items.blueprints.sets.copper.*;
import xyz.devvydont.smprpg.items.blueprints.sets.dragonsteel.*;
import xyz.devvydont.smprpg.items.blueprints.sets.elderflame.*;
import xyz.devvydont.smprpg.items.blueprints.sets.emberclad.*;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.emerald.EmeraldLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.exiled.ExiledAxe;
import xyz.devvydont.smprpg.items.blueprints.sets.exiled.ExiledCrossbow;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.*;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.holomoku.*;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.nocturnum.*;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.ruination.*;
import xyz.devvydont.smprpg.items.blueprints.sets.fishing.xenohunter.*;
import xyz.devvydont.smprpg.items.blueprints.sets.forsaken.*;
import xyz.devvydont.smprpg.items.blueprints.sets.gold.GoldHatchet;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.imperium.ImperiumLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.inferno.*;
import xyz.devvydont.smprpg.items.blueprints.sets.infinity.*;
import xyz.devvydont.smprpg.items.blueprints.sets.iron.IronBow;
import xyz.devvydont.smprpg.items.blueprints.sets.iron.IronHatchet;
import xyz.devvydont.smprpg.items.blueprints.sets.mithril.*;
import xyz.devvydont.smprpg.items.blueprints.sets.mystbloom.*;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.mystic.LuxeLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.cultivator.CultivatorBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.cultivator.CultivatorChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.cultivator.CultivatorHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.cultivator.CultivatorLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.neptune.*;
import xyz.devvydont.smprpg.items.blueprints.sets.netherite.NetheriteBow;
import xyz.devvydont.smprpg.items.blueprints.sets.netherite.NetheriteHatchet;
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
import xyz.devvydont.smprpg.items.blueprints.sets.rosegold.*;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraBoots;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraChestplate;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraHelmet;
import xyz.devvydont.smprpg.items.blueprints.sets.sakura.SakuraLeggings;
import xyz.devvydont.smprpg.items.blueprints.sets.silver.*;
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
import xyz.devvydont.smprpg.items.blueprints.sets.steel.*;
import xyz.devvydont.smprpg.items.blueprints.sets.tin.*;
import xyz.devvydont.smprpg.items.blueprints.sets.titanium.*;
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
import xyz.devvydont.smprpg.items.blueprints.sets.wood.WoodHatchet;
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

    SMALL_COIN_PURSE("Small Coin Purse", Material.LEATHER, ItemRarity.COMMON, WalletBlueprint.class),
    MEDIUM_COIN_PURSE("Medium Coin Purse", Material.LEATHER, ItemRarity.UNCOMMON, WalletBlueprint.class),
    LARGE_COIN_PURSE("Large Coin Purse", Material.LEATHER, ItemRarity.RARE, WalletBlueprint.class),
    GIGANTIC_COIN_PURSE("Gigantic Coin Purse", Material.LEATHER, ItemRarity.EPIC, WalletBlueprint.class),
    COLOSSAL_COIN_PURSE("Colossal Coin Purse", Material.LEATHER, ItemRarity.LEGENDARY, WalletBlueprint.class),

    SMALL_POCKET_COMPRESSOR("Small Pocket Compressor", Material.NETHER_BRICK, ItemRarity.COMMON, true, PocketCompressorBlueprint.class),
    MEDIUM_POCKET_COMPRESSOR("Medium Pocket Compressor", Material.NETHER_BRICK, ItemRarity.UNCOMMON, true, PocketCompressorBlueprint.class),
    LARGE_POCKET_COMPRESSOR("Large Pocket Compressor", Material.NETHER_BRICK, ItemRarity.RARE, true, PocketCompressorBlueprint.class),
    GIGANTIC_POCKET_COMPRESSOR("Gigantic Pocket Compressor", Material.NETHER_BRICK, ItemRarity.EPIC, true, PocketCompressorBlueprint.class),
    COLOSSAL_POCKET_COMPRESSOR("Colossal Pocket Compressor", Material.NETHER_BRICK, ItemRarity.LEGENDARY, true, PocketCompressorBlueprint.class),

    // DYNAMIC TOOLS
    // DRILL("Mining Drill", Material.PRISMARINE_SHARD, ItemDrill.class),
    // SMALL_FUEL_TANK("Small Fuel Tank",   Material.PRISMARINE_SHARD,   ItemRarity.COMMON,    SmallFuelTank.class),
    // MEDIUM_FUEL_TANK("Medium Fuel Tank", Material.PRISMARINE_SHARD,   ItemRarity.UNCOMMON,  MediumFuelTank.class),
    // LARGE_FUEL_TANK("Large Fuel Tank",   Material.PRISMARINE_SHARD,   ItemRarity.RARE,      LargeFuelTank.class),

    // AUGMENT STONES
    COMMON_REPAIR_CORE("Common Repair Core", Material.HEAVY_CORE, ItemRarity.COMMON, RepairCore.class),
    UNCOMMON_REPAIR_CORE("Uncommon Repair Core", Material.HEAVY_CORE, ItemRarity.UNCOMMON, RepairCore.class),
    RARE_REPAIR_CORE("Rare Repair Core", Material.HEAVY_CORE, ItemRarity.RARE, RepairCore.class),
    EPIC_REPAIR_CORE("Epic Repair Core", Material.HEAVY_CORE, ItemRarity.EPIC, RepairCore.class),
    LEGENDARY_REPAIR_CORE("Legendary Repair Core", Material.HEAVY_CORE, ItemRarity.LEGENDARY, RepairCore.class),
    RECOMBOBULATOR("Recombobulator 3000", ItemRarity.LEGENDARY, Recombobulator.class),

    // COPPER SET
    COPPER_BOW(    "Copper Bow",      Material.BOW,            CopperBow.class),

    // SILVER SET
    RAW_SILVER("Raw Silver",                     Material.NETHER_BRICK,       ItemRarity.COMMON, RawSilver.class),
    SILVER_ORE("Silver Ore",                     Material.POISONOUS_POTATO,        ItemRarity.COMMON, CraftEngineBlueprint.class),
    DEEPSLATE_SILVER_ORE("Deepslate Silver Ore", Material.POISONOUS_POTATO,        ItemRarity.COMMON, CraftEngineBlueprint.class),
    RAW_SILVER_BLOCK("Block of Raw Silver",      Material.POISONOUS_POTATO,        ItemRarity.COMMON, RawSilverFamilyBlueprint.class),
    ENCHANTED_RAW_SILVER("Enchanted Raw Silver",      Material.RAW_IRON,        ItemRarity.RARE, true, RawSilverFamilyBlueprint.class),

    SILVER_NUGGET("Silver Nugget",                        Material.IRON_NUGGET,       ItemRarity.COMMON,    CraftEngineBlueprint.class),
    SILVER_INGOT("Silver Ingot",                          Material.IRON_INGOT,        ItemRarity.COMMON,    SilverIngot.class),
    SILVER_BLOCK("Block of Silver",                       Material.NETHER_BRICK,      ItemRarity.UNCOMMON,  SilverFamilyBlueprint.class),
    ENCHANTED_SILVER("Enchanted Silver",                  Material.IRON_INGOT,        ItemRarity.RARE,      true, SilverFamilyBlueprint.class),
    ENCHANTED_SILVER_BLOCK("Enchanted Block of Silver",   Material.NETHER_BRICK,      ItemRarity.EPIC,      true, SilverFamilyBlueprint.class),
    SILVER_SINGULARITY("Silver Singularity",              Material.IRON_INGOT,        ItemRarity.LEGENDARY, true, SilverFamilyBlueprint.class),

    SILVER_PICKAXE("Silver Pickaxe",  Material.IRON_PICKAXE, SilverPickaxe.class),
    SILVER_AXE(    "Silver Axe",      Material.IRON_AXE,     SilverAxe.class),
    SILVER_HOE(    "Silver Hoe",      Material.IRON_HOE,     SilverHoe.class),
    SILVER_SHOVEL( "Silver Shovel",   Material.IRON_SHOVEL,  SilverShovel.class),
    SILVER_SWORD(  "Silver Sword",    Material.IRON_SWORD,   SilverSword.class),
    SILVER_HATCHET("Silver Hatchet",  Material.IRON_AXE,      ItemRarity.COMMON,   SilverHatchet.class),
    SILVER_BOW(    "Silver Bow",      Material.BOW,            SilverBow.class),
    SILVER_SPEAR(     "Silver Spear",    Material.IRON_SPEAR,   ItemRarity.COMMON,   SilverSpear.class),

    SILVER_HELMET(    "Silver Helmet",     Material.IRON_HELMET,     SilverHelmet.class),
    SILVER_CHESTPLATE("Silver Chestplate", Material.IRON_CHESTPLATE, SilverChestplate.class),
    SILVER_LEGGINGS(  "Silver Leggings",   Material.IRON_LEGGINGS,   SilverLeggings.class),
    SILVER_BOOTS(     "Silver Boots",      Material.IRON_BOOTS,      SilverBoots.class),

    // TIN SET
    RAW_TIN("Raw Tin",                      Material.NETHER_BRICK,         ItemRarity.COMMON, RawTin.class),
    TIN_ORE("Tin Ore",                      Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    DEEPSLATE_TIN_ORE("Deepslate Tin Ore",  Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    RAW_TIN_BLOCK("Block of Raw Tin",       Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_RAW_TIN("Enchanted Raw Tin",  Material.RAW_IRON,         ItemRarity.RARE,   true, RawTinFamilyBlueprint.class),

    TIN_INGOT("Tin Ingot",                 Material.NETHER_BRICK,     ItemRarity.COMMON, TinIngot.class),
    TIN_BLOCK("Block of Tin",              Material.NETHER_BRICK,        ItemRarity.UNCOMMON, TinFamilyBlueprint.class),
    ENCHANTED_TIN("Enchanted Tin",                  Material.IRON_INGOT,        ItemRarity.RARE,      true, TinFamilyBlueprint.class),
    ENCHANTED_TIN_BLOCK("Enchanted Block of Tin",   Material.POISONOUS_POTATO,  ItemRarity.EPIC,      true, TinFamilyBlueprint.class),
    TIN_SINGULARITY("Tin Singularity",              Material.IRON_INGOT,        ItemRarity.LEGENDARY, true, TinFamilyBlueprint.class),

    TIN_PICKAXE("Tin Pickaxe",  Material.IRON_PICKAXE, TinPickaxe.class),
    TIN_AXE(    "Tin Axe",      Material.IRON_AXE,     TinAxe.class),
    TIN_HOE(    "Tin Hoe",      Material.IRON_HOE,     TinHoe.class),
    TIN_SHOVEL( "Tin Shovel",   Material.IRON_SHOVEL,  TinShovel.class),
    TIN_SWORD(  "Tin Sword",    Material.IRON_SWORD,   TinSword.class),
    TIN_HATCHET("Tin Hatchet",  Material.IRON_AXE,     ItemRarity.COMMON,   TinHatchet.class),
    TIN_BOW(    "Tin Bow",      Material.BOW,          TinBow.class),
    TIN_SPEAR(  "Tin Spear",    Material.IRON_SPEAR,   ItemRarity.COMMON,   TinSpear.class),

    TIN_HELMET(    "Tin Helmet",     Material.IRON_HELMET,     TinHelmet.class),
    TIN_CHESTPLATE("Tin Chestplate", Material.IRON_CHESTPLATE, TinChestplate.class),
    TIN_LEGGINGS(  "Tin Leggings",   Material.IRON_LEGGINGS,   TinLeggings.class),
    TIN_BOOTS(     "Tin Boots",      Material.IRON_BOOTS,      TinBoots.class),

    // BRONZE SET
    BRONZE_INGOT("Bronze Ingot",                 Material.COPPER_INGOT,   ItemRarity.COMMON,   BronzeIngot.class),
    BRONZE_BLOCK("Block of Bronze",              Material.POISONOUS_POTATO,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),

    BRONZE_PICKAXE("Bronze Pickaxe",  Material.WOODEN_PICKAXE, BronzePickaxe.class),
    BRONZE_AXE(    "Bronze Axe",      Material.WOODEN_AXE,     BronzeAxe.class),
    BRONZE_HOE(    "Bronze Hoe",      Material.WOODEN_HOE,     BronzeHoe.class),
    BRONZE_SHOVEL( "Bronze Shovel",   Material.WOODEN_SHOVEL,  BronzeShovel.class),
    BRONZE_SWORD(  "Bronze Sword",    Material.WOODEN_SWORD,   BronzeSword.class),
    BRONZE_HATCHET("Bronze Hatchet",  Material.WOODEN_AXE,      ItemRarity.COMMON,   BronzeHatchet.class),
    BRONZE_BOW(    "Bronze Bow",      Material.BOW,            BronzeBow.class),
    BRONZE_SPEAR(  "Bronze Spear",    Material.IRON_SPEAR,   ItemRarity.COMMON,   BronzeSpear.class),

    BRONZE_HELMET(    "Bronze Helmet",     Material.LEATHER_HELMET,     BronzeHelmet.class),
    BRONZE_CHESTPLATE("Bronze Chestplate", Material.LEATHER_CHESTPLATE, BronzeChestplate.class),
    BRONZE_LEGGINGS(  "Bronze Leggings",   Material.LEATHER_LEGGINGS,   BronzeLeggings.class),
    BRONZE_BOOTS(     "Bronze Boots",      Material.LEATHER_BOOTS,      BronzeBoots.class),

    // STEEL SET
    STEEL_INGOT("Steel Ingot", Material.IRON_INGOT, ItemRarity.UNCOMMON, SteelIngot.class),
    STEEL_BLOCK(     "Block of Steel",      Material.POISONOUS_POTATO,     ItemRarity.RARE, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_STEEL("Enchanted Steel", Material.IRON_INGOT, ItemRarity.EPIC, true, SteelFamilyBlueprint.class),
    ENCHANTED_STEEL_BLOCK("Enchanted Steel Block", Material.POISONOUS_POTATO, ItemRarity.LEGENDARY, true, SteelFamilyBlueprint.class),
    STEEL_SINGULARITY("Steel Singularity", Material.IRON_INGOT, ItemRarity.MYTHIC, true, SteelFamilyBlueprint.class),

    STEEL_TOOL_SHAFT("Steel Tool Shaft", Material.STICK, ItemRarity.UNCOMMON, SteelToolShaft.class),
    // STEEL_DRILL_HEAD("Steel Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, SteelDrillHead.class),
    // STEEL_DRILL_BASE("Steel Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, SteelDrillBase.class),

    STEEL_PICKAXE("Steel Pickaxe",      Material.IRON_PICKAXE,      ItemRarity.UNCOMMON,   SteelPickaxe.class),
    STEEL_AXE("Steel Axe",              Material.IRON_AXE,          ItemRarity.UNCOMMON,   SteelAxe.class),
    STEEL_HATCHET("Steel Hatchet",      Material.IRON_AXE,          ItemRarity.UNCOMMON,   SteelHatchet.class),
    STEEL_SWORD("Steel Sword",          Material.IRON_SWORD,        ItemRarity.UNCOMMON,   SteelSword.class),
    STEEL_HOE("Steel Hoe",              Material.IRON_HOE,          ItemRarity.UNCOMMON,   SteelHoe.class),
    STEEL_SHOVEL("Steel Shovel",        Material.IRON_SHOVEL,       ItemRarity.UNCOMMON,   SteelShovel.class),
    STEEL_BOW(  "Steel Bow",            Material.BOW,               ItemRarity.UNCOMMON,   SteelBow.class),
    STEEL_SPEAR(  "Steel Spear",        Material.IRON_SPEAR,        ItemRarity.UNCOMMON,   SteelSpear.class),

    STEEL_HELMET(    "Steel Helmet",     Material.IRON_HELMET,     SteelHelmet.class),
    STEEL_CHESTPLATE("Steel Chestplate", Material.IRON_CHESTPLATE, SteelChestplate.class),
    STEEL_LEGGINGS(  "Steel Leggings",   Material.IRON_LEGGINGS,   SteelLeggings.class),
    STEEL_BOOTS(     "Steel Boots",      Material.IRON_BOOTS,      SteelBoots.class),

    // ROSE GOLD SET
    ROSE_GOLD_INGOT("Rose Gold Ingot",                 Material.IRON_INGOT,     ItemRarity.UNCOMMON,   RoseGoldIngot.class),
    ROSE_GOLD_BLOCK("Block of Rose Gold",              Material.POISONOUS_POTATO,        ItemRarity.RARE, CraftEngineBlueprint.class),

    // ROSE_GOLD_DRILL_HEAD("Rose Gold Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, RoseGoldDrillHead.class),
    // ROSE_GOLD_DRILL_BASE("Rose Gold Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, RoseGoldDrillBase.class),

    ROSE_GOLD_PICKAXE("Rose Gold Pickaxe",  Material.GOLDEN_PICKAXE, ItemRarity.UNCOMMON, RoseGoldPickaxe.class),
    ROSE_GOLD_AXE(    "Rose Gold Axe",      Material.GOLDEN_AXE,     ItemRarity.UNCOMMON, RoseGoldAxe.class),
    ROSE_GOLD_HOE(    "Rose Gold Hoe",      Material.GOLDEN_HOE,     ItemRarity.UNCOMMON, RoseGoldHoe.class),
    ROSE_GOLD_SHOVEL( "Rose Gold Shovel",   Material.GOLDEN_SHOVEL,  ItemRarity.UNCOMMON, RoseGoldShovel.class),
    ROSE_GOLD_SWORD(  "Rose Gold Sword",    Material.GOLDEN_SWORD,   ItemRarity.UNCOMMON, RoseGoldSword.class),
    ROSE_GOLD_HATCHET("Rose Gold Hatchet",  Material.GOLDEN_AXE,     ItemRarity.UNCOMMON, RoseGoldHatchet.class),
    ROSE_GOLD_BOW(    "Rose Gold Bow",      Material.BOW,            ItemRarity.UNCOMMON, RoseGoldBow.class),
    ROSE_GOLD_SPEAR(    "Rose Gold Spear",    Material.IRON_SPEAR,     ItemRarity.UNCOMMON, RoseGoldSpear.class),

    ROSE_GOLD_HELMET(    "Rose Gold Helmet",     Material.GOLDEN_HELMET,     ItemRarity.UNCOMMON, RoseGoldHelmet.class),
    ROSE_GOLD_CHESTPLATE("Rose Gold Chestplate", Material.GOLDEN_CHESTPLATE, ItemRarity.UNCOMMON,RoseGoldChestplate.class),
    ROSE_GOLD_LEGGINGS(  "Rose Gold Leggings",   Material.GOLDEN_LEGGINGS,   ItemRarity.UNCOMMON, RoseGoldLeggings.class),
    ROSE_GOLD_BOOTS(     "Rose Gold Boots",      Material.GOLDEN_BOOTS,      ItemRarity.UNCOMMON, RoseGoldBoots.class),

    // MITHRIL SET
    RAW_MITHRIL("Raw Mithril",                     Material.NETHER_BRICK,     ItemRarity.UNCOMMON,   CraftEngineBlueprint.class),
    SPARSE_MITHRIL_ORE("Sparse Mithril Ore",       Material.POISONOUS_POTATO,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    MITHRIL_ORE("Mithril Ore",                     Material.POISONOUS_POTATO,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    DENSE_MITHRIL_ORE("Dense Mithril Ore",         Material.POISONOUS_POTATO,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    RAW_MITHRIL_BLOCK("Block of Raw Mithril",      Material.POISONOUS_POTATO,        ItemRarity.RARE, CraftEngineBlueprint.class),

    MITHRIL_INGOT("Mithril Ingot",                 Material.NETHER_BRICK,     ItemRarity.UNCOMMON,   MithrilIngot.class),
    MITHRIL_BLOCK("Block of Mithril",              Material.POISONOUS_POTATO,        ItemRarity.RARE, MithrilFamilyBlueprint.class),
    ENCHANTED_MITHRIL("Enchanted Mithril",                  Material.IRON_INGOT,        ItemRarity.EPIC,      true, MithrilFamilyBlueprint.class),
    ENCHANTED_MITHRIL_BLOCK("Enchanted Block of Mithril",   Material.POISONOUS_POTATO,  ItemRarity.LEGENDARY,      true, MithrilFamilyBlueprint.class),
    MITHRIL_SINGULARITY("Mithril Singularity",              Material.IRON_INGOT,        ItemRarity.MYTHIC, true, MithrilFamilyBlueprint.class),

    // MITHRIL_DRILL_HEAD("Mithril Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, MithrilDrillHead.class),
    // MITHRIL_DRILL_BASE("Mithril Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, MithrilDrillBase.class),

    MITHRIL_PICKAXE("Mithril Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.UNCOMMON, MithrilPickaxe.class),
    MITHRIL_AXE(    "Mithril Axe",      Material.DIAMOND_AXE,     ItemRarity.UNCOMMON, MithrilAxe.class),
    MITHRIL_HOE(    "Mithril Hoe",      Material.DIAMOND_HOE,     ItemRarity.UNCOMMON, MithrilHoe.class),
    MITHRIL_SHOVEL( "Mithril Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.UNCOMMON, MithrilShovel.class),
    MITHRIL_SWORD(  "Mithril Sword",    Material.DIAMOND_SWORD,   ItemRarity.UNCOMMON, MithrilSword.class),
    MITHRIL_HATCHET("Mithril Hatchet",  Material.DIAMOND_AXE,     ItemRarity.UNCOMMON, MithrilHatchet.class),
    MITHRIL_BOW(    "Mithril Bow",      Material.BOW,             ItemRarity.UNCOMMON, MithrilBow.class),
    MITHRIL_SPEAR(    "Mithril Spear",    Material.IRON_SPEAR,     ItemRarity.UNCOMMON, MithrilSpear.class),
    MITHRIL_STAFF(    "Mithril Staff",  Material.STICK,           ItemRarity.RARE,     MithrilStaff.class),

    MITHRIL_HELMET(    "Mithril Helmet",     Material.DIAMOND_HELMET,     ItemRarity.UNCOMMON, MithrilHelmet.class),
    MITHRIL_CHESTPLATE("Mithril Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.UNCOMMON,MithrilChestplate.class),
    MITHRIL_LEGGINGS(  "Mithril Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.UNCOMMON, MithrilLeggings.class),
    MITHRIL_BOOTS(     "Mithril Boots",      Material.DIAMOND_BOOTS,      ItemRarity.UNCOMMON, MithrilBoots.class),

    // TITANIUM SET
    RAW_TITANIUM("Raw Titanium",                     Material.RAW_IRON,     ItemRarity.RARE,   RawTitanium.class),
    TITANIUM_ORE("Titanium Ore",                     Material.POISONOUS_POTATO,        ItemRarity.RARE, CraftEngineBlueprint.class),
    RAW_TITANIUM_BLOCK("Block of Raw Titanium",      Material.POISONOUS_POTATO,        ItemRarity.EPIC, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_RAW_TITANIUM("Enchanted Raw Titanium",           Material.RAW_IRON,     ItemRarity.LEGENDARY,   false, RawTitaniumFamilyBlueprint.class),

    TITANIUM_INGOT("Titanium Ingot",                 Material.IRON_INGOT,     ItemRarity.RARE,   TitaniumIngot.class),
    TITANIUM_BLOCK("Block of Titanium",              Material.POISONOUS_POTATO,        ItemRarity.EPIC, TitaniumFamilyBlueprint.class),
    ENCHANTED_TITANIUM("Enchanted Titanium",                  Material.IRON_INGOT,        ItemRarity.LEGENDARY,      true, TitaniumFamilyBlueprint.class),
    ENCHANTED_TITANIUM_BLOCK("Enchanted Block of Titanium",   Material.POISONOUS_POTATO,  ItemRarity.MYTHIC,      true, TitaniumFamilyBlueprint.class),
    TITANIUM_SINGULARITY("Titanium Singularity",              Material.IRON_INGOT,        ItemRarity.DIVINE, true, TitaniumFamilyBlueprint.class),

    // TITANIUM_DRILL_HEAD("Titanium Drill Head", Material.PRISMARINE_SHARD, ItemRarity.RARE, TitaniumDrillHead.class),
    // TITANIUM_DRILL_BASE("Titanium Drill Base", Material.PRISMARINE_SHARD, ItemRarity.RARE, TitaniumDrillBase.class),

    TITANIUM_PICKAXE("Titanium Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.RARE, TitaniumPickaxe.class),
    TITANIUM_AXE(    "Titanium Axe",      Material.DIAMOND_AXE,     ItemRarity.RARE, TitaniumAxe.class),
    TITANIUM_HOE(    "Titanium Hoe",      Material.DIAMOND_HOE,     ItemRarity.RARE, TitaniumHoe.class),
    TITANIUM_SHOVEL( "Titanium Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.RARE, TitaniumShovel.class),
    TITANIUM_SWORD(  "Titanium Sword",    Material.DIAMOND_SWORD,   ItemRarity.RARE, TitaniumSword.class),
    TITANIUM_HATCHET("Titanium Hatchet",  Material.DIAMOND_AXE,     ItemRarity.RARE,   TitaniumHatchet.class),
    TITANIUM_BOW(    "Titanium Bow",      Material.BOW,             ItemRarity.RARE, TitaniumBow.class),
    TITANIUM_SPEAR(  "Titanium Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, TitaniumSpear.class),

    TITANIUM_HELMET(    "Titanium Helmet",     Material.DIAMOND_HELMET,     ItemRarity.RARE, TitaniumHelmet.class),
    TITANIUM_CHESTPLATE("Titanium Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.RARE,TitaniumChestplate.class),
    TITANIUM_LEGGINGS(  "Titanium Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, TitaniumLeggings.class),
    TITANIUM_BOOTS(     "Titanium Boots",      Material.DIAMOND_BOOTS,      ItemRarity.RARE, TitaniumBoots.class),

    // ADAMANTIUM SET
    RAW_ADAMANTIUM("Raw Adamantium",                     Material.RAW_IRON,     ItemRarity.RARE,   CraftEngineBlueprint.class),
    ADAMANTIUM_ORE("Adamantium Ore",                     Material.NETHER_BRICK,        ItemRarity.RARE, CraftEngineBlueprint.class),
    RAW_ADAMANTIUM_BLOCK("Block of Raw Adamantium",      Material.NETHER_BRICK,        ItemRarity.EPIC, CraftEngineBlueprint.class),

    ADAMANTIUM_INGOT("Adamantium Ingot",                 Material.IRON_INGOT,     ItemRarity.RARE,   AdamantiumIngot.class),
    ADAMANTIUM_BLOCK("Block of Adamantium",              Material.NETHER_BRICK,        ItemRarity.EPIC, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_ADAMANTIUM("Enchanted Adamantium",                  Material.IRON_INGOT,        ItemRarity.LEGENDARY,      true, AdamantiumFamilyBlueprint.class),
    ENCHANTED_ADAMANTIUM_BLOCK("Enchanted Block of Adamantium",   Material.NETHER_BRICK,  ItemRarity.MYTHIC,      true, AdamantiumFamilyBlueprint.class),
    ADAMANTIUM_SINGULARITY("Adamantium Singularity",              Material.IRON_INGOT,        ItemRarity.DIVINE, true, AdamantiumFamilyBlueprint.class),

    // ADAMANTIUM_DRILL_HEAD("Adamantium Drill Head", Material.PRISMARINE_SHARD, ItemRarity.RARE, AdamantiumDrillHead.class),
    // ADAMANTIUM_DRILL_BASE("Adamantium Drill Base", Material.PRISMARINE_SHARD, ItemRarity.RARE, AdamantiumDrillBase.class),

    ADAMANTIUM_PICKAXE("Adamantium Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.RARE, AdamantiumPickaxe.class),
    ADAMANTIUM_AXE(    "Adamantium Axe",      Material.DIAMOND_AXE,     ItemRarity.RARE, AdamantiumAxe.class),
    ADAMANTIUM_HOE(    "Adamantium Hoe",      Material.DIAMOND_HOE,     ItemRarity.RARE, AdamantiumHoe.class),
    ADAMANTIUM_SHOVEL( "Adamantium Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.RARE, AdamantiumShovel.class),
    ADAMANTIUM_SWORD(  "Adamantium Sword",    Material.DIAMOND_SWORD,   ItemRarity.RARE, AdamantiumSword.class),
    ADAMANTIUM_HATCHET("Adamantium Hatchet",  Material.DIAMOND_AXE,     ItemRarity.RARE,   AdamantiumHatchet.class),
    ADAMANTIUM_BOW(    "Adamantium Bow",      Material.BOW,             ItemRarity.RARE, AdamantiumBow.class),
    ADAMANTIUM_SPEAR(  "Adamantium Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, AdamantiumSpear.class),

    ADAMANTIUM_HELMET(    "Adamantium Helmet",     Material.DIAMOND_HELMET,     ItemRarity.RARE, AdamantiumHelmet.class),
    ADAMANTIUM_CHESTPLATE("Adamantium Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.RARE, AdamantiumChestplate.class),
    ADAMANTIUM_LEGGINGS(  "Adamantium Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, AdamantiumLeggings.class),
    ADAMANTIUM_BOOTS(     "Adamantium Boots",      Material.DIAMOND_BOOTS,      ItemRarity.RARE, AdamantiumBoots.class),

    // SULFUR
    SULFUR_ORE("Sulfur Ore",                     Material.NETHER_BRICK,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    SULFUR_TREATED_TOOL_SHAFT(    "Sulfur Treated Shaft",      Material.STICK,     ItemRarity.UNCOMMON, SulfurToolShaft.class),

    SULFUR(    "Sulfur",                         Material.GUNPOWDER,               ItemRarity.UNCOMMON, Sulfur.class),
    SULFUR_BLOCK("Block of Sulfur",              Material.NETHER_BRICK,        ItemRarity.RARE, SulfurFamilyBlueprint.class),
    ENCHANTED_SULFUR("Enchanted Sulfur",                  Material.GUNPOWDER,        ItemRarity.EPIC,      true, SulfurFamilyBlueprint.class),
    ENCHANTED_SULFUR_BLOCK("Enchanted Block of Sulfur",   Material.POISONOUS_POTATO,  ItemRarity.LEGENDARY,      true, SulfurFamilyBlueprint.class),
    SULFUR_SINGULARITY("Sulfur Singularity",              Material.GUNPOWDER,        ItemRarity.MYTHIC, true, SulfurFamilyBlueprint.class),

    // ONYX
    ONYX(    "Onyx",     Material.COAL,      ItemRarity.RARE, false, 500, "materials"),
    ONYX_ORE("Onyx Ore",                     Material.NETHER_BRICK,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    ONYX_BLOCK("Block of Onyx",              Material.NETHER_BRICK,        ItemRarity.RARE, CraftEngineBlueprint.class),

    // TUNGSTEN SET
    RAW_TUNGSTEN("Raw Tungsten",                     Material.RAW_IRON,                ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    TUNGSTEN_ORE("Tungsten Ore",                     Material.NETHER_BRICK,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    RAW_TUNGSTEN_BLOCK("Block of Raw Tungsten",      Material.NETHER_BRICK,        ItemRarity.RARE, CraftEngineBlueprint.class),

    TUNGSTEN_INGOT("Tungsten Ingot",                 Material.IRON_INGOT,              ItemRarity.UNCOMMON, TungstenIngot.class),
    TUNGSTEN_BLOCK("Block of Tungsten",              Material.POISONOUS_POTATO,        ItemRarity.RARE, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_TUNGSTEN("Enchanted Tungsten",                  Material.IRON_INGOT,        ItemRarity.EPIC,      true, TungstenFamilyBlueprint.class),
    ENCHANTED_TUNGSTEN_BLOCK("Enchanted Block of Tungsten",   Material.NETHER_BRICK,  ItemRarity.LEGENDARY,      true, TungstenFamilyBlueprint.class),
    TUNGSTEN_SINGULARITY("Tungsten Singularity",              Material.IRON_INGOT,        ItemRarity.MYTHIC, true, TungstenFamilyBlueprint.class),

    // TUNGSTEN_DRILL_HEAD("Tungsten Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, AdamantiumDrillHead.class),
    // TUNGSTEN_DRILL_BASE("Tungsten Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, AdamantiumDrillBase.class),

    TUNGSTEN_PICKAXE("Tungsten Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.UNCOMMON, TungstenPickaxe.class),
    TUNGSTEN_AXE(    "Tungsten Axe",      Material.DIAMOND_AXE,     ItemRarity.UNCOMMON, TungstenAxe.class),
    TUNGSTEN_HOE(    "Tungsten Hoe",      Material.DIAMOND_HOE,     ItemRarity.UNCOMMON, TungstenHoe.class),
    TUNGSTEN_SHOVEL( "Tungsten Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.UNCOMMON, TungstenShovel.class),
    TUNGSTEN_SWORD(  "Tungsten Sword",    Material.DIAMOND_SWORD,   ItemRarity.UNCOMMON, TungstenSword.class),
    TUNGSTEN_HATCHET("Tungsten Hatchet",  Material.DIAMOND_AXE,     ItemRarity.UNCOMMON, TungstenHatchet.class),
    TUNGSTEN_BOW(    "Tungsten Bow",      Material.BOW,             ItemRarity.UNCOMMON, TungstenBow.class),
    TUNGSTEN_SPEAR(  "Tungsten Spear",    Material.IRON_SPEAR,      ItemRarity.UNCOMMON, TungstenSpear.class),

    TUNGSTEN_HELMET(    "Tungsten Helmet",     Material.DIAMOND_HELMET,     ItemRarity.UNCOMMON, TungstenHelmet.class),
    TUNGSTEN_CHESTPLATE("Tungsten Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.UNCOMMON, TungstenChestplate.class),
    TUNGSTEN_LEGGINGS(  "Tungsten Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.UNCOMMON, TungstenLeggings.class),
    TUNGSTEN_BOOTS(     "Tungsten Boots",      Material.DIAMOND_BOOTS,      ItemRarity.UNCOMMON, TungstenBoots.class),

    // COBALT SET
    RAW_COBALT("Raw Cobalt",                     Material.RAW_IRON,                ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    COBALT_ORE("Cobalt Ore",                     Material.NETHER_BRICK,        ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    RAW_COBALT_BLOCK("Block of Raw Cobalt",      Material.NETHER_BRICK,        ItemRarity.RARE, CraftEngineBlueprint.class),

    COBALT_INGOT("Cobalt Ingot",                 Material.IRON_INGOT,              ItemRarity.RARE, CobaltIngot.class),
    COBALT_BLOCK("Block of Cobalt",              Material.NETHER_BRICK,        ItemRarity.EPIC, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_COBALT("Enchanted Cobalt",                  Material.IRON_INGOT,        ItemRarity.LEGENDARY,      true, CobaltFamilyBlueprint.class),
    ENCHANTED_COBALT_BLOCK("Enchanted Block of Cobalt",   Material.POISONOUS_POTATO,  ItemRarity.MYTHIC,      true, CobaltFamilyBlueprint.class),
    COBALT_SINGULARITY("Cobalt Singularity",              Material.IRON_INGOT,        ItemRarity.DIVINE, true, CobaltFamilyBlueprint.class),

    // COBALT_DRILL_HEAD("Cobalt Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, CobaltDrillHead.class),
    // COBALT_DRILL_BASE("Cobalt Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, CobaltDrillBASE.class),

    COBALT_PICKAXE("Cobalt Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.RARE, CobaltPickaxe.class),
    COBALT_AXE(    "Cobalt Axe",      Material.DIAMOND_AXE,     ItemRarity.RARE, CobaltAxe.class),
    COBALT_HOE(    "Cobalt Hoe",      Material.DIAMOND_HOE,     ItemRarity.RARE, CobaltHoe.class),
    COBALT_SHOVEL( "Cobalt Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.RARE, CobaltShovel.class),
    COBALT_SWORD(  "Cobalt Sword",    Material.DIAMOND_SWORD,   ItemRarity.RARE, CobaltSword.class),
    COBALT_HATCHET("Cobalt Hatchet",  Material.DIAMOND_AXE,     ItemRarity.RARE, CobaltHatchet.class),
    COBALT_BOW(    "Cobalt Bow",      Material.BOW,             ItemRarity.RARE, CobaltBow.class),
    COBALT_SPEAR(  "Cobalt Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, CobaltSpear.class),

    COBALT_HELMET(    "Cobalt Helmet",     Material.DIAMOND_HELMET,     ItemRarity.RARE, CobaltHelmet.class),
    COBALT_CHESTPLATE("Cobalt Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.RARE, CobaltChestplate.class),
    COBALT_LEGGINGS(  "Cobalt Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, CobaltLeggings.class),
    COBALT_BOOTS(     "Cobalt Boots",      Material.DIAMOND_BOOTS,      ItemRarity.RARE, CobaltBoots.class),

    // ORICHALCUM SET
    RAW_ORICHALCUM("Raw Orichalcum",                     Material.RAW_IRON,                ItemRarity.RARE, CraftEngineBlueprint.class),
    ORICHALCUM_ORE("Orichalcum Ore",                     Material.NETHER_BRICK,        ItemRarity.RARE, CraftEngineBlueprint.class),
    RAW_ORICHALCUM_BLOCK("Block of Raw Orichalcum",      Material.NETHER_BRICK,        ItemRarity.EPIC, CraftEngineBlueprint.class),

    ORICHALCUM_INGOT("Orichalcum Ingot",                 Material.IRON_INGOT,              ItemRarity.RARE, OrichalcumIngot.class),
    ORICHALCUM_BLOCK("Block of Orichalcum",              Material.NETHER_BRICK,        ItemRarity.EPIC, CraftEngineCompressibleBlueprint.class),
    ENCHANTED_ORICHALCUM("Enchanted Orichalcum",                  Material.IRON_INGOT,        ItemRarity.LEGENDARY,      true, OrichalcumFamilyBlueprint.class),
    ENCHANTED_ORICHALCUM_BLOCK("Enchanted Block of Orichalcum",   Material.POISONOUS_POTATO,  ItemRarity.MYTHIC,      true, OrichalcumFamilyBlueprint.class),
    ORICHALCUM_SINGULARITY("Orichalcum Singularity",              Material.IRON_INGOT,        ItemRarity.DIVINE, true, OrichalcumFamilyBlueprint.class),

    // ORICHALCUM_DRILL_HEAD("Orichalcum Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, OrichalcumDrillHead.class),
    // ORICHALCUM_DRILL_BASE("Orichalcum Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, OrichalcumDrillBase.class),

    ORICHALCUM_PICKAXE("Orichalcum Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.RARE, OrichalcumPickaxe.class),
    ORICHALCUM_AXE(    "Orichalcum Axe",      Material.DIAMOND_AXE,     ItemRarity.RARE, OrichalcumAxe.class),
    ORICHALCUM_HOE(    "Orichalcum Hoe",      Material.DIAMOND_HOE,     ItemRarity.RARE, OrichalcumHoe.class),
    ORICHALCUM_SHOVEL( "Orichalcum Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.RARE, OrichalcumShovel.class),
    ORICHALCUM_SWORD(  "Orichalcum Sword",    Material.DIAMOND_SWORD,   ItemRarity.RARE, OrichalcumSword.class),
    ORICHALCUM_HATCHET("Orichalcum Hatchet",  Material.DIAMOND_AXE,     ItemRarity.RARE, OrichalcumHatchet.class),
    ORICHALCUM_BOW(    "Orichalcum Bow",      Material.BOW,             ItemRarity.RARE, OrichalcumBow.class),
    ORICHALCUM_SPEAR(  "Orichalcum Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, OrichalcumSpear.class),

    ORICHALCUM_HELMET(    "Orichalcum Helmet",     Material.DIAMOND_HELMET,     ItemRarity.RARE, OrichalcumHelmet.class),
    ORICHALCUM_CHESTPLATE("Orichalcum Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.RARE, OrichalcumChestplate.class),
    ORICHALCUM_LEGGINGS(  "Orichalcum Leggings",   Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, OrichalcumLeggings.class),
    ORICHALCUM_BOOTS(     "Orichalcum Boots",      Material.DIAMOND_BOOTS,      ItemRarity.RARE, OrichalcumBoots.class),

    // WOODEN SET
    WOODEN_STAFF("Wooden Staff",                     Material.STICK,              ItemRarity.COMMON,   WoodStaff.class),

    // LEATHER SET
    LEATHER_CONICAL_HAT("Leather Conical Hat", Material.LEATHER_HELMET, ItemRarity.COMMON, LeatherConicalHat.class),

    // COBBLESTONE SET
    COBBLESTONE_HELMET("Cobblestone Helmet",         Material.LEATHER_HELMET,     ItemRarity.UNCOMMON, CobblestoneHelmet.class),
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
    AMETHYST_HELMET(    "Amethyst Helmet",     Material.IRON_HELMET   ,  ItemRarity.RARE,     AmethystHelmet.class),
    AMETHYST_CHESTPLATE("Amethyst Chestplate", Material.IRON_CHESTPLATE, ItemRarity.RARE,     AmethystChestplate.class),
    AMETHYST_LEGGINGS(  "Amethyst Leggings",   Material.IRON_LEGGINGS,   ItemRarity.RARE,     AmethystLeggings.class),
    AMETHYST_BOOTS(     "Amethyst Boots",      Material.IRON_BOOTS,      ItemRarity.RARE,     AmethystBoots.class),
    AMETHYST_STAFF(     "Amethyst Staff",      Material.STICK,           ItemRarity.UNCOMMON, AmethystStaff.class),

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

    // BREEZE/TRIAL CHAMBERS
    BREEZEBORNE_STAFF(    "Breezeborne Staff",  Material.STICK,           ItemRarity.EPIC,     BreezeborneStaff.class),

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

    // CULTIVATOR SET
    CULTIVATOR_HAT("Cultivator Hat",               Material.LEATHER_HELMET,     ItemRarity.RARE, CultivatorHelmet.class),
    CULTIVATOR_SHIRT("Cultivator Shirt",           Material.LEATHER_CHESTPLATE, ItemRarity.RARE, CultivatorChestplate.class),
    CULTIVATOR_OVERALLS("Cultivator Overalls",     Material.LEATHER_LEGGINGS,   ItemRarity.RARE, CultivatorLeggings.class),
    CULTIVATOR_BOOTS("Cultivator Boots",           Material.LEATHER_BOOTS,      ItemRarity.RARE, CultivatorBoots.class),

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

    // BOILING_PICKAXE(       "Boiling Pickaxe",        Material.NETHERITE_PICKAXE,        ItemRarity.EPIC, BoilingPickaxe.class),

    OBSIDIAN_TOOL_ROD("Obsidian Tool Rod", Material.STICK, ItemRarity.RARE, true, ObsidianToolRod.class),
    BOILING_INGOT("Boiling Ingot", Material.GOLD_INGOT, ItemRarity.RARE, true, BoilingIngot.class),

    INFERNO_ARROW("Inferno Arrow", Material.SPECTRAL_ARROW, ItemRarity.EPIC, InfernoArrow.class),

    // INFERNO
    INFERNO_HELMET("Inferno Helmet",         Material.GOLDEN_HELMET,     ItemRarity.LEGENDARY, InfernoHelmet.class),
    INFERNO_CHESTPLATE("Inferno Chestplate", Material.GOLDEN_CHESTPLATE, ItemRarity.LEGENDARY, InfernoChestplate.class),
    INFERNO_LEGGINGS("Inferno Leggings",     Material.GOLDEN_LEGGINGS,   ItemRarity.LEGENDARY, InfernoLeggings.class),
    INFERNO_BOOTS("Inferno Boots",           Material.GOLDEN_BOOTS,      ItemRarity.LEGENDARY, InfernoBoots.class),
    INFERNO_SABER("Inferno Saber",           Material.NETHERITE_SWORD,   ItemRarity.LEGENDARY, InfernoSaber.class),
    INFERNO_STAFF("Inferno Staff",           Material.BLAZE_ROD,   ItemRarity.LEGENDARY, InfernoStaff.class),
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
    DISPLACEMENT_MATRIX("Displacement Matrix",      Material.NETHER_STAR,     ItemRarity.RARE, true, 60_000, "materials"),
    WARP_CATALYST("Warp Catalyst", ItemRarity.EPIC, WarpCatalyst.class),

    // ELDERFLAME
    ELDERFLAME_HELMET(    "Elderflame Helmet",     Material.LEATHER_HELMET,    ItemRarity.LEGENDARY, ElderflameHelmet.class),
    ELDERFLAME_CHESTPLATE("Elderflame Wings",      Material.LEATHER_CHESTPLATE,              ItemRarity.LEGENDARY, ElderflameChestplate.class),
    ELDERFLAME_LEGGINGS(  "Elderflame Leggings",   Material.LEATHER_LEGGINGS,  ItemRarity.LEGENDARY, ElderflameLeggings.class),
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

    // ALL AROUND FISHING
    MURKY_HELMET(    "Murky Helmet",     Material.LEATHER_HELMET,     ItemRarity.COMMON, MurkySet.class),
    MURKY_CHESTPLATE("Murky Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.COMMON, MurkySet.class),
    MURKY_LEGGINGS(  "Murky Leggings",   Material.LEATHER_LEGGINGS,   ItemRarity.COMMON, MurkySet.class),
    MURKY_BOOTS(     "Murky Boots",      Material.LEATHER_BOOTS,      ItemRarity.COMMON, MurkySet.class),

    // SEA CREATURE FISHING
    MINNOW_SCALE("Minnow Scale", Material.LIGHT_GRAY_DYE, ItemRarity.COMMON, false, 2_000, "materials"),
    MINNOW_HELMET(    "Minnow Helmet",     Material.LEATHER_HELMET,     ItemRarity.COMMON, MinnowSet.class),
    MINNOW_CHESTPLATE("Minnow Chestplate", Material.LEATHER_CHESTPLATE, ItemRarity.COMMON, MinnowSet.class),
    MINNOW_LEGGINGS(  "Minnow Leggings",   Material.LEATHER_LEGGINGS,   ItemRarity.COMMON, MinnowSet.class),
    MINNOW_BOOTS(     "Minnow Boots",      Material.LEATHER_BOOTS,      ItemRarity.COMMON, MinnowSet.class),
    MINNOW_ROD("Minnow Rod", Material.FISHING_ROD, ItemRarity.COMMON, MinnowRod.class),

    HOLOMOKU_CREST("Holomoku Crest", Material.NAUTILUS_SHELL, ItemRarity.UNCOMMON, true, HolomokuCrest.class),
    HOLOMOKU_HELMET("Holomoku Helmet",         Material.DIAMOND_HELMET,     ItemRarity.UNCOMMON, HolomokuHelmet.class),
    HOLOMOKU_CHESTPLATE("Holomoku Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.UNCOMMON, HolomokuChestplate.class),
    HOLOMOKU_LEGGINGS("Holomoku Leggings",     Material.DIAMOND_LEGGINGS,   ItemRarity.UNCOMMON, HolomokuLeggings.class),
    HOLOMOKU_BOOTS("Holomoku Boots",           Material.DIAMOND_BOOTS,      ItemRarity.UNCOMMON, HolomokuBoots.class),
    HOLOMOKU_ROD("Holomoku Rod",               Material.FISHING_ROD,        ItemRarity.UNCOMMON, HolomokuRod.class),

    HEXED_CLOTH("Hexed Cloth", Material.DRIED_KELP, ItemRarity.UNCOMMON, true, HexedCloth.class),
    RAW_TRIDENTITE_CHUNK("Raw Tridentite Chunk", Material.CYAN_DYE, ItemRarity.RARE, true, TridentiteChunk.class),
    TRIDENTITE("Tridentite", Material.IRON_INGOT, ItemRarity.RARE, Tridentite.class),
    RUINATION_HELMET("Ruination Helmet",         Material.DIAMOND_HELMET,     ItemRarity.RARE, RuinationHelmet.class),
    RUINATION_CHESTPLATE("Ruination Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.RARE, RuinationChestplate.class),
    RUINATION_LEGGINGS("Ruination Leggings",     Material.DIAMOND_LEGGINGS,   ItemRarity.RARE, RuinationLeggings.class),
    RUINATION_BOOTS("Ruination Boots",           Material.DIAMOND_BOOTS,      ItemRarity.RARE, RuinationBoots.class),
    RUINATION_ROD("Ruination Rod",               Material.FISHING_ROD,        ItemRarity.RARE, RuinationRod.class),

    MIDNIGHT_HIDE("Midnight Hide", Material.LEATHER, ItemRarity.UNCOMMON, true, 15_000),
    DEEP_SEA_BARNACLE("Deep Sea Barnacle", Material.PHANTOM_MEMBRANE, ItemRarity.UNCOMMON, true, 15_000),
    SPOOKY_TENDRIL("Spooky Tendril", Material.RABBIT_HIDE, ItemRarity.UNCOMMON, true, 15_000),
    BRIMSTONE_RESIN("Brimstone Resin", Material.RESIN_BRICK, ItemRarity.UNCOMMON, true, 15_000),
    NECROPLASM("Necroplasm",           Material.BLACK_DYE, ItemRarity.RARE, true, Necroplasm.class),
    LUCIFUGOUS_THREAD("Lucifugous Thread", Material.STRING,    ItemRarity.EPIC, true, LucifugousThread.class),
    LUCIFUGOUS_BINDING(  "Lucifugous Binding",    Material.NETHERITE_SCRAP,     ItemRarity.EPIC, true, LucifugousBinding.class),
    NOCTURNUM_HELMET(    "Nocturnum Helmet",     Material.NETHERITE_HELMET,     ItemRarity.EPIC, NocturnumHelmet.class),
    NOCTURNUM_CHESTPLATE("Nocturnum Chestplate", Material.NETHERITE_CHESTPLATE, ItemRarity.EPIC, NocturnumChestplate.class),
    NOCTURNUM_LEGGINGS(  "Nocturnum Leggings",   Material.NETHERITE_LEGGINGS,   ItemRarity.EPIC, NocturnumLeggings.class),
    NOCTURNUM_BOOTS(     "Nocturnum Boots",      Material.LEATHER_BOOTS,        ItemRarity.EPIC, NocturnumBoots.class),
    NOCTURNUM_ROD("Nocturnum Rod", Material.FISHING_ROD, ItemRarity.EPIC, NocturnumRod.class),

    HEART_OF_THE_VOID(    "Heart of the Void",      Material.HEART_OF_THE_SEA, ItemRarity.RARE, true, 50_000),
    DISSIPATING_SEA_SHELL("Dissipating Sea Shell",  Material.GUNPOWDER,        ItemRarity.UNCOMMON, true, 15_000),
    ERRATIC_SLIME(        "Erratic Slime",          Material.DRAGON_BREATH,    ItemRarity.UNCOMMON, true, 15_000),
    IMPOSSIBLE_GEOMETRY(  "Impossible Geometry",    Material.FIREWORK_STAR,    ItemRarity.RARE, ImpossibleGeometry.class),
    FLAMEBROILED_PORKCHOP("Flame-broiled Porkchop", Material.COOKED_PORKCHOP,  ItemRarity.RARE, FlamebroiledPorkchop.class),
    XENOMATTER(          "Xenomatter",              Material.PHANTOM_MEMBRANE, ItemRarity.EPIC, true, Xenomatter.class),
    LATTICED_XENOMATTER( "Latticed Xenomatter",     Material.ECHO_SHARD,       ItemRarity.EPIC, true, LatticedXenomatter.class),
    STRANGE_FIBER(        "Strange Fiber",          Material.STRING,               ItemRarity.LEGENDARY, true, StrangeFiber.class),
    STRANGE_BINDING(      "Strange Binding",        Material.HEAVY_CORE,           ItemRarity.LEGENDARY, true, StrangeBinding.class),
    XENOHUNTER_HELMET(    "Xenohunter Helmet",      Material.NETHERITE_HELMET,     ItemRarity.LEGENDARY, XenohunterHelmet.class),
    XENOHUNTER_CHESTPLATE("Xenohunter Chestplate",  Material.NETHERITE_CHESTPLATE, ItemRarity.LEGENDARY, XenohunterChestplate.class),
    XENOHUNTER_LEGGINGS(  "Xenohunter Leggings",    Material.NETHERITE_LEGGINGS,   ItemRarity.LEGENDARY, XenohunterLeggings.class),
    XENOHUNTER_BOOTS(     "Xenohunter Boots",       Material.NETHERITE_BOOTS,      ItemRarity.LEGENDARY, XenohunterBoots.class),
    XENOHUNTER_ROD(       "Xenohunter Rod",         Material.FISHING_ROD,          ItemRarity.LEGENDARY, XenohunterRod.class),

    // SLAYER

    // SHAMBLING ABOMINATION

    NECROTIC_FLESH(      "Necrotic Flesh",          Material.ROTTEN_FLESH,       ItemRarity.UNCOMMON,      false, NecroticFlesh.class),
    PREMIUM_NECROTIC_FLESH("Premium Necrotic Flesh", Material.ROTTEN_FLESH, ItemRarity.RARE, true, NecroticFleshFamilyBlueprint.class),
    ENCHANTED_NECROTIC_FLESH(    "Enchanted Necrotic Flesh",        Material.ROTTEN_FLESH,       ItemRarity.EPIC,    true, NecroticFleshFamilyBlueprint.class),
    NECROTIC_FLESH_SINGULARITY(    "Necrotic Flesh Singularity",        Material.ROTTEN_FLESH,       ItemRarity.LEGENDARY,    true, NecroticFleshFamilyBlueprint.class),

    REVILED_VISCERA("Reviled Viscera", Material.ROTTEN_FLESH, ItemRarity.RARE, ReviledViscera.class),
    VISCERAL_AMALGAMATION("Visceral Amalgamation", Material.ROTTEN_FLESH, ItemRarity.RARE, VisceralAmalgamation.class),
    UNDIGESTED_BRAINS("Undigested Brains", Material.COCOA_BEANS, ItemRarity.EPIC, UndigestedBrains.class),
    NECRONOMICON_EXCERPTS("Accursed Manuscripts", Material.PAPER, ItemRarity.LEGENDARY, NecronomiconExcerpts.class),

    // HEART CRYSTALS (permanent maximum-health consumables, one tier per dimension; see HeartTier).
    VITALITY_HEART("Vitality Heart", Material.GHAST_TEAR,       ItemRarity.COMMON,    HeartConsumableBlueprint.class),
    EMBER_HEART(   "Ember Heart",    Material.BLAZE_POWDER,     ItemRarity.UNCOMMON,  HeartConsumableBlueprint.class),
    CLOUDHEART(    "Cloudheart",     Material.HEART_OF_THE_SEA, ItemRarity.RARE,      HeartConsumableBlueprint.class),
    VOIDHEART(     "Voidheart",      Material.ECHO_SHARD,       ItemRarity.EPIC,      HeartConsumableBlueprint.class),
    ETERNAL_HEART( "Eternal Heart",  Material.NETHER_STAR,      ItemRarity.LEGENDARY, HeartConsumableBlueprint.class),

    // HEART CRYSTAL REAGENTS (recipes/drops are added later; defined here so they exist as items).
    // Universal grind reagent, intended to drop from skill activities across every dimension.
    VITALITY_SHARD("Vitality Shard", Material.AMETHYST_SHARD,   ItemRarity.UNCOMMON,  false, 250),
    // Dimension-exclusive core reagents that gate each heart tier to its origin dimension.
    VERDANT_CORE(  "Verdant Core",   Material.EMERALD,          ItemRarity.COMMON,    false, 500),
    INFERNAL_CORE( "Infernal Core",  Material.MAGMA_CREAM,      ItemRarity.UNCOMMON,  false, 1500),
    AETHERIAL_CORE("Aetherial Core", Material.PHANTOM_MEMBRANE, ItemRarity.RARE,      false, 5000),
    VOID_CORE(     "Void Core",      Material.DRAGON_BREATH,    ItemRarity.EPIC,      false, 20000),
    ETERNAL_CORE(  "Eternal Core",   Material.HEAVY_CORE,       ItemRarity.LEGENDARY, false, 80000),

    ABOMINABLE_CLEAVER(     "Abominable Cleaver",  Material.GOLDEN_SWORD,     ItemRarity.RARE, AbominableCleaver.class),
    ABOMINABLE_MACHETE(     "Abominable Machete",  Material.DIAMOND_SWORD,    ItemRarity.EPIC, AbominableMachete.class),
    ABOMINABLE_HALBERD(     "Abominable Halberd",  Material.NETHERITE_SWORD,  ItemRarity.LEGENDARY, AbominableHalberd.class),
    ABOMINABLE_HELMET("Abominable Helmet", Material.DIAMOND_HELMET, ItemRarity.EPIC, AbominationHelmet.class),
    ABOMINABLE_CHESTPLATE("Abominable Chestplate", Material.DIAMOND_CHESTPLATE, ItemRarity.EPIC, AbominationChestplate.class),
    ABOMINABLE_LEGGINGS("Abominable Leggings", Material.DIAMOND_LEGGINGS, ItemRarity.EPIC, AbominationLeggings.class),
    ABOMINABLE_BOOTS("Abominable Boots", Material.DIAMOND_BOOTS, ItemRarity.EPIC, AbominationBoots.class),

    // ILLAGER WARLOCK

    SPELL_POWDER(      "Spell Powder",          Material.GUNPOWDER,       ItemRarity.UNCOMMON,      false, SpellPowder.class),
    PREMIUM_SPELL_POWDER("Premium Spell Powder", Material.GUNPOWDER, ItemRarity.RARE, true, SpellPowderFamilyBlueprint.class),
    ENCHANTED_SPELL_POWDER(    "Enchanted Spell Powder",        Material.GUNPOWDER,       ItemRarity.EPIC,    true, SpellPowderFamilyBlueprint.class),
    SPELL_POWDER_SINGULARITY(    "Spell Powder Singularity",        Material.GUNPOWDER,       ItemRarity.LEGENDARY,    true, SpellPowderFamilyBlueprint.class),

    SPELLBOUND_CLOTH("Spellbound Cloth", Material.FLINT, ItemRarity.RARE, true, SpellboundCloth.class),
    HORN_OF_WARLOCK("Horn of Warlock", Material.FLINT, ItemRarity.RARE, HornOfWarlock.class),
    CRYSTAL_BALL("Crystal Ball", Material.FLINT, ItemRarity.RARE, CrystalBallBlueprint.class),

    WARLOCK_HOOD("Warlock's Hood", Material.DIAMOND_HELMET, ItemRarity.EPIC, WarlockHood.class),
    WARLOCK_ROBES("Warlock's Robes", Material.DIAMOND_CHESTPLATE, ItemRarity.EPIC, WarlockRobes.class),
    WARLOCK_TROUSERS("Warlock's Trousers", Material.DIAMOND_LEGGINGS, ItemRarity.EPIC, WarlockTrousers.class),
    WARLOCK_SHOES("Warlock's Shoes", Material.DIAMOND_BOOTS, ItemRarity.EPIC, WarlockShoes.class),

    IRON_ROD("Iron Rod", Material.FISHING_ROD, ItemRarity.COMMON, WaterRod.class),
    MITHRIL_ROD("Mithril Rod", Material.FISHING_ROD, ItemRarity.UNCOMMON, WaterRod.class),
    PRISMARINE_ROD("Prismarine Rod", Material.FISHING_ROD, ItemRarity.RARE, WaterRod.class),

    GOLD_ROD("Gold Rod", Material.FISHING_ROD, ItemRarity.COMMON, LavaRod.class),
    STEEL_ROD("Steel Rod", Material.FISHING_ROD, ItemRarity.UNCOMMON, LavaRod.class),
    NETHERITE_ROD("Netherite Rod", Material.FISHING_ROD, ItemRarity.RARE, LavaRod.class),
    SPITFIRE_ROD("Spitfire Rod", Material.FISHING_ROD, ItemRarity.EPIC, LavaRod.class),

    SHARK_FIN("Shark Fin", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, true, 5_000),
    CINDERITE("Cinderite", Material.BLAZE_POWDER, ItemRarity.UNCOMMON, true, 7_500, "materials"),
    ECHO_MEMBRANE("Echo Membrane", Material.PHANTOM_MEMBRANE, ItemRarity.UNCOMMON, true, 15_000, "materials"),

    AERCLOUD_ROD("Aercloud Rod", Material.FISHING_ROD, ItemRarity.COMMON, AerialRod.class),
    ETHER_ROD("Ether Rod", Material.FISHING_ROD, ItemRarity.UNCOMMON, AerialRod.class),
    MERCURIAL_ROD("Mercurial Rod", Material.FISHING_ROD, ItemRarity.RARE, AerialRod.class),
    ZEPHYRUS_ROD("Zephyrus Rod", Material.FISHING_ROD, ItemRarity.EPIC, AerialRod.class),

    QUICKSILVER_FILAMENT("Astral Filament", Material.STRING, ItemRarity.RARE, true, 20_000),
    OLYMPIAN_THREAD("Olypmian Thread", Material.STRING, ItemRarity.EPIC, true, 50_000),

    ENDSTONE_ROD("Endstone Rod", Material.FISHING_ROD, ItemRarity.COMMON, VoidRod.class),
    ENDER_ROD("Ender Rod", Material.FISHING_ROD, ItemRarity.UNCOMMON, VoidRod.class),
    COMET_ROD("Comet Rod", Material.FISHING_ROD, ItemRarity.RARE, VoidRod.class),
    NEBULA_ROD("Nebula Rod", Material.FISHING_ROD, ItemRarity.EPIC, VoidRod.class),

    ASTRAL_FILAMENT("Astral Filament", Material.STRING, ItemRarity.RARE, AstralFilament.class),
    ETHEREAL_FIBER("Ethereal Fiber", Material.STRING, ItemRarity.EPIC, EtherealFiber.class),

    // ELYTRAS
    PHANTOM_WINGS("Phantom Wings", Material.ELYTRA, ItemRarity.RARE, PhantomWings.class),
    EVORI_DREAMWINGS("Evori Dreamwings", Material.ELYTRA, ItemRarity.EPIC, EvoriDreamwings.class),
    WINGS_OF_ICARUS("Wings of Icarus", Material.ELYTRA, ItemRarity.UNCOMMON, WingsOfIcarus.class),

    // CHARMS
    SPEED_CHARM("Speed Charm",       Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      SpeedCharm.class),
    STRENGTH_CHARM("Strength Charm", Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      StrengthCharm.class),
    LUCKY_CHARM("Fortune Charm",       Material.PRISMARINE_CRYSTALS, ItemRarity.EPIC,      LuckyCharm.class),

    // TOMES
    SIMPLE_TOME("Simple Tome",          Material.PAPER,  ItemRarity.COMMON,        SimpleTome.class),
    ENCHANTED_TOME("Enchanted Tome",    Material.PAPER,  ItemRarity.UNCOMMON,      EnchantedTome.class),
    EVOKATION_CODEX("Evokation Codex",  Material.PAPER,  ItemRarity.RARE,          EvokationCodex.class),
    BOOK_OF_SHADOWS("Book of Shadows",  Material.PAPER,  ItemRarity.EPIC,          BookOfShadows.class),
    NECRONOMICON("Necronomicon",        Material.PAPER,  ItemRarity.LEGENDARY,     Necronomicon.class),

    // SPELLS
    FIREBALL_SPELL("Fireball Spell Scroll", Material.PAPER, ItemRarity.COMMON, FireballSpellScroll.class),
    SYPHON_SPELL("Syphon Spell Scroll", Material.PAPER, ItemRarity.RARE, SyphonSpellScroll.class),
    FANG_STRIKE_SPELL("Fang Strike Spell Scroll", Material.PAPER, ItemRarity.UNCOMMON, FangStrikeSpellScroll.class),
    DAMAGE_AURA_SPELL("Damage Aura Spell Scroll", Material.PAPER, ItemRarity.RARE, DamageAuraSpellScroll.class),
    HEALING_AURA_SPELL("Healing Aura Spell Scroll", Material.PAPER, ItemRarity.RARE, HealingAuraSpellScroll.class),
    CONJURE_PLATFORM_SPELL("Conjure Platform Spell Scroll", Material.PAPER, ItemRarity.COMMON, ConjurePlatformSpellScroll.class),
    CONJURE_WALL_SPELL("Conjure Wall Spell Scroll", Material.PAPER, ItemRarity.COMMON, ConjureWallSpellScroll.class),

    // BOWS
    NETHERITE_BOW("Netherite Bow", Material.BOW, ItemRarity.EPIC,     NetheriteBow.class),
    // DIAMOND_BOW("Diamond Bow",     Material.BOW, ItemRarity.UNCOMMON, DiamondBow.class),
    IRON_BOW(   "Iron Bow",        Material.BOW, ItemRarity.COMMON,   IronBow.class),

    // HATCHETS
    WOODEN_HATCHET("Wooden Hatchet",    Material.WOODEN_AXE,    ItemRarity.COMMON,   WoodHatchet.class),
    // STONE_HATCHET("Stone Hatchet",     Material.STONE_AXE,     ItemRarity.COMMON,   StoneHatchet.class),
    COPPER_HATCHET("Copper Hatchet",    Material.WOODEN_AXE,    ItemRarity.COMMON,   CopperHatchet.class),
    IRON_HATCHET("Iron Hatchet",      Material.IRON_AXE,      ItemRarity.COMMON,   IronHatchet.class),
    GOLD_HATCHET("Golden Hatchet",      Material.GOLDEN_AXE,    ItemRarity.COMMON,   GoldHatchet.class),
    // DIAMOND_HATCHET("Diamond Hatchet",   Material.DIAMOND_AXE,   ItemRarity.RARE, DiamondHatchet.class),
    NETHERITE_HATCHET("Netherite Hatchet", Material.NETHERITE_AXE, ItemRarity.EPIC,     NetheriteHatchet.class),

    // PROGRESSIVE HOES
    WHEAT_HOE("Wheat Hoe", Material.IRON_HOE, ItemRarity.RARE, ProgressiveHoeBlueprint.class),
    POTATO_HOE("Potato Hoe", Material.IRON_HOE, ItemRarity.RARE, ProgressiveHoeBlueprint.class),
    ONION_HOE("Onion Hoe", Material.IRON_HOE, ItemRarity.RARE, ProgressiveHoeBlueprint.class),
    CARROT_HOE("Carrot Hoe", Material.IRON_HOE, ItemRarity.RARE, ProgressiveHoeBlueprint.class),
    MELON_HOE("Melon Hoe", Material.IRON_HOE, ItemRarity.RARE, ProgressiveHoeBlueprint.class),

    // KNIVES
    IRON_KNIFE("Iron Knife", Material.IRON_SWORD, ItemRarity.COMMON, IronKnife.class),

    // EXILED SET
    EXILED_CROSSBOW("Exiled Crossbow", Material.CROSSBOW, ItemRarity.EPIC,  ExiledCrossbow.class),
    EXILED_AXE("Exiled Axe", Material.IRON_AXE, ItemRarity.EPIC,  ExiledAxe.class),

    // MISC TOOLS
    PORTACRAFTER("Port-A-Crafter", ItemRarity.UNCOMMON, Portacrafter.class),
    ENDER_PACK("Ender Pack", ItemRarity.EPIC, EnderPack.class),
    SQUID_HELMET("Squid Helmet", ItemRarity.RARE, SquidHelmet.class),
    MAGMA_HELMET("Magma Helmet", ItemRarity.RARE, MagmaHelmet.class),
    EVERLASTING_WATER_BUCKET("Everlasting Water Bucket", Material.WATER_BUCKET, ItemRarity.RARE, EverlastingWaterBucket.class),
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

    // COMMON AETHER FISH
    FLYING_FISH("Flying Fish",  Material.TROPICAL_FISH, ItemRarity.COMMON,    FishBlueprint.class),

    // UNCOMMON AETHER FISH
    SKY_BARNACLE("Sky Barnacle",  Material.TROPICAL_FISH, ItemRarity.UNCOMMON,  FishBlueprint.class),

    // RARE AETHER FISH
    CLOUD_CLAM("Cloud Clam",  Material.TROPICAL_FISH, ItemRarity.RARE,  FishBlueprint.class),

    // EPIC AETHER FISH
    ANGELFISH("Angelfish",     Material.TROPICAL_FISH, ItemRarity.EPIC,    FishBlueprint.class),

    // LEGENDARY AETHER FISH,
    HOLY_MACKEREL("Holy Mackerel",  Material.TROPICAL_FISH, ItemRarity.LEGENDARY,  FishBlueprint.class),

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

    COMMON_FISH_ESSENCE("Fish Essence", Material.GUNPOWDER, ItemRarity.COMMON, false, 50),
    UNCOMMON_FISH_ESSENCE("Fish Essence", Material.SUGAR, ItemRarity.UNCOMMON, false, 300),
    RARE_FISH_ESSENCE("Fish Essence", Material.BLUE_DYE, ItemRarity.RARE, false, 4_000),
    EPIC_FISH_ESSENCE("Fish Essence", Material.REDSTONE, ItemRarity.EPIC, false, 20_000),
    LEGENDARY_FISH_ESSENCE("Fish Essence", Material.GLOWSTONE_DUST, ItemRarity.LEGENDARY, false, 100_000),
    MYTHIC_FISH_ESSENCE("Fish Essence", Material.AXOLOTL_SPAWN_EGG, ItemRarity.MYTHIC, true, 500_000),
    DIVINE_FISH_ESSENCE("Fish Essence", Material.ALLAY_SPAWN_EGG, ItemRarity.DIVINE, true, 2_500_000),
    TRANSCENDENT_FISH_ESSENCE("Fish Essence", Material.MAGMA_CUBE_SPAWN_EGG, ItemRarity.TRANSCENDENT, true, 10_000_000),

    // FISH STUFF
    CAVIAR("Caviar", Material.PHANTOM_MEMBRANE, ItemRarity.EPIC, true, Caviar.class),

    // CROPS
    TOMATO("Tomato", Material.APPLE, ItemRarity.COMMON, Tomato.class),
    TOMATO_SEEDS("Tomato Seeds", Material.WHEAT, ItemRarity.COMMON, CraftEngineBlueprint.class),
    CABBAGE("Cabbage", Material.APPLE, ItemRarity.COMMON, Cabbage.class),
    CABBAGE_SEEDS("Cabbage Seeds", Material.WHEAT, ItemRarity.COMMON, CraftEngineBlueprint.class),
    CABBAGE_LEAF("Cabbage Leaf", Material.APPLE, ItemRarity.COMMON, CabbageLeaf.class),
    RICE("Rice", Material.WHEAT, ItemRarity.COMMON, CraftEngineBlueprint.class),
    RICE_PANICLE("Rice Pannicle", Material.WHEAT, ItemRarity.COMMON, false, 5, "materials"),
    HEARTBEET("Heartbeet", Material.BEETROOT, ItemRarity.UNCOMMON, Heartbeet.class),
    STRAW("Straw", Material.WHEAT, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SUPER_SOIL("Super Soil", Material.NETHER_BRICK, ItemRarity.UNCOMMON, SuperSoil.class),

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

    // INGREDIENTS
    UNFIRED_CERAMIC_PLATE("Unfired Ceramic Plate", Material.CLAY_BALL, ItemRarity.COMMON, UnfiredCeramicPlate.class),
    CERAMIC_PLATE("Ceramic Plate", Material.BRICK, ItemRarity.COMMON, CeramicPlate.class),

    BACON("Bacon", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    BEEF_PATTY("Beef Patty", Material.NETHER_BRICK, ItemRarity.COMMON, BeefPatty.class),
    CHICKEN_CUTS("Chicken Cuts", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COD_FILET("Cod Filet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COOKED_BACON("Cooked Bacon", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COOKED_BEEF_PATTY("Cooked Beef Patty", Material.NETHER_BRICK, ItemRarity.COMMON, CookedBeefPatty.class),
    COOKED_CHICKEN_CUTS("Cooked Chicken Cuts", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COOKED_COD_FILET("Cod Filet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COOKED_GROUND_BEEF("Cooked Ground Beef", Material.NETHER_BRICK, ItemRarity.COMMON, CookedGroundBeef.class),
    COOKED_GROUND_PORK("Cooked Ground Pork", Material.NETHER_BRICK, ItemRarity.COMMON, CookedGroundPork.class),
    COOKED_MUTTON_CHOPS("Cooked Mutton Chops", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    COOKED_SALMON_FILET("Cooked Salmon Filet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    FRIED_EGG("Cooked Salmon Filet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    GROUND_BEEF("Ground Beef", Material.NETHER_BRICK, ItemRarity.COMMON, GroundBeef.class),
    GROUND_PORK("Ground Pork", Material.NETHER_BRICK, ItemRarity.COMMON, GroundPork.class),
    HAM("Ham", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    MILK_BOTTLE("Milk Bottle", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    MUTTON_CHOPS("Mutton Chops", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    PIE_CRUST("Pie Crust", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    PUMPKIN_SLICE("Pumpkin Slice", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    RAW_PASTA("Raw Pasta", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    SALMON_FILET("Salmon Filet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    STEAK_STRIPS("Steak Strips", Material.NETHER_BRICK, ItemRarity.COMMON, SteakStrips.class),
    TOMATO_SAUCE("Tomato Sauce", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),
    WHEAT_DOUGH("Wheat Dough", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineFoodBlueprint.class),

    // MEALS
    APPLE_CIDER("Apple Cider", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    APPLE_PIE("Apple Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    APPLE_PIE_SLICE("Slice of Apple Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    BACON_AND_EGGS("Bacon and Eggs", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    BACON_SANDWICH("Bacon Sandwich", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    BAKED_COD_STEW("Baked Cod Stew", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    BARBECUE_STICK("Barbecue Stick", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    BEEF_STEW("Beef Stew", Material.MUSHROOM_STEW, ItemRarity.UNCOMMON, BeefStew.class),
    BONE_BROTH("Bone Broth", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CABBAGE_ROLLS("Cabbage Rolls", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CAKE_SLICE("Slice of Cake", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CHICKEN_SANDWICH("Chicken Sandwich", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CHICKEN_SOUP("Chicken Soup", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CHOCOLATE_PIE("Chocolate Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    CHOCOLATE_PIE_SLICE("Slice of Chocolate Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    COD_ROLL("Cod Roll", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    COOKED_RICE("Cooked Rice", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    DOG_FOOD("Dog Food", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    DUMPLINGS("Dumplings", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    EGG_SANDWICH("Egg Sandwich", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    FISH_STEW("Fish Stew", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    FRIED_RICE("Fried Rice", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    FRUIT_SALAD("Fruit Salad", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    GLEAMING_SALAD("Gleaming Salad", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    GLOW_BERRY_CUSTARD("Glow Berry Custard", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    GRILLED_SALMON("Grilled Salmon", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    HAMBURGER("Hamburger", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    HONEY_COOKIE("Honey Cookies", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    HONEY_GLAZED_HAM("Honey Glazed Ham", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    HORSE_FEED("Horse Feed", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    HOT_COCOA("Hot Cocoa", Material.NETHER_BRICK, ItemRarity.UNCOMMON, HotCocoa.class),
    KELP_ROLL("Kelp Roll", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    KELP_ROLL_SLICE("Kelp Roll Slice", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    MELON_JUICE("Melon Juice", Material.NETHER_BRICK, ItemRarity.UNCOMMON, MelonJuice.class),
    MELON_POPSICLE("Melon Popsicle", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    MIXED_SALAD("Mixed Salad", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    MUSHROOM_RICE("Mushroom Rice", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    MUTTON_WRAP("Mutton Wrap", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    NETHER_SALAD("Nether Salad", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    NOODLE_SOUP("Noodle Soup", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    ONION_SOUP("Onion Soup", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    PASTA_WITH_MEATBALLS("Pasta with Meatballs", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    PASTA_WITH_MUTTON_CHOP("Pasta with Mutton Chop", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    PUMPKIN_PIE_SLICE("Slice of Pumpkin Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    PUMPKIN_SOUP("Pumpkin Soup", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    RATATOUILLE("Ratatouille", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    ROAST_CHICKEN("Roast Chicken", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    ROASTED_MUTTON_CHOPS("Roasted Mutton Chops", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SALMON_ROLL("Salmon Roll", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SHEPHERDS_PIE("Shepherd's Pie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SMOKED_HAM("Smoked Ham", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SQUID_INK_PASTA("Squid Ink Pasta", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    STEAK_AND_POTATOES("Steak and Potatoes", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    STUFFED_POTATO("Stuffed Potato", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    STUFFED_PUMPKIN("Stuffed Pumpkin", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SWEET_BERRY_CHEESECAKE("Sweet Berry Cheesecake", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SWEET_BERRY_CHEESECAKE_SLICE("Slice of Sweet Berry Cheesecake", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    SWEET_BERRY_COOKIE("Sweet Berry Cookie", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    VEGETABLE_NOODLES("Vegetable Noodles", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),
    VEGETABLE_SOUP("Vegetable Soup", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineFoodBlueprint.class),

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

    // Dirt
    COMPRESSED_DIRT("Compressed Dirt", Material.DIRT, ItemRarity.COMMON, true, DirtFamilyBlueprint.class),
    DOUBLE_COMPRESSED_DIRT("Double Compressed Dirt", Material.DIRT, ItemRarity.UNCOMMON, true, DirtFamilyBlueprint.class),
    ENCHANTED_DIRT("Enchanted Dirt", Material.DIRT, ItemRarity.UNCOMMON, true, DirtFamilyBlueprint.class),
    DIRT_SINGULARITY("Dirt Singularity", Material.PACKED_MUD, ItemRarity.RARE, true, DirtFamilyBlueprint.class),

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

    // AETHER MOBS

    // BREEZE
    PREMIUM_BREEZE_ROD("Premium Breeze Rod", Material.BREEZE_ROD, ItemRarity.UNCOMMON, true, BreezeRodFamilyBlueprint.class),
    ENCHANTED_BREEZE_ROD("Enchanted Breeze Rod", Material.BREEZE_ROD, ItemRarity.RARE, true, BreezeRodFamilyBlueprint.class),

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
    PLUTO_FRAGMENT("Pluto Fragment", Material.PRISMARINE_SHARD, ItemRarity.RARE, true, 4000, "materials"),
    PLUTOS_ARTIFACT("Pluto's Artifact", ItemRarity.EPIC, PlutosArtifact.class),
    JUPITER_CRYSTAL("Jupiter Crystal", Material.PRISMARINE_CRYSTALS, ItemRarity.RARE, true, 1000, "materials"),
    JUPITERS_ARTIFACT("Jupiter's Artifact", ItemRarity.EPIC, JupiterArtifact.class),
    IRIDESCENT_LENS("Iridescent Lens", Material.FLINT, ItemRarity.LEGENDARY, IridescentLens.class),
    PREDATOR_TOOTH("Predator Tooth", ItemRarity.LEGENDARY, PredatorTooth.class),
    HYPNOTIC_EYE("Hypnotic Eye", ItemRarity.LEGENDARY, HypnoticEye.class),

    MOSSY_SKULL("Mossy Skull", ItemRarity.EPIC, MossySkull.class),
    GILDED_SKULL("Gilded Skull", ItemRarity.LEGENDARY, GildedSkull.class),

    // DRAGON
    DRAGON_SCALES(   "Dragon Scales",    Material.PHANTOM_MEMBRANE, ItemRarity.RARE, true, 50_000, "materials"),
    DRACONIC_CRYSTAL("Draconic Crystal", ItemRarity.EPIC, DraconicCrystal.class),
    TRANSMISSION_WAND("Transmission Wand", Material.PRISMARINE_SHARD, ItemRarity.EPIC, TransmissionWand.class),
    MAGIC_MIRROR("Magic Mirror", Material.RECOVERY_COMPASS, ItemRarity.EPIC, MagicMirror.class),
    SLUMBER_SHARD("Slumber Shard", Material.PRISMARINE_SHARD, ItemRarity.EPIC, MagicMirrorShard.class),
    CINDER_SHARD("Cinder Shard", Material.RESIN_BRICK, ItemRarity.EPIC, MagicMirrorShard.class),
    VOID_SHARD("Void Shard", Material.ECHO_SHARD, ItemRarity.EPIC, MagicMirrorShard.class),

    SUMMONING_CRYSTAL("Summoning Crystal", Material.END_CRYSTAL, ItemRarity.EPIC, true, 50_000),

    // DRAGONSTEEL
    DRAGONSTEEL_BLOCK(     "Block of Dragonsteel",      Material.NETHER_BRICK,     ItemRarity.LEGENDARY, CraftEngineBlueprint.class),
    DRAGONSTEEL_INGOT("Dragonsteel Ingot", Material.IRON_INGOT, ItemRarity.EPIC, DragonsteelIngot.class),

    DRAGONSTEEL_PICKAXE("Dragonsteel Pickaxe",      Material.NETHERITE_PICKAXE,      ItemRarity.LEGENDARY,   DragonsteelPickaxe.class),
    DRAGONSTEEL_AXE("Dragonsteel Axe",              Material.NETHERITE_AXE,          ItemRarity.LEGENDARY,   DragonsteelAxe.class),
    DRAGONSTEEL_HATCHET("Dragonsteel Hatchet",      Material.NETHERITE_AXE,          ItemRarity.LEGENDARY,   DragonsteelHatchet.class),
    DRAGONSTEEL_SWORD("Dragonsteel Sword",          Material.NETHERITE_SWORD,        ItemRarity.LEGENDARY,   DragonsteelSword.class),
    DRAGONSTEEL_HOE("Dragonsteel Hoe",              Material.NETHERITE_HOE,          ItemRarity.LEGENDARY,   DragonsteelHoe.class),
    DRAGONSTEEL_SHOVEL("Dragonsteel Shovel",        Material.NETHERITE_SHOVEL,       ItemRarity.LEGENDARY,   DragonsteelShovel.class),
    DRAGONSTEEL_SPEAR(  "Dragonsteel Spear",        Material.IRON_SPEAR,             ItemRarity.LEGENDARY,   DragonsteelSpear.class),

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

    MASHED_MELON("Mashed Melon", Material.NETHER_BRICK, ItemRarity.RARE, false, 10_000, "materials"),

    PREMIUM_WHEAT("Premium Wheat", Material.WHEAT, ItemRarity.UNCOMMON, true, WheatBlueprintFamily.class),
    PREMIUM_HAY_BLOCK("Premium Hay Block", Material.HAY_BLOCK, ItemRarity.UNCOMMON, true, WheatBlueprintFamily.class),
    ENCHANTED_WHEAT("Enchanted Wheat", Material.WHEAT, ItemRarity.RARE, true, WheatBlueprintFamily.class),
    ENCHANTED_HAY_BLOCK("Enchanted Hay Block", Material.HAY_BLOCK, ItemRarity.EPIC, true, WheatBlueprintFamily.class),
    WHEAT_SINGULARITY("Wheat Singularity", Material.WHEAT, ItemRarity.LEGENDARY, true, WheatBlueprintFamily.class),

    WHEAT_GRAINS("Wheat Grains", Material.NETHER_BRICK, ItemRarity.RARE, false, 10_000, "materials"),

    PREMIUM_CARROT("Premium Carrot", Material.CARROT, ItemRarity.UNCOMMON, true, CarrotBlueprintFamily.class),
    PREMIUM_GOLDEN_CARROT("Premium Golden Carrot", Material.GOLDEN_CARROT, ItemRarity.UNCOMMON, true, CarrotBlueprintFamily.class),
    ENCHANTED_CARROT("Enchanted Carrot", Material.CARROT, ItemRarity.RARE, true, CarrotBlueprintFamily.class),
    ENCHANTED_GOLDEN_CARROT("Enchanted Golden Carrot", Material.GOLDEN_CARROT, ItemRarity.EPIC, true, CarrotBlueprintFamily.class),
    CARROT_SINGULARITY("Carrot Singularity", Material.GOLDEN_CARROT, ItemRarity.LEGENDARY, true, CarrotBlueprintFamily.class),

    BOOK_OF_BOUNTIES("Book of Bounties", Material.BOOK, ItemRarity.EPIC, BookOfBounties.class),
    GNAWED_CARROT("Gnawed Carrot", Material.NETHER_BRICK, ItemRarity.RARE, false, 10_000, "materials"),

    PREMIUM_POTATO("Premium Potato", Material.POTATO, ItemRarity.UNCOMMON, true, PotatoBlueprintFamily.class),
    PREMIUM_BAKED_POTATO("Premium Baked Potato", Material.BAKED_POTATO, ItemRarity.UNCOMMON, true, PotatoBlueprintFamily.class),
    ENCHANTED_POTATO("Enchanted Potato", Material.POTATO, ItemRarity.RARE, true, PotatoBlueprintFamily.class),
    ENCHANTED_BAKED_POTATO("Enchanted Baked Potato", Material.BAKED_POTATO, ItemRarity.EPIC, true, PotatoBlueprintFamily.class),
    POTATO_SINGULARITY("Potato Singularity", Material.POTATO, ItemRarity.LEGENDARY, true, PotatoBlueprintFamily.class),

    HOT_POTATO_BOOK("Hot Potato Book", Material.BOOK, ItemRarity.EPIC, HotPotatoBook.class),
    POTATO_EYES("Potato Eyes", Material.NETHER_BRICK, ItemRarity.RARE, false, 10_000, "materials"),

    PREMIUM_BEETROOT("Premium Beetroot", Material.BEETROOT, ItemRarity.UNCOMMON, true, BeetrootBlueprintFamily.class),
    ENCHANTED_BEETROOT("Enchanted Beetroot", Material.BEETROOT, ItemRarity.RARE, true, BeetrootBlueprintFamily.class),
    BEETROOT_SINGULARITY("Beetroot Singularity", Material.BEETROOT, ItemRarity.EPIC, true, BeetrootBlueprintFamily.class),

    PREMIUM_PUMPKIN("Premium Pumpkin", Material.PUMPKIN, ItemRarity.UNCOMMON, true, PumpkinBlueprintFamily.class),
    PREMIUM_CARVED_PUMPKIN("Premium Carved Pumpkin", Material.CARVED_PUMPKIN, ItemRarity.UNCOMMON, true, PumpkinBlueprintFamily.class),
    ENCHANTED_PUMPKIN("Enchanted Pumpkin", Material.PUMPKIN, ItemRarity.RARE, true, PumpkinBlueprintFamily.class),
    ENCHANTED_CARVED_PUMPKIN("Enchanted Carved Pumpkin", Material.CARVED_PUMPKIN, ItemRarity.EPIC, true, PumpkinBlueprintFamily.class),
    PUMPKIN_SINGULARITY("Pumpkin Singularity", Material.PUMPKIN, ItemRarity.LEGENDARY, true, PumpkinBlueprintFamily.class),

    ONION("Onion", Material.APPLE, ItemRarity.COMMON, Onion.class),
    PREMIUM_ONION("Premium Onion", Material.APPLE, ItemRarity.COMMON, OnionBlueprintFamily.class),
    ENCHANTED_ONION("Enchanted Onion", Material.APPLE, ItemRarity.UNCOMMON, OnionBlueprintFamily.class),
    ONION_SINGULARITY("Onion Singularity", Material.APPLE, ItemRarity.RARE, OnionBlueprintFamily.class),

    ONION_SKIN("Onion Skin", Material.NETHER_BRICK, ItemRarity.RARE, false, 10_000, "materials"),

    PREMIUM_OAK_LOG("Premium Oak Log", Material.OAK_LOG, ItemRarity.UNCOMMON, true, OakBlueprintFamily.class),
    ENCHANTED_OAK_LOG("Enchanted Oak Log", Material.OAK_LOG, ItemRarity.RARE, true, OakBlueprintFamily.class),
    OAK_LOG_SINGULARITY("Oak Log Singularity", Material.OAK_LOG, ItemRarity.EPIC, true, OakBlueprintFamily.class),

    PREMIUM_SPRUCE_LOG("Premium Spruce Log", Material.SPRUCE_LOG, ItemRarity.UNCOMMON, true, SpruceBlueprintFamily.class),
    ENCHANTED_SPRUCE_LOG("Enchanted Spruce Log", Material.SPRUCE_LOG, ItemRarity.RARE, true, SpruceBlueprintFamily.class),
    SPRUCE_LOG_SINGULARITY("Spruce Log Singularity", Material.SPRUCE_LOG, ItemRarity.EPIC, true, SpruceBlueprintFamily.class),

    PREMIUM_BIRCH_LOG("Premium Birch Log", Material.BIRCH_LOG, ItemRarity.UNCOMMON, true, BirchBlueprintFamily.class),
    ENCHANTED_BIRCH_LOG("Enchanted Birch Log", Material.BIRCH_LOG, ItemRarity.RARE, true, BirchBlueprintFamily.class),
    BIRCH_LOG_SINGULARITY("Birch Log Singularity", Material.BIRCH_LOG, ItemRarity.EPIC, true, BirchBlueprintFamily.class),

    PREMIUM_JUNGLE_LOG("Premium Jungle Log", Material.JUNGLE_LOG, ItemRarity.UNCOMMON, true, JungleBlueprintFamily.class),
    ENCHANTED_JUNGLE_LOG("Enchanted Jungle Log", Material.JUNGLE_LOG, ItemRarity.RARE, true, JungleBlueprintFamily.class),
    JUNGLE_LOG_SINGULARITY("Jungle Log Singularity", Material.JUNGLE_LOG, ItemRarity.EPIC, true, JungleBlueprintFamily.class),

    PREMIUM_DARK_OAK_LOG("Premium Dark Oak Log", Material.DARK_OAK_LOG, ItemRarity.UNCOMMON, true, DarkOakBlueprintFamily.class),
    ENCHANTED_DARK_OAK_LOG("Enchanted Dark Oak Log", Material.DARK_OAK_LOG, ItemRarity.RARE, true, DarkOakBlueprintFamily.class),
    DARK_OAK_LOG_SINGULARITY("Dark Oak Log Singularity", Material.DARK_OAK_LOG, ItemRarity.EPIC, true, DarkOakBlueprintFamily.class),

    PREMIUM_ACACIA_LOG("Premium Acacia Log", Material.ACACIA_LOG, ItemRarity.UNCOMMON, true, AcaciaBlueprintFamily.class),
    ENCHANTED_ACACIA_LOG("Enchanted Acacia Log", Material.ACACIA_LOG, ItemRarity.RARE, true, AcaciaBlueprintFamily.class),
    ACACIA_LOG_SINGULARITY("Acacia Log Singularity", Material.ACACIA_LOG, ItemRarity.EPIC, true, AcaciaBlueprintFamily.class),

    PREMIUM_MANGROVE_LOG("Premium Mangrove Log", Material.MANGROVE_LOG, ItemRarity.UNCOMMON, true, MangroveBlueprintFamily.class),
    ENCHANTED_MANGROVE_LOG("Enchanted Mangrove Log", Material.MANGROVE_LOG, ItemRarity.RARE, true, MangroveBlueprintFamily.class),
    MANGROVE_LOG_SINGULARITY("Mangrove Log Singularity", Material.MANGROVE_LOG, ItemRarity.EPIC, true, MangroveBlueprintFamily.class),

    PREMIUM_CHERRY_LOG("Premium Cherry Log", Material.CHERRY_LOG, ItemRarity.UNCOMMON, true, CherryBlueprintFamily.class),
    ENCHANTED_CHERRY_LOG("Enchanted Cherry Log", Material.CHERRY_LOG, ItemRarity.RARE, true, CherryBlueprintFamily.class),
    CHERRY_LOG_SINGULARITY("Cherry Log Singularity", Material.CHERRY_LOG, ItemRarity.EPIC, true, CherryBlueprintFamily.class),

    PREMIUM_PALE_OAK_LOG("Premium Pale Oak Log", Material.PALE_OAK_LOG, ItemRarity.UNCOMMON, true, PaleOakBlueprintFamily.class),
    ENCHANTED_PALE_OAK_LOG("Enchanted Pale Oak Log", Material.PALE_OAK_LOG, ItemRarity.RARE, true, PaleOakBlueprintFamily.class),
    PALE_OAK_LOG_SINGULARITY("Pale Oak Log Singularity", Material.PALE_OAK_LOG, ItemRarity.EPIC, true, PaleOakBlueprintFamily.class),

    PREMIUM_CRIMSON_STEM("Premium Crimson Stem", Material.CRIMSON_STEM, ItemRarity.UNCOMMON, true, CrimsonBlueprintFamily.class),
    ENCHANTED_CRIMSON_STEM("Enchanted Crimson Stem", Material.CRIMSON_STEM, ItemRarity.RARE, true, CrimsonBlueprintFamily.class),
    CRIMSON_STEM_SINGULARITY("Crimson Stem Singularity", Material.CRIMSON_STEM, ItemRarity.EPIC, true, CrimsonBlueprintFamily.class),

    PREMIUM_WARPED_STEM("Premium Warped Stem", Material.WARPED_STEM, ItemRarity.UNCOMMON, true, WarpedBlueprintFamily.class),
    ENCHANTED_WARPED_STEM("Enchanted Warped Stem", Material.WARPED_STEM, ItemRarity.RARE, true, WarpedBlueprintFamily.class),
    WARPED_STEM_SINGULARITY("Warped Stem Singularity", Material.WARPED_STEM, ItemRarity.EPIC, true, WarpedBlueprintFamily.class),

    PREMIUM_SKYROOT_LOG("Premium Skyroot Log", Material.NETHER_BRICK, ItemRarity.UNCOMMON, true, SkyrootBlueprintFamily.class),
    ENCHANTED_SKYROOT_LOG("Enchanted Skyroot Log", Material.NETHER_BRICK, ItemRarity.RARE, true, SkyrootBlueprintFamily.class),
    SKYROOT_LOG_SINGULARITY("Skyroot Log Singularity", Material.NETHER_BRICK, ItemRarity.EPIC, true, SkyrootBlueprintFamily.class),

    PREMIUM_GOLDEN_OAK_LOG("Premium Golden Oak Log", Material.NETHER_BRICK, ItemRarity.UNCOMMON, true, GoldenOakBlueprintFamily.class),
    ENCHANTED_GOLDEN_OAK_LOG("Enchanted Golden Oak Log", Material.NETHER_BRICK, ItemRarity.RARE, true, GoldenOakBlueprintFamily.class),
    GOLDEN_OAK_LOG_SINGULARITY("Golden Oak Log Singularity", Material.NETHER_BRICK, ItemRarity.EPIC, true, GoldenOakBlueprintFamily.class),

    PREMIUM_BINARY_LOG("Premium Binary Log", Material.NETHER_BRICK, ItemRarity.UNCOMMON, true, BinaryBlueprintFamily.class),
    ENCHANTED_BINARY_LOG("Enchanted Binary Log", Material.NETHER_BRICK, ItemRarity.RARE, true, BinaryBlueprintFamily.class),
    BINARY_LOG_SINGULARITY("Binary Log Singularity", Material.NETHER_BRICK, ItemRarity.EPIC, true, BinaryBlueprintFamily.class),

    // SINGULARITY SET
    SINGULARITY_HELMET(    "Singularity Helmet",     Material.CRYING_OBSIDIAN,      ItemRarity.MYTHIC, SingularityHelmet.class),
    SINGULARITY_CHESTPLATE("Singularity Chestplate", Material.NETHERITE_CHESTPLATE, ItemRarity.MYTHIC, SingularityChestplate.class),
    SINGULARITY_LEGGINGS(  "Singularity Leggings",   Material.NETHERITE_LEGGINGS,   ItemRarity.MYTHIC, SingularityLeggings.class),
    SINGULARITY_BOOTS(     "Singularity Boots",      Material.NETHERITE_BOOTS,      ItemRarity.MYTHIC, SingularityBoots.class),

    // WARDROBE TOKENS
    WARDROBE_SLOT_COMMON(   "Common Wardrobe Token",    Material.ARMOR_STAND, ItemRarity.COMMON,    WardrobeSlotToken.class),
    WARDROBE_SLOT_UNCOMMON( "Uncommon Wardrobe Token",  Material.ARMOR_STAND, ItemRarity.UNCOMMON,  WardrobeSlotToken.class),
    WARDROBE_SLOT_RARE(     "Rare Wardrobe Token",      Material.ARMOR_STAND, ItemRarity.RARE,      WardrobeSlotToken.class),
    WARDROBE_SLOT_EPIC(     "Epic Wardrobe Token",      Material.ARMOR_STAND, ItemRarity.EPIC,      WardrobeSlotToken.class),
    WARDROBE_SLOT_LEGENDARY("Legendary Wardrobe Token", Material.ARMOR_STAND, ItemRarity.LEGENDARY, WardrobeSlotToken.class),

    DEATH_CERTIFICATE("Death Certificate", Material.PAPER, ItemRarity.SPECIAL, DeathCertificate.class),
    DEATH_COMPASS("Death Compass", Material.COMPASS, ItemRarity.SPECIAL, DeathCompass.class),

    // BLOCKS
    REFORGE_TABLE(     "Reforge Table",        Material.NETHER_BRICK,     ItemRarity.UNCOMMON, ReforgeTable.class),
    NETHERITE_ANVIL(   "Netherite Anvil",      Material.NETHER_BRICK,     ItemRarity.EPIC, CraftEngineBlueprint.class),
    FREEZER(           "Freezer",              Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    CUTTING_BOARD(     "Cutting Board",        Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    COOKING_POT(       "Cooking Pot",          Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    TITANIUM_CACHE(    "Titanium Cache",       Material.NETHER_BRICK,     ItemRarity.RARE, CraftEngineBlueprint.class),

    GRIMSTONE("Grimstone", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    COBBLED_GRIMSTONE("Cobbled Grimstone", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRIMSTONE_DIAMOND_ORE("Grimstone Diamond Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRIMSTONE_IRON_ORE("Grimstone Iron Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRIMSTONE_GOLD_ORE("Grimstone Gold Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRIMSTONE_SILVER_ORE("Grimstone Silver Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRIMSTONE_LAPIS_ORE("Grimstone Lapis Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    RUNE_BLANK("Unattuned Enchantment Rune", Material.POISONOUS_POTATO, ItemRarity.UNCOMMON, RuneBlank.class),
    RUNE_POTENTIAL("Rune of Potential", Material.POISONOUS_POTATO, ItemRarity.RARE, RunePotential.class),
    RUNE_AMBITION("Rune of Ambition", Material.POISONOUS_POTATO, ItemRarity.RARE, RuneAmbition.class),
    RUNE_MEMORIZATION("Rune of Memorization", Material.POISONOUS_POTATO, ItemRarity.RARE, RuneMemorization.class),
    RUNE_GREED("Rune of Greed", Material.POISONOUS_POTATO, ItemRarity.RARE, RuneGreed.class),
    RUNE_INSIGHT("Rune of Insight", Material.POISONOUS_POTATO, ItemRarity.RARE, RuneInsight.class),
    RUNE_FORTUITY("Rune of Fortuity", Material.POISONOUS_POTATO, ItemRarity.RARE, RuneFortuity.class),
    RUNE_DIVINITY("Rune of Divinity", Material.POISONOUS_POTATO, ItemRarity.EPIC, RuneDivinity.class),

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
    DUMMY_SMITHING_RESULT("DUMMY SMITHING RESULT", Material.BARRIER, ItemRarity.SPECIAL),

    // SLAYER BOSS ARMOR (Worn by boss, not used by players)
    SHAMBLING_BOOTS("Shambling Boss Boots (not used by players)", Material.DIAMOND_BOOTS, ItemRarity.SPECIAL, ShamblingBossBoots.class),
    SHAMBLING_LEGGINGS("Shambling Boss Leggings (not used by players)", Material.DIAMOND_LEGGINGS, ItemRarity.SPECIAL, ShamblingBossLeggings.class),
    SHAMBLING_CHESTPLATE("Shambling Boss Chestplate (not used by players)", Material.DIAMOND_CHESTPLATE, ItemRarity.SPECIAL, ShamblingBossChestplate.class),

    // ENCHANTING SCROLLS
    ENCHANTING_SCROLL("Scroll of Imbuement", Material.PAPER, ItemRarity.RARE, true, DynamicEnchantingScroll.class),

    // AETHER
    COLD_AERCLOUD("Cold Aercloud", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BLUE_AERCLOUD("Blue Aercloud", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLD_AERCLOUD("Gold Aercloud", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    WISPY_AERCLOUD("Wispy Aercloud", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    AEROGEL("Aerogel", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    AETHER_DIRT("Aether Dirt", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    AETHER_GRASS_BLOCK("Aether Grass Block", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    ENCHANTED_AETHER_GRASS_BLOCK("Enchanted Aether Grass Block", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE("Holystone", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    ICESTONE("Icestone", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    QUICKSOIL("Quicksoil", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    HOLYSTONE_BRICKS("Holystone Bricks", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_BRICK_STAIRS("Holystone Brick Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_BRICK_SLAB("Holystone Brick Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_STAIRS("Holystone Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_SLAB("Holystone Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    ICESTONE_STAIRS("Icestone Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    ICESTONE_SLAB("Icestone Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_BUTTON("Holystone Button", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HOLYSTONE_PRESSURE_PLATE("Holystone Pressure Plate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    AETHER_SILVER_ORE("Aether Silver Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    AMBROSIUM_ORE("Ambrosium Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    ZANITE_ORE("Zanite Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    PLATINUM_ORE("Platinum Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    PALLADIUM_ORE("Palladium Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GRAVITITE_ORE("Gravitite Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    PUFFBLOOM("Puffbloom", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    VIOLET("Violet", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    RAINBOW_LILY("Rainbow Lily", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    EXTINGUISHED_TORCH("Extinguished Torch", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    AMBROSIUM_TORCH("Ambrosium Torch", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKY_BERRY("Sky Berry", Material.NETHER_BRICK, ItemRarity.COMMON, SkyBerry.class),
    SQUASH("Squash", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SQUASH_SEEDS("Squash Seeds", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    HALVED_SQUASH("Halved Squash", Material.NETHER_BRICK, ItemRarity.COMMON, HalvedSquash.class),

    AMBROSIUM("Ambrosium", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    ZANITE("Zanite", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),

    AMBROSIUM_BLOCK("Block of Ambrosium", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),
    ZANITE_BLOCK("Block of Zanite", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),
    AMBROSIUM_ENCRUSTED_SHAFT(    "Ambrosium Encrusted Shaft",      Material.STICK,     ItemRarity.UNCOMMON, AmbrosiumToolShaft.class),

    // PALLADIUM
    RAW_PALLADIUM("Raw Palladium", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),
    PALLADIUM_INGOT("Palladium Ingot", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),

    // PALLADIUM_DRILL_HEAD("Palladium Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, PalladiumDrillHead.class),
    // PALLADIUM_DRILL_BASE("Palladium Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, PalladiumDrillBase.class),

    PALLADIUM_PICKAXE("Palladium Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.RARE, PalladiumPickaxe.class),
    PALLADIUM_AXE(    "Palladium Axe",      Material.DIAMOND_AXE,     ItemRarity.RARE, PalladiumAxe.class),
    PALLADIUM_HOE(    "Palladium Hoe",      Material.DIAMOND_HOE,     ItemRarity.RARE, PalladiumHoe.class),
    PALLADIUM_SHOVEL( "Palladium Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.RARE, PalladiumShovel.class),
    PALLADIUM_SWORD(  "Palladium Sword",    Material.DIAMOND_SWORD,   ItemRarity.RARE, PalladiumSword.class),
    PALLADIUM_HATCHET("Palladium Hatchet",  Material.DIAMOND_AXE,     ItemRarity.RARE, PalladiumHatchet.class),
    PALLADIUM_BOW(    "Palladium Bow",      Material.BOW,             ItemRarity.RARE, PalladiumBow.class),
    PALLADIUM_SPEAR(  "Palladium Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, PalladiumSpear.class),

    PALLADIUM_BOOTS(     "Palladium Boots",      Material.IRON_BOOTS,       ItemRarity.RARE, PalladiumBoots.class),
    PALLADIUM_LEGGINGS(  "Palladium Leggings",   Material.IRON_LEGGINGS,    ItemRarity.RARE, PalladiumLeggings.class),
    PALLADIUM_CHESTPLATE("Palladium Chestplate", Material.IRON_CHESTPLATE,  ItemRarity.RARE, PalladiumChestplate.class),
    PALLADIUM_HELMET(    "Palladium Helmet",     Material.IRON_HELMET,      ItemRarity.RARE, PalladiumHelmet.class),

    // PLATINUM
    RAW_PLATINUM("Raw Platinum", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),
    PLATINUM_INGOT("Platinum Ingot", Material.NETHER_BRICK, ItemRarity.RARE, PlatinumIngot.class),
    PLATINUM_BLOCK("Platinum Block", Material.NETHER_BRICK, ItemRarity.EPIC, PlatinumFamilyBlueprint.class),
    ENCHANTED_PLATINUM("Enchanted Platinum",                  Material.NETHER_BRICK,        ItemRarity.LEGENDARY,true, PlatinumFamilyBlueprint.class),
    ENCHANTED_PLATINUM_BLOCK("Enchanted Block of Platinum",   Material.NETHER_BRICK,        ItemRarity.MYTHIC, true, PlatinumFamilyBlueprint.class),
    PLATINUM_SINGULARITY("Platinum Singularity",              Material.NETHER_BRICK,        ItemRarity.DIVINE, true, PlatinumFamilyBlueprint.class),

    // PLATINUM_DRILL_HEAD("Platinum Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, PlatinummDrillHead.class),
    // PLATINUM_DRILL_BASE("Platinum Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, PlatinummDrillBase.class),

    PLATINUM_PICKAXE("Platinum Pickaxe",  Material.IRON_PICKAXE, ItemRarity.RARE, PlatinumPickaxe.class),
    PLATINUM_AXE(    "Platinum Axe",      Material.IRON_AXE,     ItemRarity.RARE, PlatinumAxe.class),
    PLATINUM_HOE(    "Platinum Hoe",      Material.IRON_HOE,     ItemRarity.RARE, PlatinumHoe.class),
    PLATINUM_SHOVEL( "Platinum Shovel",   Material.IRON_SHOVEL,  ItemRarity.RARE, PlatinumShovel.class),
    PLATINUM_SWORD(  "Platinum Sword",    Material.IRON_SWORD,   ItemRarity.RARE, PlatinumSword.class),
    PLATINUM_HATCHET("Platinum Hatchet",  Material.IRON_AXE,     ItemRarity.RARE, PlatinumHatchet.class),
    PLATINUM_BOW(    "Platinum Bow",      Material.BOW,             ItemRarity.RARE, PlatinumBow.class),
    PLATINUM_SPEAR(  "Platinum Spear",    Material.IRON_SPEAR,      ItemRarity.RARE, PlatinumSpear.class),

    PLATINUM_BOOTS(     "Platinum Boots",      Material.IRON_BOOTS,      ItemRarity.RARE, PlatinumBoots.class),
    PLATINUM_LEGGINGS(  "Platinum Leggings",   Material.IRON_LEGGINGS,   ItemRarity.RARE, PlatinumLeggings.class),
    PLATINUM_CHESTPLATE("Platinum Chestplate", Material.IRON_CHESTPLATE, ItemRarity.RARE, PlatinumChestplate.class),
    PLATINUM_HELMET(    "Platinum Helmet",     Material.IRON_HELMET,     ItemRarity.RARE, PlatinumHelmet.class),

    // AETHERIUM
    GRAVITITE_SHARDS("Gravitite Shards", Material.NETHER_BRICK, ItemRarity.RARE, CraftEngineBlueprint.class),
    AETHERIUM_INGOT("Aetherium Ingot", Material.IRON_INGOT, ItemRarity.EPIC, AetheriumIngot.class),

    // AETHERIUM_DRILL_HEAD("Aetherium Drill Head", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, AetheriumDrillHead.class),
    // AETHERIUM_DRILL_BASE("Aetherium Drill Base", Material.PRISMARINE_SHARD, ItemRarity.UNCOMMON, AetheriumDrillBase.class),

    AETHERIUM_PICKAXE("Aetherium Pickaxe",  Material.DIAMOND_PICKAXE, ItemRarity.EPIC, AetheriumPickaxe.class),
    AETHERIUM_AXE(    "Aetherium Axe",      Material.DIAMOND_AXE,     ItemRarity.EPIC, AetheriumAxe.class),
    AETHERIUM_HOE(    "Aetherium Hoe",      Material.DIAMOND_HOE,     ItemRarity.EPIC, AetheriumHoe.class),
    AETHERIUM_SHOVEL( "Aetherium Shovel",   Material.DIAMOND_SHOVEL,  ItemRarity.EPIC, AetheriumShovel.class),
    AETHERIUM_SWORD(  "Aetherium Sword",    Material.DIAMOND_SWORD,   ItemRarity.EPIC, AetheriumSword.class),
    AETHERIUM_HATCHET("Aetherium Hatchet",  Material.DIAMOND_AXE,     ItemRarity.EPIC, AetheriumHatchet.class),
    AETHERIUM_BOW(    "Aetherium Bow",      Material.BOW,             ItemRarity.EPIC, AetheriumBow.class),
    AETHERIUM_SPEAR(  "Aetherium Spear",    Material.IRON_SPEAR,      ItemRarity.EPIC, AetheriumSpear.class),

    AETHERIUM_BOOTS(     "Aetherium Boots",      Material.IRON_BOOTS,      ItemRarity.EPIC, AetheriumBoots.class),
    AETHERIUM_LEGGINGS(  "Aetherium Leggings",   Material.IRON_LEGGINGS,   ItemRarity.EPIC, AetheriumLeggings.class),
    AETHERIUM_CHESTPLATE("Aetherium Chestplate", Material.IRON_CHESTPLATE, ItemRarity.EPIC, AetheriumChestplate.class),
    AETHERIUM_HELMET(    "Aetherium Helmet",     Material.IRON_HELMET,     ItemRarity.EPIC, AetheriumHelmet.class),

    SKYROOT_BUTTON("Skyroot Button", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_DOOR("Skyroot Door", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_FENCE("Skyroot Fence", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_FENCE_GATE("Skyroot Fence Gate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_LEAVES("Skyroot Leaves", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_LOG("Skyroot Log", Material.NETHER_BRICK, ItemRarity.COMMON, SkyrootLog.class),
    SKYROOT_PLANKS("Skyroot Planks", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_PRESSURE_PLATE("Skyroot Pressure Plate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_SAPLING("Skyroot Sapling", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_SLAB("Skyroot Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_STAIRS("Skyroot Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_TRAPDOOR("Skyroot Trapdoor", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SKYROOT_WOOD("Skyroot Wood", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_SKYROOT_LOG("Stripped Skyroot Log", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_SKYROOT_WOOD("Stripped Skyroot Wood", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    GOLDEN_OAK_BUTTON("Golden Oak Button", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_DOOR("Golden Oak Door", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_FENCE("Golden Oak Fence", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_FENCE_GATE("Golden Oak Fence Gate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_LEAVES("Golden Oak Leaves", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_LOG("Golden Oak Log", Material.NETHER_BRICK, ItemRarity.COMMON, GoldenOakLog.class),
    GOLDEN_OAK_PLANKS("Golden Oak Planks", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_PRESSURE_PLATE("Golden Oak Pressure Plate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_SAPLING("Golden Oak Sapling", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_SLAB("Golden Oak Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_STAIRS("Golden Oak Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_TRAPDOOR("Golden Oak Trapdoor", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    GOLDEN_OAK_WOOD("Golden Oak Wood", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_GOLDEN_OAK_LOG("Stripped Golden Oak Log", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_GOLDEN_OAK_WOOD("Stripped Golden Oak Wood", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    CRYSTAL_SAPLING("Crystal Sapling", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    CRYSTAL_LEAVES("Crystal Leaves", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    CRYSTAL_FRUIT_LEAVES("Crystal Fruit Leaves", Material.NETHER_BRICK, ItemRarity.UNCOMMON, CraftEngineBlueprint.class),
    CRYSTAL_FRUIT("Crystal Fruit", Material.NETHER_BRICK, ItemRarity.COMMON, CrystalFruit.class),

    // END
    NULLYIUM("Nullyium", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    BINARY_BUTTON("Binary Button", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_DOOR("Binary Door", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_FENCE("Binary Fence", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_FENCE_GATE("Binary Fence Gate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_LEAVES("Binary Nodes", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_LOG("Binary Branch", Material.NETHER_BRICK, ItemRarity.COMMON, BinaryLog.class),
    BINARY_PLANKS("Binary Planks", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_PRESSURE_PLATE("Binary Pressure Plate", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_SAPLING("Binary Root", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_SLAB("Binary Slab", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_STAIRS("Binary Stairs", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_TRAPDOOR("Binary Trapdoor", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    BINARY_WOOD("Binary Arborescence", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_BINARY_LOG("Stripped Binary Branch", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    STRIPPED_BINARY_WOOD("Stripped Binary Arborescence", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    SMOKY_QUARTZ_ORE("Smoky Quartz Ore", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SMOKY_QUARTZ("Smoky Quartz", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SMOKY_QUARTZ_BLOCK("Smoky Quartz Block", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SMOOTH_SMOKY_QUARTZ("Smooth Smoky Quartz", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SMOKY_QUARTZ_BRICKS("Smoky Quartz Bricks", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    SMOKY_QUARTZ_PILLAR("Smoky Quartz Pillar", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    CHISELED_SMOKY_QUARTZ_BLOCK("Chiseled Smoky Quartz Block", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    POINTER_PRISM("Pointer Prism", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),
    POINTER_PRISM_BLOCK("Pointer Prism Block", Material.NETHER_BRICK, ItemRarity.COMMON, CraftEngineBlueprint.class),

    KITCHEN_TILES("Kitchen Tiles", Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    ROPE(       "Rope",            Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    ROPE_COIL(       "Rope Coil",  Material.NETHER_BRICK,     ItemRarity.COMMON, CraftEngineBlueprint.class),
    ;

    public final String ItemName;
    public final Material DisplayMaterial;
    public final ItemRarity DefaultRarity;
    public final boolean WantGlow;
    public int Worth = 0;
    public String ModelDir = null;
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

    CustomItemType(String name, Material material, ItemRarity rarity, boolean WantGlow, int worth, String modelDir) {
        this(name, material, rarity, WantGlow, SellableResource.class);
        this.Worth = worth;
        this.ModelDir = modelDir;
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
