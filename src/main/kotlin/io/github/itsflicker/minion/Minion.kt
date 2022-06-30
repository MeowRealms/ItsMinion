package io.github.itsflicker.minion

import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionInfo
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.createLocal
import taboolib.module.configuration.util.getLocation
import taboolib.platform.util.toBukkitLocation
import java.util.*

object Minion : Plugin() {

//    @Config
    lateinit var conf: Configuration
        private set

    val data by lazy { createLocal("data.yml") }

    override fun onEnable() {
        data.getKeys(false).forEach { key ->
            data.getConfigurationSection(key)!!.apply {
                val type = MinionAPI.registeredMinions.entries.firstOrNull { it.key == getString("type") }?.value ?: return@apply
                val owner = Bukkit.getOfflinePlayer(UUID.fromString(getString("owner")))
                val location = getLocation("location")!!.toBukkitLocation()
                val info = getConfigurationSection("info")!!.run {
                    val uuid = getString("uuid")?.let { UUID.fromString(it) } ?: UUID.randomUUID()
                    val level = getInt("level", 1)
                    val amount = getInt("amount", 0)
                    val totalGenerated = getInt("totalGenerated", 0)
                    val maxStorage = getInt("maxStorage", 3)
                    val delay = getLong("delay", 30L)
                    MinionInfo(uuid, level, amount, totalGenerated, maxStorage, delay)
                }
                (type.invokeConstructor(owner, location) as BaseMinion).init(info)
            }
        }
    }

    override fun onDisable() {
        ArrayList(MinionAPI.runningMinions).forEach {
            it.viewings.forEach { player ->
                player.closeInventory()
            }
            it.destroy(save = true)
        }
        data.saveToFile()
    }
}