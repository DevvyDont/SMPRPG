package xyz.devvydont.smprpg.market.storage

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import org.bukkit.inventory.ItemStack
import java.util.Base64

/**
 * Gson TypeAdapter for serializing/deserializing ItemStack to/from Base64 strings.
 * Uses Paper's native serializeAsBytes/deserializeBytes for full item data preservation,
 * including PDC, enchantments, custom model data, reforges, etc.
 */
class ItemStackSerializer : TypeAdapter<ItemStack>() {

    override fun write(out: JsonWriter, value: ItemStack?) {
        if (value == null) {
            out.nullValue()
            return
        }
        val bytes = value.serializeAsBytes()
        out.value(Base64.getEncoder().encodeToString(bytes))
    }

    override fun read(reader: JsonReader): ItemStack? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val base64 = reader.nextString()
        val bytes = Base64.getDecoder().decode(base64)
        return ItemStack.deserializeBytes(bytes)
    }
}
