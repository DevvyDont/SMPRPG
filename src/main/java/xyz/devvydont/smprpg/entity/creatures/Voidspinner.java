package xyz.devvydont.smprpg.entity.creatures;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Spider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.CustomEntityType;
import xyz.devvydont.smprpg.entity.base.CustomEntityInstance;
import xyz.devvydont.smprpg.items.CustomItemType;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;
import xyz.devvydont.smprpg.util.items.QuantityLootDrop;

import java.util.Collection;
import java.util.List;

public class Voidspinner extends CustomEntityInstance<Spider> {

    public Voidspinner(Entity entity, CustomEntityType entityType) {
        super(entity, entityType);
    }

    @Override
    public void updateAttributes() {
        super.updateAttributes();
        this.updateBaseAttribute(AttributeWrapper.SCALE, .75);
        this.updateBaseAttribute(AttributeWrapper.MOVEMENT_SPEED, .5);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        return List.of(
                new ChancedItemDrop(ItemService.generate(CustomItemType.COTTON_CANDY), 4, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ARAXYS_HELMET), 450, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ARAXYS_CHESTPLATE), 500, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ARAXYS_LEGGINGS), 400, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ARAXYS_BOOTS), 450, this),
                new ChancedItemDrop(ItemService.generate(CustomItemType.ARAXYS_CLAW), 500, this),
                new QuantityLootDrop(ItemService.generate(Material.STRING), 1, 2, this)
        );
    }

}
