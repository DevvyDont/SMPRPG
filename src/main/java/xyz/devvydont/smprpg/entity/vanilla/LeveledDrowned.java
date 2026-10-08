package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.Material;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import xyz.devvydont.smprpg.entity.MobType;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;

public class LeveledDrowned extends VanillaEntity<Drowned> {

    public LeveledDrowned(Drowned entity) {
        super(entity);
    }

    @Override
    public void setup() {
        mobTypes.add(MobType.UNDEAD);
        mobTypes.add(MobType.AQUATIC);
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
