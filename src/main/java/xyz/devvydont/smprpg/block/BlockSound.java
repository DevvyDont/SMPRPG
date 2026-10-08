package xyz.devvydont.smprpg.block;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import xyz.devvydont.smprpg.util.BlockDataBuilders;

import javax.annotation.Nullable;

public enum BlockSound {

    STONE_GENERIC("minecraft:block.stone.break", 0.8f, 1.0f,
                  "minecraft:block.stone.place", 0.8f, 1.0f,
                  "minecraft:block.stone.hit", 0.5f, 0.25f,
                  "minecraft:block.stone.fall", 0.75f, 0.5f,
                  "minecraft:block.stone.step", 1.0f, 0.15f),

    DEEPSLATE_GENERIC("minecraft:block.deepslate.break", 0.8f, 1.0f,
            "minecraft:block.deepslate.place", 0.8f, 1.0f,
            "minecraft:block.deepslate.hit", 0.5f, 0.25f,
            "minecraft:block.deepslate.fall", 0.75f, 0.5f,
            "minecraft:block.deepslate.step", 1.0f, 0.15f),

    METAL_GENERIC("minecraft:block.metal.break", 1.2f, 1.0f,
            "minecraft:block.metal.place", 1.2f, 1.0f,
            "minecraft:block.metal.hit", 0.75f, 0.25f,
            "minecraft:block.metal.fall", 1.25f, 0.5f,
            "minecraft:block.metal.step", 1.5f, 0.15f),

    IRON( "minecraft:block.iron.break", 0.8f, 1.0f,
          "minecraft:block.iron.place", 0.8f, 1.0f,
          "minecraft:block.iron.hit", 0.5f, 0.25f,
          "minecraft:block.iron.fall", 0.75f, 0.5f,
          "minecraft:block.iron.step", 1.0f, 0.15f),

    COPPER( "minecraft:block.copper.break", 0.8f, 1.0f,
            "minecraft:block.copper.place", 0.8f, 1.0f,
            "minecraft:block.copper.hit", 0.5f, 0.25f,
            "minecraft:block.copper.fall", 0.75f, 0.5f,
            "minecraft:block.copper.step", 1.0f, 0.15f),

    MITHRIL_ORE( "minecraft:block.gilded_blackstone.break", 1.2f, 1.0f,
            "minecraft:block.gilded_blackstone.place", 1.2f, 1.0f,
            "minecraft:block.gilded_blackstone.hit", 0.75f, 0.25f,
            "minecraft:block.gilded_blackstone.fall", 0.1875f, 0.5f,
            "minecraft:block.gilded_blackstone.step", 0.25f, 0.15f),

    TITANIUM_METAL( "minecraft:block.iron.break", 0.8f, 1.0f,
            "minecraft:block.iron.place", 0.8f, 1.0f,
            "minecraft:block.iron.hit", 0.5f, 0.25f,
            "minecraft:block.iron.fall", 1.25f, 0.5f,
            "minecraft:block.iron.step", 1.5f, 0.15f),

    ADAMANTIUM_ORE( "minecraft:block.chain.break", 0.25f, 1.0f,
            "minecraft:block.stone.place", 0.8f, 1.0f,
            "minecraft:block.chain.place", 1.25f, 0.25f,
            "minecraft:block.chain.fall", 1.25f, 0.5f,
            "minecraft:block.chain.step", 1.5f, 0.15f),

    WOOD_GENERIC( "audio:block.wood_custom.break", 0.8f, 1.0f,
            "audio:block.wood_custom.place", 0.8f, 1.0f,
            "audio:block.wood_custom.hit", 0.5f, 0.25f,
            "minecraft:block.wood.fall", 0.75f, 0.5f,
            "minecraft:block.wood.step", 1.0f, 0.15f),

    BLACKSTONE_GENERIC("minecraft:block.gilded_blackstone.break", 0.8f, 1.0f,
                              "minecraft:block.gilded_blackstone.place", 0.8f, 1.0f,
                              "minecraft:block.gilded_blackstone.hit", 0.5f, 0.25f,
                              "minecraft:block.gilded_blackstone.fall", 0.75f, 0.5f,
                              "minecraft:block.gilded_blackstone.step", 1.0f, 0.15f),

    RUNE("audio:block.rune.break", 0.8f, 1.0f,
                               "audio:block.rune.place", 0.8f, 1.0f,
                               "audio:block.rune.hit", 0.5f, 0.25f,
                               "audio:block.rune.fall", 0.75f, 0.5f,
                               "audio:block.rune.step", 1.0f, 0.15f);

    public final String BreakSound;
    public final String PlaceSound;
    public final String HitSound;
    public final String FallDmgSound;
    public final String FootstepSound;

    public final float BreakPitch;
    public final float PlacePitch;
    public final float HitPitch;
    public final float FallDmgPitch;
    public final float FootstepPitch;

    public final float BreakVolume;
    public final float PlaceVolume;
    public final float HitVolume;
    public final float FallDmgVolume;
    public final float FootstepVolume;

    BlockSound(String breakSound, float breakPitch, float breakVolume,
               String placeSound, float placePitch, float placeVolume,
               String hitSound, float hitPitch, float hitVolume,
               String fallDmgSound, float fallDmgPitch, float fallDmgVolume,
               String footstepSound, float footstepPitch, float footstepVolume) {
        BreakSound = breakSound;
        PlaceSound = placeSound;
        HitSound = hitSound;
        FallDmgSound = fallDmgSound;
        FootstepSound = footstepSound;

        BreakPitch = breakPitch;
        PlacePitch = placePitch;
        HitPitch = hitPitch;
        FallDmgPitch = fallDmgPitch;
        FootstepPitch = footstepPitch;

        BreakVolume = breakVolume;
        PlaceVolume = placeVolume;
        HitVolume = hitVolume;
        FallDmgVolume = fallDmgVolume;
        FootstepVolume = footstepVolume;
    }
}
