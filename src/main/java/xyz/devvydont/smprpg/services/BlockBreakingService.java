package xyz.devvydont.smprpg.services;

import xyz.devvydont.smprpg.blockbreaking.PacketManager;
import xyz.devvydont.smprpg.blockbreaking.SpeedConfigFileHandler;

public class BlockBreakingService implements IService {

    public SpeedConfigFileHandler filehandler;

    @Override
    public void setup() throws RuntimeException {

        filehandler = new SpeedConfigFileHandler();
        new PacketManager();
        System.out.println("we gaming");

    }

    @Override
    public void cleanup() {
    }
}
