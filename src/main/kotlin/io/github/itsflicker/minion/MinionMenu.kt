package io.github.itsflicker.minion

import io.github.itsflicker.minion.common.BaseMinion
import org.bukkit.entity.Player
import taboolib.library.xseries.XItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir

/**
 * @author wlys
 * @since 2022/5/9 14:30
 */
val resourceSlots = arrayOf(
    21, 22, 23, 24, 25,
    30, 31, 32, 33, 34,
    39, 40, 41, 42, 43
)

fun Player.openMinionMenu(minion: BaseMinion) {
    minion.viewings.add(this)
    openMenu<Basic>(minion.id) {
        rows(6)
        handLocked(false)
        map(
            "xxxLMxxxx",
            "xxxxxxxxx",
            "xxxIIIIIx",
            "xxxIIIIIx",
            "xxxIIIIIx",
            "xxxCxQxxD",
        )
        set('x', PLACEHOLDER)
        set('L', XMaterial.REDSTONE_TORCH) {
            name = "§c工作环境"
        }
        set('M', minion.item)
        set('C', XMaterial.CHEST) {
            name = "§6全部收回"
        }
        set('Q', XMaterial.DIAMOND) {
            name = "§2快速升级"
        }
        set('D', XMaterial.BEDROCK) {
            name = "§c摧毁"
        }
        var amount = minion.info.amount
        resourceSlots.forEach { slot ->
            if (amount - minion.resource.type.maxStackSize > 0) {
                set(slot, minion.resource.clone().also { r -> r.amount = 64 })
                amount -= minion.resource.type.maxStackSize
            } else if (amount > 0) {
                set(slot, minion.resource.clone().also { r -> r.amount = amount })
                amount = 0
            }
        }
        onClick(lock = true) {
            when (it.slot) {
                'I' -> {
                    if (it.currentItem.isNotAir()) {
                        XItemStack.giveOrDrop(it.clicker, it.currentItem)
                        minion.info.amount -= it.currentItem!!.amount
                        it.currentItem = null
                    }
                }
                'C' -> {
                    XItemStack.giveOrDrop(it.clicker, true, minion.resource.clone().also { r -> r.amount = minion.info.amount })
                    minion.info.amount = 0
                    it.clicker.openMinionMenu(minion)
                }
                'D' -> {
                    XItemStack.giveOrDrop(it.clicker, minion.destroy())
                    it.clicker.closeInventory()
                }
            }
        }
        onClose {
            minion.viewings.remove(it.player)
        }
    }
}

val PLACEHOLDER = buildItem(XMaterial.BLACK_STAINED_GLASS_PANE) {
    name = "§r"
}