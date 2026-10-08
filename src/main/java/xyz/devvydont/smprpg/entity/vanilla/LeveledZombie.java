package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.items.blueprints.resources.scrolls.DynamicEnchantingScroll;
import xyz.devvydont.smprpg.services.EnchantmentService;
import xyz.devvydont.smprpg.util.items.ChancedItemDrop;
import xyz.devvydont.smprpg.util.items.LootDrop;

import java.util.Collection;
import java.util.List;

public class LeveledZombie extends VanillaEntity<Zombie> {

    public LeveledZombie(Zombie entity) {
        super(entity);
    }

    @Override
    public @Nullable Collection<LootDrop> getItemDrops() {
        var smiteScroll = DynamicEnchantingScroll.getScrollWithEnchantment(EnchantmentService.SMITE);
        return List.of(
                new ChancedItemDrop(smiteScroll, 500, this)
        );
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.HUMANOID);

        super.setup();

        // Remove enchantments from zombie's items
        _entity.getEquipment();
        if (!_entity.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) {
            ItemStack item = _entity.getEquipment().getItemInMainHand();
            item.removeEnchantments();
            _entity.getEquipment().setItemInMainHand(item);
        }
    }

}
