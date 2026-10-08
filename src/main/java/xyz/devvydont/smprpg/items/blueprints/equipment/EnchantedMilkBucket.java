package xyz.devvydont.smprpg.items.blueprints.equipment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.ChargedItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IHeaderDescribable;
import xyz.devvydont.smprpg.services.ActionBarService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.List;

public class EnchantedMilkBucket extends ChargedItemBlueprint implements IHeaderDescribable, Listener {

    public EnchantedMilkBucket(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getHeader(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("Consume to cleanse ").append(ComponentUtils.create("ALL", NamedTextColor.GOLD)),
                ComponentUtils.create("status effects you have!")
        );
    }

    @Override
    public int getMaxCharges(ItemStack item) {
        return 32;
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.EQUIPMENT;
    }

    @EventHandler
    public void onConsumeEnchantedMilk(PlayerItemConsumeEvent event) {

        if (!isItemOfType(event.getItem()))
            return;

        ItemStack milk = event.getItem();
        useCharge(event.getPlayer(), milk);
        event.getPlayer().getInventory().setItem(event.getHand(), milk);
        event.setCancelled(true);
        event.getPlayer().clearActivePotionEffects();
        SMPRPG.getService(ActionBarService.class).addActionBarComponent(event.getPlayer(), ActionBarService.ActionBarSource.MISC, ComponentUtils.create("CLEANSED!", NamedTextColor.GREEN), 2);
    }
}
