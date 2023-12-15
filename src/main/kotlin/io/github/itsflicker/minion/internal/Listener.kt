package io.github.itsflicker.minion.internal

import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.zaphkiel.api.event.ItemEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.common.BaseMinion
import org.bukkit.block.BlockFace
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.util.parseUUID
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.xseries.XItemStack
import java.util.*

/**
 * @author wlys
 * @since 2022/5/9 15:06
 */
object Listener {

    @SubscribeEvent
    fun onPlace(e: ItemEvent.Interact) {
        if (e.isMainhand()
            && e.hasBlock()
            && e.isRightClickBlock()
            && e.blockFace == BlockFace.UP)
        {
            val data = e.itemStream.getZaphkielData()
            val type = data["type"]?.asString() ?: return
            val minion = MinionAPI.registeredMinions[type] ?: return

            val id = data["uuid"]?.asString()?.parseUUID() ?: UUID.randomUUID()
            val tier = data["tier"]?.asInt() ?: 1
            val totalGenerated = data["totalGenerated"]?.asInt() ?: 0

            (minion.invokeConstructor(id, type, e.player,
                e.clickedBlock!!.location.clone().add(0.5, 1.0, 0.5),
                tier, totalGenerated, null, null, null
            ) as BaseMinion).start()

            e.isCancelled = true

            val item = e.player.inventory.itemInMainHand
            if (item.amount > 1) {
                item.amount -= 1
                e.player.inventory.setItemInMainHand(item)
            } else {
                e.player.inventory.setItemInMainHand(null)
            }
        }
    }

    @SubscribeEvent
    fun onDisplay(e: ItemReleaseEvent.Display) {
        val data = e.itemStream.getZaphkielData()
        e.addLore("info", listOf(
            "",
            "§7等级: §a${data["tier"]?.asInt() ?: 1}阶",
            "§7已生产: §3${data["totalGenerated"]?.asInt() ?: 0}",
            ""
        ))
    }

    @SubscribeEvent
    fun onClick(e: AdyeshachEntityInteractEvent) {
        if (!e.isMainHand) return
        val minion = MinionAPI.runningMinions.firstOrNull { it.entity == e.entity } ?: return
        if (e.player.uniqueId == minion.owner.uniqueId) {
            e.player.openInventory(minion.inv)
        }
    }

    @SubscribeEvent
    fun onDamage(e: AdyeshachEntityDamageEvent) {
        val minion = MinionAPI.runningMinions.firstOrNull { it.entity == e.entity } ?: return
        if (minion.owner.uniqueId == e.player.uniqueId) {
            minion.destroy()
            XItemStack.giveOrDrop(e.player, minion.getItem())
        }
    }
}