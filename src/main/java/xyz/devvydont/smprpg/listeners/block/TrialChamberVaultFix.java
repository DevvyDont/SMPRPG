package xyz.devvydont.smprpg.listeners.block;

import io.papermc.paper.event.block.VaultChangeStateEvent;
import org.bukkit.block.Vault;
import org.bukkit.event.EventHandler;
import xyz.devvydont.smprpg.SMPRPG;
import xyz.devvydont.smprpg.services.ItemService;
import xyz.devvydont.smprpg.util.listeners.ToggleableListener;

public class TrialChamberVaultFix extends ToggleableListener {

    /**
     * Simply just fixes vaults by updating their key requirement.
     * It is important to note that vaults require exact item data matches to
     * consider a key valid, so if you every try to create custom keys for vaults ensure that their
     * item data is consistent.
     */
    @EventHandler
    public void __onVaultInteract(final VaultChangeStateEvent event) {
        // If this is a vault tile state, update the key with an updated version of the key per our plugin's spec.
        if (event.getBlock().getState() instanceof Vault vault) {
            vault.setKeyItem(SMPRPG.getService(ItemService.class).ensureItemStackUpdated(vault.getKeyItem()));
            vault.update();
        }
    }

}
