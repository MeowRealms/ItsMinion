package io.github.itsflicker.minion.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.google.gson.reflect.TypeToken
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.expansion.ioc.serialization.SerializationManager
import taboolib.expansion.ioc.serialization.impl.SerializationFunctionGson
import java.util.*

val gson: Gson = GsonBuilder().apply {
    excludeFieldsWithoutExposeAnnotation()
    registerTypeAdapter(
        ItemStack::class.java,
        JsonSerializer<ItemStack> { src, typeOfSrc, context ->
            Gson().toJsonTree(src.serialize())
        }
    )
    registerTypeAdapter(
        ItemStack::class.java,
        JsonDeserializer { json, typeOfT, context ->
            ItemStack.deserialize(
                Gson().fromJson(
                    json,
                    object : TypeToken<MutableMap<String, Any>>() {}.type
                )
            )
        }
    )
    registerTypeAdapter(
        Location::class.java,
        JsonSerializer<Location> { a, _, _ ->
            JsonPrimitive(fromLocation(a))
        }
    )
    registerTypeAdapter(
        Location::class.java,
        JsonDeserializer { a, _, _ ->
            toLocation(a.asString)
        }
    )
    registerTypeAdapter(
        OfflinePlayer::class.java,
        JsonSerializer<OfflinePlayer> { a, _, _ ->
            JsonPrimitive(a.uniqueId.toString())
        }
    )
    registerTypeAdapter(
        OfflinePlayer::class.java,
        JsonDeserializer { a, _, _ ->
            Bukkit.getOfflinePlayer(UUID.fromString(a.asString))
        }
    )
}.create()

private fun toLocation(source: String): Location {
    return source.replace("__", ".").split(",").run {
        Location(
            Bukkit.getWorld(get(0)),
            getOrElse(1) { "0" }.toDouble(),
            getOrElse(2) { "0" }.toDouble(),
            getOrElse(3) { "0" }.toDouble()
        )
    }
}

private fun fromLocation(location: Location): String {
    return "${location.world?.name},${location.x},${location.y},${location.z}".replace(".", "__")
}

@Awake(LifeCycle.INIT)
fun init() {
    (SerializationManager.function["Gson"] as SerializationFunctionGson).gson = gson
}