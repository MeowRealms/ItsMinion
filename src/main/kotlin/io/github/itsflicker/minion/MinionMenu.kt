package io.github.itsflicker.minion

import io.github.itsflicker.minion.common.BaseMinion
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Stored
import taboolib.platform.util.buildItem
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir

/**
 * @author wlys
 * @since 2022/5/9 14:30
 */
val resourceSlots = arrayOf(
    12, 13, 14, 15, 16,
    21, 22, 23, 24, 25,
    30, 31, 32, 33, 34
)

val resourceSlotRanges = listOf(
    12..16,
    21..25,
    30..34
)

fun generateInventory(minion: BaseMinion): Inventory = buildMenu<Stored>(minion.name) {
    rows(6)
    map(
        "xaxxxMxxx",
        "xhxIIIIIx",
        "xcxIIIIIx",
        "xlxIIIIIx",
        "xbxxxCxxx",
        "xxxfffffx",
    )
    rule {
        checkSlot(0 until 54) { _, _ -> false }
        checkSlot(18) { _, item -> item.type.equipmentSlot == EquipmentSlot.CHEST }
        checkSlot(27) { _, item -> item.type.equipmentSlot == EquipmentSlot.LEGS }
        checkSlot(36) { _, item -> item.type.equipmentSlot == EquipmentSlot.FEET }
        resourceSlots.forEach {
            checkSlot(it) { inv, item -> inv.getItem(it)?.type != Material.WHITE_STAINED_GLASS_PANE && item.isAir }
        }
        firstSlot { _, item ->
            when (item.type.equipmentSlot) {
                EquipmentSlot.CHEST -> 18
                EquipmentSlot.LEGS -> 27
                EquipmentSlot.FEET -> 36
                else -> -1
            }
        }
        writeItem { inv, item, slot ->

        }
        readItem { inv, slot ->
            if (slot !in 0 until inv.size) null
            else {
                when (slot) {
                    18 -> minion.chestplate
                    27 -> minion.leggings
                    36 -> minion.boots
                    else -> inv.getItem(slot)
                }
            }
        }
    }
    set('x', XMaterial.BLACK_STAINED_GLASS_PANE) { name = "§r" }
    set('a', XMaterial.ARMOR_STAND) {  }
    set('f', XMaterial.ORANGE_STAINED_GLASS_PANE) { name = "§r" }
    set('M', minion.item)
    set('C', buildItem(XMaterial.CHEST) { name = "&6全部收回" }) {
        val resources = resourceSlots.mapNotNull { minion.inv.getItem(it).takeUnless { item -> item?.type == Material.WHITE_STAINED_GLASS_PANE } }
        clicker.giveItem(resources)

    }
}