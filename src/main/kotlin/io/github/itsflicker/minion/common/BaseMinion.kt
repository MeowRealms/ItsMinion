package io.github.itsflicker.minion.common

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.taboolib.module.nms.ItemTagData
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.generateInventory
import io.github.itsflicker.minion.resourceSlots
import io.github.itsflicker.minion.util.createMinionEntity
import kotlinx.coroutines.Job
import net.md_5.bungee.api.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common5.util.parseMillis
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import java.util.*
import java.util.function.Predicate
import kotlin.properties.Delegates

/**
 * @author wlys
 * @since 2022/5/9 14:32
 */
abstract class BaseMinion(
    @Expose val id: UUID,
    @Expose val type: String,
    @Expose val owner: OfflinePlayer,
    @Expose val location: Location,
    @Expose val tier: Int,
    @Expose val totalGenerated: Int,
    @Expose val chestplate: ItemStack?,
    @Expose val leggings: ItemStack?,
    @Expose val boots: ItemStack?
) {

    val info = MinionType.types.first { it.id == type }

    val settings = info.tiers.first { it.tier == tier }

    val name = settings.item

    val color = Color.fromRGB(ChatColor.of(settings.color).color.rgb)

    val item = ZaphkielAPI.getItem(settings.item)!!.rebuildToItemStack()

    val hand = XItemStack.deserialize(settings.hand)

    val hologram = Adyeshach.api().getHologramHandler().createHologram(location.add(0.0, 1.0, 0.0), emptyList())

    val sleep = settings.sleep.parseMillis()

    open val entity = createMinionEntity(location, hand, item) {
        setEquipment(EquipmentSlot.CHEST, chestplate ?: buildItem(XMaterial.LEATHER_CHESTPLATE) { color = this@BaseMinion.color })
        setEquipment(EquipmentSlot.LEGS, leggings ?: buildItem(XMaterial.LEATHER_LEGGINGS) { color = this@BaseMinion.color })
        setEquipment(EquipmentSlot.FEET, boots ?: buildItem(XMaterial.LEATHER_BOOTS) { color = this@BaseMinion.color })
    }

    val viewings = mutableSetOf<Player>()

    val inv = generateInventory(this)

    var job: Job? = null

    var status by Delegates.observable(MinionStatus.NULL) { _, oldValue, newValue ->
        if (newValue != oldValue) {
            hologram.updateSafely(newValue.text)
        }
    }

    fun addItem(vararg item: ItemStack) {
        XItemStack.addItems(inv, true, { it in resourceSlots }, *item)
    }

    fun isFulled(item: ItemStack): Boolean {
        return XItemStack.firstPartialOrEmpty(inv, item, resourceSlots[0]) !in resourceSlots
    }

    open fun start() = Unit

    open fun end() {
        job?.cancel()
    }

    open fun destroy() {
        end()
        MinionAPI.runningMinions.remove(this)
        entity.remove()
        hologram.remove()
    }

    open fun getItem(): ItemStack {
        return ZaphkielAPI.getItem(settings.item)!!.also { stream ->
            stream.getZaphkielData().also {
                it["type"] = ItemTagData(type)
                it["tier"] = ItemTagData(tier)
                it["totalGenerated"] = ItemTagData(totalGenerated.toString())
            }
        }.rebuildToItemStack()
    }
}