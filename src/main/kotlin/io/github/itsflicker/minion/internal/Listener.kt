package io.github.itsflicker.minion.internal

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent
import ink.ptms.zaphkiel.api.event.ItemEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionInfo
import io.github.itsflicker.minion.openMinionMenu
import org.bukkit.block.BlockFace
import org.bukkit.event.block.Action
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.reflect.Reflex.Companion.invokeConstructor
import java.util.*

/**
 * @author wlys
 * @since 2022/5/9 15:06
 */
object Listener {

    @SubscribeEvent
    fun e(e: ItemEvent.Interact) {
        val event = e.bukkitEvent
        if (event.hand == EquipmentSlot.HAND
            && event.hasBlock()
            && event.action == Action.RIGHT_CLICK_BLOCK
            && event.blockFace == BlockFace.UP
            && e.itemStream.getZaphkielItem().group?.name.equals("minion", true))
        {
            val minion = MinionAPI.registeredMinions.entries.firstOrNull { e.itemStream.getZaphkielItem().id.startsWith(it.key) }?.value ?: return
            val info = e.itemStream.getZaphkielData().run {
                val uuid = get("uuid")?.asString()?.let { UUID.fromString(it) } ?: UUID.randomUUID()
                val level = get("level")?.asInt() ?: 1
                val amount = get("amount")?.asInt() ?: 0
                val totalGenerated = get("totalGenerated")?.asInt() ?: 0
                val maxStorage = get("maxStorage")?.asInt() ?: 3
                val delay = get("delay")?.asLong() ?: 30L
                MinionInfo(uuid, level, amount, totalGenerated, maxStorage, delay)
            }
            (minion.invokeConstructor(
                event.player,
                event.clickedBlock!!.location.clone().add(0.5, 1.0, 0.5),
            ) as BaseMinion).init(info)

            event.isCancelled = true

            val item = event.player.inventory.itemInMainHand
            if (item.amount > 1) {
                item.amount -= 1
                event.player.inventory.setItemInMainHand(item)
            } else {
                event.player.inventory.setItemInMainHand(null)
            }
        }
    }

    @SubscribeEvent
    fun e(e: ItemReleaseEvent.Display) {
        e.addLore("info", listOf(
            "",
            "§7休息时间: §a${e.itemStream.getZaphkielData()["delay"]?.asInt() ?: 48}s",
            "§7最大存储量: §e${e.itemStream.getZaphkielData()["maxStorage"]?.asInt() ?: 3}组",
            "§7生成的资源总数: §3${e.itemStream.getZaphkielData()["totalGenerated"]?.asInt() ?: 0}",
            ""
        ))
    }

    @SubscribeEvent
    fun e(e: AdyeshachEntityInteractEvent) {
        val minion = MinionAPI.runningMinions.firstOrNull { it.entity == e.entity } ?: return
        if (e.player.uniqueId == minion.owner.uniqueId) {
            e.player.openMinionMenu(minion)
        }
    }
}