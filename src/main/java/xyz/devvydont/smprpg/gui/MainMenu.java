package xyz.devvydont.smprpg.gui;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.fishing.gui.FishingPoolViewerMenu;
import xyz.devvydont.smprpg.fishing.gui.LootTypeChancesMenu;
import xyz.devvydont.smprpg.gui.base.MenuBase;
import xyz.devvydont.smprpg.gui.enchantments.MagicMenu;
import xyz.devvydont.smprpg.gui.player.InterfaceStats;
import xyz.devvydont.smprpg.gui.player.MenuDifficultyChooser;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.skills.SkillGlobals;
import xyz.devvydont.smprpg.skills.SkillInstance;
import xyz.devvydont.smprpg.skills.SkillType;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static xyz.devvydont.smprpg.util.formatting.ComponentUtils.*;

/**
 * A simple menu that contains buttons to open other menus!
 */
public class MainMenu extends MenuBase {

    private static final int ROWS = 5;

    private static final char BAR_CHARACTER = '▏';
    private static final int BAR_NUM_CHARS = 100;

    /*
    Indexes of buttons.
     */
    private static final int PLAYER_MENU = 13;

    private static final int COMBAT_INDEX = 19;
    private static final int MINING_INDEX = 20;
    private static final int FISHING_INDEX = 21;
    private static final int MAGIC_INDEX = 23;
    private static final int WOODCUTTING_INDEX = 24;
    private static final int FARMING_INDEX = 25;

    private static final int DIFFICULTY_INDEX = 41;
    private static final int SETTINGS_INDEX = 42;

    public MainMenu(@NotNull Player player) {
        super(player, ROWS);
    }

    @Override
    protected void handleInventoryClicked(InventoryClickEvent event) {
        event.setCancelled(true);
        this.playInvalidAnimation();
    }

    @Override
    protected void handleInventoryOpened(InventoryOpenEvent event) {
        event.titleOverride(Component.text("Main Menu"));
        render();
    }

    public void render() {

        this.setBorderFull();
        this.setBackButton();

        this.setButton(PLAYER_MENU, this.getPlayerDisplay(), e -> this.openSubMenu(new InterfaceStats(this.player, this.player, this)));
        this.setButton(COMBAT_INDEX, this.getCombatDisplay(), e -> this.playInvalidAnimation());
        this.setButton(MINING_INDEX, this.getMiningDisplay(), e -> this.playInvalidAnimation());
        this.setButton(FISHING_INDEX, this.getFishingDisplay(), e -> this.openSubMenu(new LootTypeChancesMenu(this.player, this)));
        this.setButton(FARMING_INDEX, this.getFarmingDisplay(), e -> this.playInvalidAnimation());
        this.setButton(WOODCUTTING_INDEX, this.getWoodcuttingDisplay(), e -> this.playInvalidAnimation());
        this.setButton(MAGIC_INDEX, this.getMagicDisplay(), e -> this.openSubMenu(new MagicMenu(this.player, this)));

        this.setButton(DIFFICULTY_INDEX, this.getDifficultyDisplay(), e -> this.openSubMenu(new MenuDifficultyChooser(this.player, this)));
    }

    private @NotNull ItemStack getDifficultyDisplay() {
        var item = InterfaceUtil.getNamedItemWithDescription(
                Material.NETHER_STAR,
                ComponentUtils.create("Difficulty", GREEN),
                ComponentUtils.create("Check and/or lower your difficulty!"),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view difficulty options!", YELLOW)
        );
        item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(this.player.getPlayerProfile()));
        return item;
    }

    private @NotNull ItemStack getPlayerDisplay() {
        var item = InterfaceUtil.getNamedItemWithDescription(
                Material.PLAYER_HEAD,
                ComponentUtils.create("Your Profile", GOLD),
                ComponentUtils.create("View your statistics and various information"),
                merge(create("You can check other players by using "), create("/stats", GREEN)),
                ComponentUtils.EMPTY,
                ComponentUtils.create("Click to view information about you!", YELLOW)
                );
        item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(this.player.getPlayerProfile()));
        return item;
    }

    private @NotNull ItemStack getCombatDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("View information about various"));
        lore.add(ComponentUtils.create("creatures and their drops!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getCombatSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about combat!", YELLOW));
        lore.add(EMPTY);
        lore.add(create("Will be implemented at a later date B)", RED));
        return InterfaceUtil.getNamedItemWithDescription(Material.DIAMOND_SWORD, ComponentUtils.create("Combat", GOLD), lore);
    }

    private @NotNull ItemStack getMiningDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("View information about various"));
        lore.add(ComponentUtils.create("ores and materials in the world!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getMiningSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about mining!", YELLOW));
        lore.add(EMPTY);
        lore.add(create("Will be implemented at a later date B)", RED));
        return InterfaceUtil.getNamedItemWithDescription(Material.IRON_PICKAXE, ComponentUtils.create("Mining", GOLD), lore);
    }

    private @NotNull ItemStack getFishingDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("Check to see what you are allowed to"));
        lore.add(ComponentUtils.create("catch and what you have caught so far!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getFishingSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about fishing!", YELLOW));
        return InterfaceUtil.getNamedItemWithDescription(Material.COD, ComponentUtils.create("Fishing", GOLD), lore);
    }

    private @NotNull ItemStack getFarmingDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("View information relating to"));
        lore.add(ComponentUtils.create("growing and maintaining crops!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getFarmingSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about farming!", YELLOW));
        lore.add(EMPTY);
        lore.add(create("Will be implemented at a later date B)", RED));
        return InterfaceUtil.getNamedItemWithDescription(Material.WHEAT, ComponentUtils.create("Farming", GOLD), lore);
    }

    private @NotNull ItemStack getWoodcuttingDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("View information relating to"));
        lore.add(ComponentUtils.create("chopping down trees!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getWoodcuttingSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about woodcutting!", YELLOW));
        lore.add(EMPTY);
        lore.add(create("Will be implemented at a later date B)", RED));
        return InterfaceUtil.getNamedItemWithDescription(Material.DARK_OAK_SAPLING, ComponentUtils.create("Woodcutting", GOLD), lore);
    }

    private @NotNull ItemStack getMagicDisplay() {
        var lore = new ArrayList<Component>();
        lore.add(ComponentUtils.create("View all the ways to magically"));
        lore.add(ComponentUtils.create("increase your potential!"));
        lore.add(EMPTY);
        lore.addAll(formatSkill(SMPRPG.getService(EntityService.class).getPlayerInstance(this.player).getMagicSkill()));
        lore.add(EMPTY);
        lore.add(ComponentUtils.create("Click to view information about magic!", YELLOW));
        lore.add(EMPTY);
        lore.add(create("Will be polished at a later date B)", RED));
        return InterfaceUtil.getNamedItemWithDescription(Material.ENCHANTING_TABLE, ComponentUtils.create("Magic", GOLD), lore);
    }

    private List<Component> formatSkill(SkillInstance skill) {
        var rightBound = SkillGlobals.getExperienceForLevel(skill.getNextLevel());
        var progress = skill.getExperienceProgress();
        var percentage = (double) progress / rightBound;
        var greenBars = (int) (percentage * BAR_NUM_CHARS);
        var grayBars = BAR_NUM_CHARS-greenBars;
        var bar = merge(create(StringUtils.repeat(BAR_CHARACTER, greenBars), GREEN),
                create(StringUtils.repeat(BAR_CHARACTER, grayBars), DARK_GRAY)).decoration(TextDecoration.BOLD, true);

        var df = new DecimalFormat("#,###");

        return List.of(
                merge(create("You are currently: "), create(skill.type.getDisplayName() + " " + skill.getLevel(), AQUA)),
                merge(create(df.format(progress) + "XP ", GRAY), bar, create(" " + df.format(rightBound) + "XP", GRAY))
        );
    }
}
