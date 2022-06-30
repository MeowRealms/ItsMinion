package io.github.itsflicker.minion.common

import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import ink.ptms.zaphkiel.ZaphkielAPI
import ink.ptms.zaphkiel.taboolib.module.nms.ItemTagData
import io.github.itsflicker.minion.Minion
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.internal.PublicHologram
import io.github.itsflicker.minion.util.toRomeNumber
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common5.Baffle
import taboolib.module.configuration.util.setLocation
import taboolib.platform.util.toProxyLocation
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

/**
 * @author wlys
 * @since 2022/5/9 14:32
 */
abstract class BaseMinion(val owner: OfflinePlayer, val location: Location) {

    val originId = if (javaClass.isAnnotationPresent(MinionImpl::class.java)) {
        javaClass.getAnnotation(MinionImpl::class.java).value
    } else {
        javaClass.simpleName.toString()
    }

    val id by lazy { originId + info.level.toRomeNumber() }

    lateinit var info: MinionInfo
        private set

    val item by lazy {
        ZaphkielAPI.getItem(id)!!.rebuildToItemStack()
    }

    abstract val entity: AdyArmorStand

    abstract val color: Color

    abstract val resource: ItemStack

    var markRemoved = false

    val viewings = mutableSetOf<Player>()

    var status by Delegates.observable(Status.NULL) { _, oldValue, newValue ->
        if (newValue != oldValue) {
            updateStatus()
        }
    }

    private var hologram: PublicHologram<*>? = null

    private val baffle by lazy { Baffle.of(info.delay, TimeUnit.SECONDS) }

    val maxStorage by lazy { resource.type.maxStackSize * (info.maxStorage) }

    open fun onPreTick() {
        if (baffle.hasNext() && location.chunk.isLoaded) {
            onTick()
        }
    }

    open fun onTick() {

    }

    open fun init(info: MinionInfo) {
        this.info = info
        id
        entity.id = id + info.uuid.toString()
        MinionAPI.cachedMinions.add(this)
    }

    open fun destroy(player: Player? = null, save: Boolean = false): ItemStack {
        markRemoved = true
        entity.delete()
        hologram?.delete()

        if (save) {
            save()
        } else {
            Minion.data[info.uuid.toString()] = null
        }
        return ZaphkielAPI.getItem(id, player)!!.also {
            it.getZaphkielData().apply {
                this["owner"] = ItemTagData(owner.uniqueId.toString())
                this["uuid"] = ItemTagData(info.uuid.toString())
                this["level"] = ItemTagData(info.level.toString())
                this["amount"] = ItemTagData(info.amount.toString())
                this["totalGenerated"] = ItemTagData(info.totalGenerated.toString())
                this["maxStorage"] = ItemTagData(info.maxStorage.toString())
                this["delay"] = ItemTagData(info.delay.toString())
            }
        }.rebuildToItemStack(player)
    }

    open fun save() {
        val key = info.uuid.toString()
        Minion.data[key] = mapOf(
            "type" to originId,
            "owner" to owner.uniqueId.toString()
        )
        Minion.data.setLocation("$key.location", location.toProxyLocation())
        Minion.data["$key.info"] = mapOf(
            "uuid" to info.uuid.toString(),
            "level" to info.level,
            "amount" to info.amount,
            "totalGenerated" to info.totalGenerated,
            "maxStorage" to info.maxStorage,
            "delay" to info.delay
        )
    }

    open fun updateStatus() {
        hologram?.delete()
        if (status.text.isNotEmpty()) {
            hologram = PublicHologram.AdyeshachImpl().also { it.create(location.add(0.0, 1.0, 0.0), status.text) }
        }
    }

    fun increaseAmount(amount: Int) {
        info.amount += amount
        if (info.amount > maxStorage) {
            info.amount = maxStorage
        }
//        submit {
//            ArrayList(viewings).forEach {
//                it.closeInventory()
//                it.openMinionMenu(this@BaseMinion)
//            }
//        }
    }

    enum class Status(val text: List<String>) {
        NO_SPACE(listOf("§c/!\\", "§c没有工作空间! :(")),

        FULL(listOf("§c/!\\", "§c我的背包满了! :(")),

        NULL(emptyList())
    }

}