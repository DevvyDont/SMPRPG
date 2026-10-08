package xyz.devvydont.smprpg.listeners.block

import net.momirealms.craftengine.bukkit.api.BukkitAdaptor
import net.momirealms.craftengine.core.util.Key
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.block.NotePlayEvent
import xyz.devvydont.smprpg.block.CraftEngineBlockEnums
import xyz.devvydont.smprpg.util.listeners.ToggleableListener
import xyz.devvydont.smprpg.util.persistence.KeyStore
import kotlin.math.pow

class CustomNoteblockSoundListener : ToggleableListener() {

    @EventHandler
    private fun onCustomNoteblockPlay(event: NotePlayEvent) {
        val block = event.block.getRelative(BlockFace.DOWN)
        val ceBlock = BukkitAdaptor.adapt(block)
        if (ceBlock.blockState().hasTag(Key.of("smprpg:custom_instrument"))) {
            val note = event.note.id.toInt()
            val pitch = 2.0.pow((note - 12.0) / 12.0).toFloat()

            block.world.spawnParticle(Particle.NOTE, Location(block.world, block.x + 0.5, block.y + 2.2, block.z + 0.5), 1, 0.0, 0.0, 0.0, note/24.0)

            var sound = ""
            when (ceBlock.id()) {
                CraftEngineBlockEnums.TIN_BLOCK.key, CraftEngineBlockEnums.ENCHANTED_TIN_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_SAWTOOTH.toString()
                CraftEngineBlockEnums.SILVER_BLOCK.key, CraftEngineBlockEnums.ENCHANTED_SILVER_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_SNES_PIANO.toString()
                CraftEngineBlockEnums.STEEL_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_STEEL_DRUM.toString()
                CraftEngineBlockEnums.BRONZE_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_ORCHESTRA_HIT.toString()
                CraftEngineBlockEnums.ORICHALCUM_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_PAH.toString()
                CraftEngineBlockEnums.COBALT_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_DRUM.toString()
                CraftEngineBlockEnums.MITHRIL_BLOCK.key -> sound = KeyStore.AUDIO_NOTE_OCARINA.toString()
                Key.of("minecraft", "red_concrete") -> sound = KeyStore.AUDIO_NOTE_DOO_HIGH.toString()
                Key.of("minecraft", "green_concrete") -> sound = KeyStore.AUDIO_NOTE_DOO_LOW.toString()
                Key.of("minecraft", "black_concrete_powder") -> sound = KeyStore.AUDIO_NOTE_DOO_BASS.toString()
                Key.of("minecraft", "black_concrete_powder") -> sound = KeyStore.AUDIO_NOTE_DOO_BASS.toString()
            }
            block.world.playSound(block.location, sound, 1f, pitch)
            event.isCancelled = true
        }
    }
}
