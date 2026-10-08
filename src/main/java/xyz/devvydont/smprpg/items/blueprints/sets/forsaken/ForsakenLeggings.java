package xyz.devvydont.smprpg.items.blueprints.sets.forsaken;

import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.services.ItemService;

public class ForsakenLeggings extends ForsakenArmorSet {

    public static final int DEFENSE = 140;
    public static final int HEALTH = 20;
    public static final double STRENGTH = .35;

    public ForsakenLeggings(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public int getDefense() {
        return DEFENSE;
    }

    @Override
    public int getHealth() {
        return HEALTH;
    }

    @Override
    public double getStrength() {
        return STRENGTH;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.LEGGINGS;
    }
}
