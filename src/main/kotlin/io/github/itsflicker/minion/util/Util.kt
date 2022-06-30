package io.github.itsflicker.minion.util

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityInstance
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.ai.general.GeneralSmoothLook
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.buildItem

/**
 * @author wlys
 * @since 2022/5/20 23:42
 */
fun createMinionEntity(location: Location, hand: ItemStack, head: ItemStack, color: Color): AdyArmorStand {
    return AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.ARMOR_STAND, location) {
        it.defaultStyle(hand, head, color)
    } as AdyArmorStand
}

fun EntityInstance.defaultStyle(hand: ItemStack, head: ItemStack, color: Color) {
    this as AdyArmorStand
    registerController(GeneralSmoothLook(this))
    setSmall(true)
    setBasePlate(false)
    setArms(true)
    setEquipment(EquipmentSlot.HEAD, head)
    setEquipment(EquipmentSlot.CHEST, buildItem(XMaterial.LEATHER_CHESTPLATE) { this.color = color })
    setEquipment(EquipmentSlot.LEGS, buildItem(XMaterial.LEATHER_LEGGINGS) { this.color = color })
    setEquipment(EquipmentSlot.FEET, buildItem(XMaterial.LEATHER_BOOTS) { this.color = color })
    setEquipment(EquipmentSlot.HAND, hand)
}

val additionMap = mapOf(1000 to "M",900 to "CM",500 to "D",400 to "CD",100 to "C",90 to "XC",50 to "L",40 to "XL",10 to "X",9 to "IX",5 to "V",4 to "IV",1 to "I")

fun Int.toRomeNumber(): String {
    val sb = StringBuilder()
    var temp = this
    while (temp != 0) {
        additionMap.forEach {
            while (temp >= it.key) {
                sb.append(it.value)
                temp -= it.key
            }
        }
    }
    return sb.toString()
}