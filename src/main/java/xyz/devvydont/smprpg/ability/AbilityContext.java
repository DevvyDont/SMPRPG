package xyz.devvydont.smprpg.ability;

import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record AbilityContext(@NotNull LivingEntity caster, @Nullable EquipmentSlot hand) {

}
