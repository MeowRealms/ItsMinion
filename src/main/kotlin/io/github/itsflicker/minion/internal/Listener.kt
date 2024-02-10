package io.github.itsflicker.minion.internal

import ink.ptms.adyeshach.core.event.AdyeshachEntityDamageEvent
import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import ink.ptms.zaphkiel.api.event.ItemEvent
import ink.ptms.zaphkiel.api.event.ItemReleaseEvent
import io.github.itsflicker.minion.MinionAPI
import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionType
import io.github.itsflicker.minion.openMinionMenu
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
        val data = e.itemStream.getZaphkielData()
        val type = data["minion"]?.asString() ?: return
        e.bukkitEvent.isCancelled = true
        if (!e.isRightClickBlock() || e.blockFace != BlockFace.UP) return

        val impl = MinionType.types.firstOrNull { it.name == type }?.impl ?: return
        val minion = MinionAPI.registeredMinions[impl] ?: return

        val uuid = data["uuid"]?.asString()?.parseUUID() ?: UUID.randomUUID()
        val tier = data["tier"]?.asInt() ?: 1
        val totalGenerated = data["totalGenerated"]?.asInt() ?: 0

        val item = e.player.inventory.itemInMainHand
        if (item.amount > 1) {
            item.amount -= 1
            e.player.inventory.setItemInMainHand(item)
        } else {
            e.player.inventory.setItemInMainHand(null)
        }

        val instance = minion.invokeConstructor(uuid, type, e.player,
            e.clickedBlock!!.location.clone().add(0.5, 1.0, 0.5),
            tier, totalGenerated, null, null, null
        ) as BaseMinion
        MinionAPI.runningMinions.add(instance)
        instance.startJob()
    }

    @SubscribeEvent
    fun onDisplay(e: ItemReleaseEvent.Display) {
        val data = e.itemStream.getZaphkielData()
        if (!data.containsKey("minion")) return
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
        val iterator = MinionAPI.runningMinions.iteratorIOC()
        var minion: BaseMinion? = null
        while (iterator.hasNext()) {
            val cur = (iterator.next() as BaseMinion)
            if (cur.entity == e.entity) {
                minion = cur
                break
            }
        }
        if (minion == null) return
        if (e.player.uniqueId == minion.owner.uniqueId) {
            e.player.openMinionMenu(minion)
        }
    }

    @SubscribeEvent
    fun onDamage(e: AdyeshachEntityDamageEvent) {
        val iterator = MinionAPI.runningMinions.iteratorIOC()
        var minion: BaseMinion? = null
        while (iterator.hasNext()) {
            val cur = (iterator.next() as BaseMinion)
            if (cur.entity == e.entity) {
                minion = cur
                break
            }
        }
        if (minion == null) return
        if (minion.owner.uniqueId == e.player.uniqueId) {
            minion.destroy()
            XItemStack.giveOrDrop(e.player, minion.buildMinionItem())
        }
    }
}