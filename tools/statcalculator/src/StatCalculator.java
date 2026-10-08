import enums.ArmorPiece;
import enums.ItemRarity;
import enums.StatSource;

import java.util.Scanner;

/**
 * A utility application to aid in the creation and modification of items, stats, and attributes of entities.
 * This code is explicitly a developer utility and does not affect the project in any way.
 */
public class StatCalculator {

    private enum Operation {
        SET_LEVEL("l", "Set the calculator level"),
        CREATE_ARMOR("armor", "Create armor"),
        BATCH_ARMOR("batch", "Batch armor from stdin (level,rarity,combat[,name] per line)"),
        CREATE_SWORD("sword", "Create sword"),
        CREATE_AXE("axe", "Create axe"),
        DUMP_PLAYER_EXPECTATIONS("player", "Dump player expectations"),
        DUMP_DPS_TABLE("dps", "Dump DPS Table"),
        DUMP_HP_TABLE("hp", "Dump HP/DEF Table"),
        DUMP_SKILLS_TABLE("skills", "Dump Skills Table"),
        QUIT("q", "Quit"),
        ;

        public final String Key;
        public final String Description;

        Operation(String key, String description) {
            Key = key;
            this.Description = description;
        }

        public static Operation get(String key) {
            for (Operation op : Operation.values()) {
                if (op.Key.equalsIgnoreCase(key)) {
                    return op;
                }
            }
            return null;
        }
    }

    /**
     * How many times should an enemy hit a player in order to kill them assuming they are equal in power and
     * are not receiving any sort of health regeneration?
     * Higher values will allow players to survive easier, while lower values will make enemies more lethal.
     */
    private final static double ENEMY_HITS_TO_KILL_PLAYER = 8.0;

    /**
     * How many times should a player have to hit an enemy in order to kill it assuming they are equal in power and
     * keeping up with their stats and all mechanics? These hits will also assume they are not landing critical hits,
     * meaning that this number will most likely *feel* like it's actually set lower, since optimal players will
     * typically maximize the number of crits they are landing.
     */
    private final static double PLAYER_HITS_TO_KILL_ENEMY = 3.75;

    /**
     * A modifier for how much damage should be applied to swords. Since swords have a relatively quick attack speed,
     * full charge hits can happen multiple times a second meaning that DPS thresholds can be hit faster with
     * multiple hits.
     * Increase this to make swords more powerful.
     */
    private final static double SWORD_DPS = 0.75;

    /**
     * A modifier for how much damage should be applied to axes. Since axes have a relatively slower attack speed,
     * full charge hits happen less frequently meaning DPS thresholds are harder to hit.
     * Increase this to make axes more powerful.
     */
    private final static double AXE_DPS = 1.1;

    /**
     * How effective should defense be at mitigating damage? This contributes to a standard diminshing returns formula
     * where higher values mean that defense needs to be HIGHER in order to be more effective, where lower values
     * means that you can have less defense be more effective sooner.
     * A good way to think of this, is that once you hit whatever value is set below in defense, you will achieve
     * 50% damage reduction. When doubled from there, it's then increased to 66% damage reduction. When tripled, 75%...
     *
     * In even simpler math, let's say that K factor is 100. For every 100 defense you get, you are effectively
     * multiplying your health based on how m any times your defense divides into K factor after adding 1.
     * 0 / 100 + 1 = 1x
     * 100 / 100 + 1 = 2x
     * 200 / 100 + 1 = 3x
     * 500 / 100 + 1 = 6x
     * 1500 / 100 + 1 = 16x
     */
    private final static int DEFENSE_K_FACTOR = 100;

    private final static int MAX_LEVEL = 100;
    private final static int STARTING_HEALTH = 100;

    /**
     * How much total defense (from gear) a max-level (MAX_LEVEL) player is expected to have.
     * DEFENSE_AT_MAX_LEVEL / DEFENSE_K_FACTOR + 1 is the endgame defense multiplier "M".
     * 1900 / 100 + 1 = 20x effective health from defense at level 100.
     */
    private final static int DEFENSE_AT_MAX_LEVEL = 1900;

    /**
     * The defense curve is a logistic (S-curve): low at low levels (so skill HP carries early-game
     * survivability), steepest growth through the mid game, then easing off toward the cap (gentle
     * 80->100). MIDPOINT is the level of fastest growth; STEEPNESS controls how sharp the ramp is.
     * The curve is scaled so it passes through DEFENSE_AT_MAX_LEVEL at MAX_LEVEL.
     */
    private final static double DEFENSE_CURVE_MIDPOINT = 50.0;
    private final static double DEFENSE_CURVE_STEEPNESS = 0.05;

    /**
     * How much max HP a player gains per skill level, per HP-granting skill. The game grants this
     * directly (+2/level), so the calculator treats it as a known, fixed contribution.
     */
    private final static double SKILL_HP_PER_LEVEL = 2.0;

    /**
     * How many skills grant HP. Every core skill rewards the same per-level HP, so a player's total
     * skill HP at an average skill level L is SKILL_HP_PER_LEVEL * NUM_HP_SKILLS * L.
     */
    private final static int NUM_HP_SKILLS = 7;

    /**
     * The fraction of a player's total defense that should come from the armor SET itself.
     * The remainder is left as headroom for defensive enchantments (Protection, etc.), which our
     * augment system treats as optional. 0.85 = armor carries 85% of defense, enchants up to 15%.
     */
    private final static double ARMOR_DEFENSE_SHARE = 0.85;

    /**
     * Survivability multiplier for non-combat sets (farming/fishing/mining/etc.). They should still
     * be wearable, just worse than a dedicated combat set. 0.8 = 20% less HP/DEF.
     */
    private final static double NON_COMBAT_PENALTY = 0.8;

    /**
     * All armor stat outputs are rounded to the nearest multiple of this so item numbers look clean
     * (e.g. 150/155/160 instead of 147/153/161).
     */
    private final static int CLEAN_ROUNDING = 5;

    /*
     * The core of how stat scaling should player out mid/late game.
     * A higher number will make the game feel more exponential.
     * (NOTE: This gets out of control FAST)
     */
    private final static double EHP_SCALING_FACTOR = 1.0964782;  // 1.075 results in ~100k, 1.0964782 results in ~1M

    /**
     * The level the calculator is set to run calculations for.
     */
    private int _level = 25;

    /**
     * A single shared stdin scanner. Using one instance everywhere avoids two Scanners fighting over
     * System.in (which corrupts input and breaks piped/scripted runs).
     */
    private final Scanner _scanner = new Scanner(System.in);

    /**
     * Calculates what a standard enemy/player's effective health should be at a certain level.
     * Note that this does not factor in defense, so it is assumed that the entity will have 0 defense if they
     * want to use this health value.
     * @param level Level of an entity.
     * @return How much health (EHP) they should have.
     */
    private int calculateExpectedHealth(int level) {
        return (int) Math.ceil(STARTING_HEALTH*Math.pow(EHP_SCALING_FACTOR, level));
    }

    /**
     * Work out how much total defense (from gear) is expected of a player at a certain level.
     * Defense is now the independent variable that defines the HP/defense split: we pick a defense
     * curve, and a player's HP is whatever is left over to reach their target EHP.
     *
     * The curve is a logistic S-curve (see DEFENSE_CURVE_MIDPOINT/STEEPNESS): low early so skill HP
     * carries the early game, steepest through the mid game, easing off toward DEFENSE_AT_MAX_LEVEL.
     * @param level The level of the player.
     * @return How much defense they should have.
     */
    private int calculateExpectedDefense(int level) {
        // Logistic S-curve, normalized so that level MAX_LEVEL yields exactly DEFENSE_AT_MAX_LEVEL.
        var scale = DEFENSE_AT_MAX_LEVEL / logistic(MAX_LEVEL);
        return (int) Math.round(scale * logistic(level));
    }

    private double logistic(int level) {
        return 1.0 / (1.0 + Math.exp(-DEFENSE_CURVE_STEEPNESS * (level - DEFENSE_CURVE_MIDPOINT)));
    }

    /**
     * The effective-health multiplier granted by a player's expected defense at a given level.
     * EHP = HP * defenseMultiplier, so this is simply 1 + defense/K.
     */
    private double calculateExpectedDefenseMultiplier(int level) {
        return 1.0 + calculateExpectedDefense(level) / (double) DEFENSE_K_FACTOR;
    }

    /**
     * Players have a defense stat, so their raw HP doesn't need to reach their full EHP; defense
     * multiplies it the rest of the way. HP = targetEHP / defenseMultiplier, floored at the base
     * starting health so low-level players always have at least their base pool.
     * @param level The level of the player.
     * @return How much raw (non-effective) health a player should have.
     */
    private int calculateExpectedPlayerHealth(int level) {
        var hp = (int) Math.ceil(calculateExpectedHealth(level) / calculateExpectedDefenseMultiplier(level));
        return Math.max(hp, STARTING_HEALTH);
    }

    /**
     * How much max HP a player is expected to have from skills at a given average skill level.
     * Every HP-granting skill pays a flat +2/level (no tier ramping), so this is simply
     * SKILL_HP_PER_LEVEL * NUM_HP_SKILLS * level.
     * @param level The average skill level of the player.
     * @return Fixed skill HP contribution.
     */
    private double calculateSkillHp(int level) {
        return SKILL_HP_PER_LEVEL * NUM_HP_SKILLS * level;
    }

    /**
     * Calculates how much damage a standard/average enemy should do at a certain level.
     * @param level Level of an enemy.
     * @return How much damage on average they should be doing a second.
     */
    private int calculateEnemyDps(int level) {
        // Since players and enemies are meant to be on the same playing field power wise, we can just divide
        // the entity's health by however many hits it should take to kill. Very simple :p
        return (int) Math.ceil(calculateExpectedHealth(level) / ENEMY_HITS_TO_KILL_PLAYER);
    }

    /**
     * Calculates how much damage a standard player should be doing per second at a certain level.
     * Keep in mind, this is the TOTAL, meaning that all augments of a player should be contributing towards this total
     * when dealing damage. As newer mechanics introduce to a player, the reliance on base stats and gear needs to
     * slowly and gradually decrease, as a measure of preventing power creep.
     * @param level The level of a player.
     * @return
     */
    private int calculatePlayerDps(int level) {
        // Similarly to enemy DPS...
        return (int) Math.ceil(calculateExpectedHealth(level) / PLAYER_HITS_TO_KILL_ENEMY);
    }

    /**
     * Works out what damage multiplier will be applied when an entity has a certain defense rating.
     * Keep in mind this is the multiplier itself, and not the damage reduction (achieved by adding -1).
     * Obviously, higher defense = more damage reduction (with diminishing returns!).
     * @param defense How much defense to use for damage reduction.
     * @return A floating point number from 0-1, representing what to multiply damage by in order to apply resistance.
     */
    private float calculateDamageMultiplierWithDefense(int defense) {
        return (float)DEFENSE_K_FACTOR / (defense + DEFENSE_K_FACTOR);
    }

    /**
     * The inverse of damage defense multiplier. This is simply a different way of looking at the same concept.
     * Use this method if you want to analyze how much damage is resisted.
     * @param defense How much defense to use for damage reduction.
     * @return A floating point number from 0-1, representing what percent damage reduction an entity has with this defense.
     */
    private float calculateDamageReductionWithDefense(int defense) {
        return 1 - calculateDamageMultiplierWithDefense(defense);
    }

    private void operationSetLevel() {
        System.out.println("Please enter a level you want to use.");
        try {
            _level = Integer.parseInt(_scanner.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Failed to read level (level is unchanged): " + e.getMessage());
            return;
        }
        System.out.println("Set level to " + _level);
    }

    private void operationDumpPlayerExpectationsTable() {
        for (var i = 1; i <= 120; i++)
            System.out.println(i + ": " + calculateExpectedPlayerHealth(i) + "HP," + calculateExpectedDefense(i) + "DEF" + " EHP=" + calculateExpectedHealth(i) + " DPS=" + calculatePlayerDps(i) + " STR=" + (1.0 + i/11.0) + "x" + " realizedEHPx" + String.format("%.2f", calculatePlayerOvershoot(i)));
    }

    /**
     * How much the EHP of a skill-average player in a perfectly-budgeted armor set OVERSHOOTS the
     * target EHP curve. Because skills + base HP can exceed the raw HP target at low levels (where
     * armor HP floors at 0), early-game players end up tankier than the curve intends - a deliberate
     * "feel OP early, then feel the scaling" effect. 1.0 = exactly on curve.
     * @param level The level / average skill level of the player.
     * @return The ratio of realized EHP to target EHP (>= 1.0).
     */
    private double calculatePlayerOvershoot(int level) {
        var realizedHp = Math.max((double) calculateExpectedPlayerHealth(level), STARTING_HEALTH + calculateSkillHp(level));
        var realizedEhp = realizedHp * calculateExpectedDefenseMultiplier(level);
        return realizedEhp / calculateExpectedHealth(level);
    }

    private void operationDumpHpTable() {
        for (var i = 1; i <= 120; i++)
            System.out.println(i + ": " + "player=" + calculateExpectedPlayerHealth(i) + "HP," + calculateExpectedDefense(i) + "DEF" + " EHP=" + calculateExpectedHealth(i));
    }

    private void operationDumpDpsTable() {
        for (var i = 1; i <= 120; i++)
            System.out.println(i + ": " + "player=" + calculatePlayerDps(i) + " entity=" + calculateEnemyDps(i));
    }

    private void operationCreateSword() {
        System.out.println("Creating sword for level " + _level);
        for (ItemRarity rarity : ItemRarity.values())
            System.out.println(rarity + ": " + (StatSource.getExpectedWeaponDamage(_level, rarity, SWORD_DPS)));
        var rareDmg = StatSource.getExpectedWeaponDamage(_level, ItemRarity.RARE, SWORD_DPS);
        var expectedMultiplier = calculatePlayerDps(_level) / (rareDmg * SWORD_DPS);
        System.out.println("Player should have a damage multiplier of x" + expectedMultiplier + " to achieve a DPS of " + calculatePlayerDps(_level));
    }

    private void operationCreateAxe() {
        System.out.println("Creating axe for level " + _level);
        for (ItemRarity rarity : ItemRarity.values())
            System.out.println(rarity + ": " + (StatSource.getExpectedWeaponDamage(_level, rarity, AXE_DPS)));
        var rareDmg = StatSource.getExpectedWeaponDamage(_level, ItemRarity.RARE, AXE_DPS);
        var expectedMultiplier = calculatePlayerDps(_level) / (rareDmg * AXE_DPS);
        System.out.println("Player should have a damage multiplier of x" + expectedMultiplier + " to achieve a DPS of " + calculatePlayerDps(_level));
    }

    /**
     * Work out how much HP and DEF the armor SET as a whole should provide at a given level.
     * HP heavy-lifting is done by skills (and misc world items); the armor set only tops off however
     * much HP is still needed to reach the target. Defense is mostly on the armor set
     * (ARMOR_DEFENSE_SHARE), with the rest left as headroom for optional defensive enchantments.
     * Non-combat sets take a small survivability penalty.
     * @return [armorHp, armorDef] for the whole set (before per-piece weighting/rarity).
     */
    private double[] computeArmorPools(int level, boolean combat) {
        var targetHp = calculateExpectedPlayerHealth(level);
        var skillHp = calculateSkillHp(level);
        var totalDef = (double) calculateExpectedDefense(level);

        var armorHp = Math.max(0.0, targetHp - STARTING_HEALTH - skillHp);
        var armorDef = ARMOR_DEFENSE_SHARE * totalDef;

        if (!combat) {
            armorHp *= NON_COMBAT_PENALTY;
            armorDef *= NON_COMBAT_PENALTY;
        }
        return new double[]{armorHp, armorDef};
    }

    /**
     * Rounds a stat to the nearest CLEAN_ROUNDING so item numbers look intentional, not random.
     */
    private static long roundClean(double value) {
        return Math.round(value / CLEAN_ROUNDING) * (long) CLEAN_ROUNDING;
    }

    private void operationCreateArmor() {
        System.out.println("Creating armor for level " + _level);

        var pools = computeArmorPools(_level, true);
        var armorHp = pools[0];
        var armorDef = pools[1];
        var enchDef = (1.0 - ARMOR_DEFENSE_SHARE) * calculateExpectedDefense(_level);

        System.out.println("Target: " + calculateExpectedPlayerHealth(_level) + "HP / " + calculateExpectedDefense(_level) + "DEF  (EHP " + calculateExpectedHealth(_level) + ")");
        System.out.println("  Skills " + calculateSkillHp(_level) + "HP leave " + armorHp + "HP for the set; set carries " + armorDef + "DEF (" + enchDef + "DEF enchant headroom).");

        for (var rarity : ItemRarity.values()) {
            var sb = new StringBuilder(rarity + ": ");
            for (var piece : ArmorPiece.values()) {
                var def = roundClean(piece.calculateStatTarget(armorDef, rarity));
                var hp = roundClean(piece.calculateStatTarget(armorHp, rarity));
                sb.append(piece).append("=").append(def).append("DEF/").append(hp).append("HP").append(" | ");
            }
            System.out.println(sb);
        }
    }

    /**
     * Batch mode: read armor specs from stdin (one per line: "level,rarity,combat[,name]") and print
     * the clean-rounded per-piece DEF/HP for each. A blank line finishes. Lets us regenerate every
     * armor set in the game in one pass.
     */
    private void operationBatch() {
        System.out.println("Enter sets as 'level,rarity,combat[,name]' (blank line to finish):");
        while (_scanner.hasNextLine()) {
            var line = _scanner.nextLine().trim();
            if (line.isEmpty())
                break;
            try {
                var parts = line.split(",");
                var level = Integer.parseInt(parts[0].trim());
                var rarity = ItemRarity.valueOf(parts[1].trim().toUpperCase());
                var combat = Boolean.parseBoolean(parts[2].trim());
                var name = parts.length > 3 ? parts[3].trim() : ("L" + level + " " + rarity);

                var pools = computeArmorPools(level, combat);
                var sb = new StringBuilder(name + " (L" + level + " " + rarity + (combat ? "" : " NON-COMBAT") + "): ");
                for (var piece : ArmorPiece.values()) {
                    var def = roundClean(piece.calculateStatTarget(pools[1], rarity));
                    var hp = roundClean(piece.calculateStatTarget(pools[0], rarity));
                    sb.append(piece).append("=").append(def).append("/").append(hp).append(" ");
                }
                System.out.println(sb);
            } catch (Exception e) {
                System.out.println("  ! could not parse '" + line + "': " + e.getMessage());
            }
        }
    }

    private void operationDumpSkillExpectations() {
        System.out.println("Skill HP expectations (+" + SKILL_HP_PER_LEVEL + " HP/level x " + NUM_HP_SKILLS + " skills, flat)");
        for (var i = 1; i <= 120; i++)
            System.out.println(i + ": " + calculateSkillHp(i) + "HP (avg skill level " + i + ")");
    }

    private void operationQuit() {
        System.out.println("Exiting! Bye bye :)");
        System.exit(0);
    }

    private void operationMissingLogic() {
        System.out.println("Missing logic for the selected handler!");
    }

    public Runnable getHandler(String key) {

        Operation op = Operation.get(key);
        if (op == null) {
            return null;
        }

        return switch (op) {
            case SET_LEVEL -> this::operationSetLevel;
            case CREATE_ARMOR -> this::operationCreateArmor;
            case BATCH_ARMOR -> this::operationBatch;
            case CREATE_SWORD -> this::operationCreateSword;
            case CREATE_AXE -> this::operationCreateAxe;
            case DUMP_PLAYER_EXPECTATIONS -> this::operationDumpPlayerExpectationsTable;
            case DUMP_DPS_TABLE -> this::operationDumpDpsTable;
            case DUMP_HP_TABLE -> this::operationDumpHpTable;
            case DUMP_SKILLS_TABLE -> this::operationDumpSkillExpectations;
            case QUIT -> this::operationQuit;
            default -> this::operationMissingLogic;
        };
    }

    public static void main(String[] args) {
        var calculator = new StatCalculator();
        while (true) {
            System.out.println();
            System.out.println("What would you like to do? Enter the characters before the colon to select an option.");
            for (Operation operation : Operation.values())
                System.out.println(operation.Key + ": " + operation.Description);

            var handler = calculator.getHandler(calculator._scanner.nextLine());
            if (handler == null) {
                System.out.println("Invalid input! Try again.\n");
                continue;
            }

            System.out.println();
            handler.run();
            System.out.println();
        }

    }

}
