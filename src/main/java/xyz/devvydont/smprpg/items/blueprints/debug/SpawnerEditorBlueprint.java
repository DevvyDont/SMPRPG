package xyz.devvydont.smprpg.items.blueprints.debug;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.spawning.EntitySpawner;
import xyz.devvydont.smprpg.gui.spawner.InterfaceSpawnerMainMenu;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.items.ItemClassification;
import xyz.devvydont.smprpg.items.base.CustomItemBlueprint;
import xyz.devvydont.smprpg.items.interfaces.IFooterDescribable;
import xyz.devvydont.smprpg.services.EntityService;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.formatting.ComponentUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpawnerEditorBlueprint extends CustomItemBlueprint implements Listener, IFooterDescribable {

    public SpawnerEditorBlueprint(ItemService itemService, CustomItemType type) {
        super(itemService, type);
    }

    @Override
    public List<Component> getFooter(ItemStack itemStack) {
        return List.of(
                ComponentUtils.create("Used to interact with"),
                ComponentUtils.create("and edit custom spawner"),
                ComponentUtils.create("entities in the world")
        );
    }

    @Override
    public ItemClassification getItemClassification() {
        return ItemClassification.ITEM;
    }

    private boolean canUse(Player player) {
        return player.isOp() || player.permissionValue("smprpg.items.spawneditor.view").toBooleanOrElse(false);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractWhileHoldingEditor(PlayerInteractEvent event) {

        ItemStack item = event.getItem();
        if (!isItemOfType(item))
            return;

        event.setCancelled(true);

        if (!canUse(event.getPlayer())){
            event.getPlayer().sendMessage(ComponentUtils.error("You lack permissions to use this item!"));
            return;
        }

        Collection<Entity> nearbyDisplays = event.getPlayer().getWorld().getNearbyEntitiesByType(CustomEntityType.SPAWNER.Type.getEntityClass(), event.getPlayer().getEyeLocation(), 2.5);
        List<EntitySpawner> nearbySpawners = new ArrayList<>();
        for (Entity display : nearbyDisplays)
            if (SMPRPG.getService(EntityService.class).getEntityInstance(display) instanceof EntitySpawner spawner)
                nearbySpawners.add(spawner);

        if (nearbySpawners.isEmpty()) {
            event.getPlayer().sendMessage(ComponentUtils.error("Did not detect any spawners near you! Get closer to one and try again :3"));
            return;
        }

        if (nearbySpawners.size() > 1) {
            event.getPlayer().sendMessage(ComponentUtils.error("Detected too many spawners near you! Try to limit the spawners you are close to!"));
            return;
        }

        EntitySpawner spawner = nearbySpawners.getFirst();

        new InterfaceSpawnerMainMenu(event.getPlayer(), spawner).openMenu();
        event.getPlayer().sendMessage(ComponentUtils.success("Now editing the spawner you were looking at!"));
    }
}
