package xyz.devvydont.smprpg.util;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.NoteBlock;

public class BlockDataBuilders {

    public static BlockData newNoteBlockData(Instrument instrument, Note note, boolean isPowered) {

        var noteBlockData = (NoteBlock) Material.NOTE_BLOCK.createBlockData();
        noteBlockData.setInstrument(instrument);
        noteBlockData.setNote(note);
        noteBlockData.setPowered(isPowered);
        return noteBlockData;
    }

    public static BlockData newLeavesData(Material leafType, Boolean persistent, int distance) {
        var leavesData = (Leaves) leafType.createBlockData();
        leavesData.setDistance(distance);
        leavesData.setPersistent(persistent);
        return leavesData;
    }
}
