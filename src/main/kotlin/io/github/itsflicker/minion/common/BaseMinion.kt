package io.github.itsflicker.minion.common

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachHologram
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.taboolib.module.nms.ItemTagData
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.util.createMinionEntity
import kotlinx.coroutines.*
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import taboolib.common5.util.parseMillis
import taboolib.expansion.AsyncDispatcher
import taboolib.expansion.Chain
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem
import taboolib.platform.util.isNotAir
import java.util.*
import kotlin.properties.Delegates

/**
 * @author wlys
 * @since 2022/5/9 14:32
 */
abstract class BaseMinion(
    @Expose val uuid: UUID,
    @Expose val type: String,
    @Expose val owner: OfflinePlayer,
    @Expose val location: Location,
    @Expose val tier: Int,
    @Expose val totalGenerated: Int,
    @Expose val chestplate: ItemStack?,
    @Expose val leggings: ItemStack?,
    @Expose val boots: ItemStack?
) : InventoryHolder {

    val info = MinionType.types.first { it.name == type }

    val settings = info.tiers.first { it.tier == tier }

    val name = settings.item

    val color = ChatColor.of(settings.color).color.let { Color.fromRGB(it.red, it.green, it.blue) }

    val item = ZaphkielAPI.getItem(settings.item)!!.rebuildToItemStack()

    val hand = XItemStack.deserialize(settings.hand)

    val sleepMillis = settings.sleep.parseMillis()

    val availableSlots = 0 until settings.storage

    lateinit var hologram: AdyeshachHologram

    lateinit var entity: AdyArmorStand

    protected lateinit var inv: Inventory

    var status by Delegates.observable(MinionStatus.NULL) { _, oldValue, newValue ->
        if (newValue != oldValue) {
            hologram.updateSafely(newValue.text)
        }
    }

    var job: Job? = null

    abstract val workChain: Chain

    fun addItems(vararg item: ItemStack) {
        XItemStack.addItems(inv, true, { it in availableSlots }, *item)
    }

    fun isFulled(item: ItemStack): Boolean {
        return XItemStack.firstPartialOrEmpty(inv, item, 0) !in availableSlots
    }

    fun getItems(): List<ItemStack> {
        return availableSlots.mapNotNull { slot ->
            inv.getItem(slot).takeIf { it.isNotAir() }
        }
    }

    fun clearItems(slot: Int = -1) {
        if (slot < 0) {
            inv.clear()
        } else {
            inv.clear(slot)
        }
    }

    open fun init() {
        inv = Bukkit.createInventory(this, 54)
        entity = createMinionEntity(location, hand, item) {
            setEquipment(EquipmentSlot.CHEST, chestplate ?: buildItem(XMaterial.LEATHER_CHESTPLATE) { color = this@BaseMinion.color })
            setEquipment(EquipmentSlot.LEGS, leggings ?: buildItem(XMaterial.LEATHER_LEGGINGS) { color = this@BaseMinion.color })
            setEquipment(EquipmentSlot.FEET, boots ?: buildItem(XMaterial.LEATHER_BOOTS) { color = this@BaseMinion.color })
        }
        hologram = Adyeshach.api().getHologramHandler().createHologram(location.add(0.0, 1.0, 0.0), emptyList())
    }

    open fun startJob() {
        if (job != null) return
        job = CoroutineScope(AsyncDispatcher).launch {
            while (isActive) {
                delay(sleepMillis)
                workChain.chain(workChain)
            }
        }
    }

    open fun endJob() {
        job?.cancel()
    }

    open fun destroy() {
        endJob()
        MinionAPI.runningMinions.remove(this)
        entity.remove()
        hologram.remove()
    }

    open fun buildMinionItem(): ItemStack {
        return ZaphkielAPI.getItem(settings.item)!!.also { stream ->
            stream.getZaphkielData().also {
                it["minion"] = ItemTagData(type)
                it["tier"] = ItemTagData(tier)
                it["totalGenerated"] = ItemTagData(totalGenerated.toString())
            }
        }.rebuildToItemStack()
    }

    override fun getInventory(): Inventory {
        return inv
    }
}