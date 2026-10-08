package xyz.devvydont.smprpg.util.java;

import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

/**
 * Java helper class for instantiating sealed classes, something Kotlin cannot do
 * as it views sealed classes as abstract, and not instantiatable.
 */

public class SealedInstantiators {
    public static RecipeChoice getMaterialChoiceInstance(Material... choices) {
        return new RecipeChoice.MaterialChoice(choices);
    }
}