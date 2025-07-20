package xyz.devvydont.smprpg.entity.vanilla;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Stray;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.attribute.AttributeWrapper;
import xyz.devvydont.smprpg.entity.base.VanillaEntity;
import xyz.devvydont.smprpg.services.AttributeService;
import xyz.devvydont.smprpg.services.ItemService;

public class LeveledStray extends VanillaEntity<Stray> {

    public LeveledStray(Stray entity) {
        super(entity);
    }

    @Override
    public void setup() {
        super.setup();
        var attr = SMPRPG.getService(AttributeService.class).getAttribute(_entity, AttributeWrapper.STRENGTH);

        // Nerf the bow damage a bit. This is because base damage stacks on top of the damage from the bow.
        if (attr != null)
            attr.addModifier(new AttributeModifier(new NamespacedKey("smprpg", "stray_nerf"), -.3, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        _entity.getEquipment().setItemInMainHand(ItemService.generate(Material.BOW));
    }

}
