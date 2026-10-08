package xyz.devvydont.smprpg.services;

import org.bukkit.event.Listener;
import xyz.devvydont.smprpg.blockbreaking.PacketManager;

public class BlockBreakingService implements IService, Listener {


    @Override
    public void setup() throws RuntimeException {
        PacketManager packetManager = new PacketManager();
    }

    @Override
    public void cleanup() {
    }
}
