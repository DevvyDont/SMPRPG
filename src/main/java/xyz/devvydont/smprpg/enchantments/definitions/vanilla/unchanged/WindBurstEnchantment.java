package xyz.devvydont.smprpg.enchantments.definitions.vanilla.unchanged;

import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import xyz.devvydont.smprpg.enchantments.definitions.vanilla.UnchangedEnchantment;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

public class WindBurstEnchantment extends UnchangedEnchantment {

    public WindBurstEnchantment(TypedKey<Enchantment> key) {
        super(key);
    }

    @Override
    public TagKey<ItemType> getItemTypeTag() {
        return ItemTypeTagKeys.ENCHANTABLE_MACE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ComponentUtils.create("Wind Burst");
    }

    @Override
    public @NotNull Component getDescription() {
        return ComponentUtils.merge(
            ComponentUtils.create("Propel upwards "),
            ComponentUtils.create(String.valueOf(getLevel() * 5), NamedTextColor.GREEN),
            ComponentUtils.create(" blocks when dealing damage")
        );
    }

    @Override
    public int getSkillRequirement() {
        return 34;
    }
}
