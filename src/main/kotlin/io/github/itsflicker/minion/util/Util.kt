package io.github.itsflicker.minion.util

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.Controllable
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyArmorStand
import org.bukkit.Location
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @author wlys
 * @since 2022/5/20 23:42
 */
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

fun Location.center() = add(0.5, 0.5, 0.5)

fun createMinionEntity(location: Location, hand: ItemStack, head: ItemStack, callback: AdyArmorStand.() -> Unit): AdyArmorStand {
    return Adyeshach.api().getPublicEntityManager().create(EntityTypes.ARMOR_STAND, location) {
        it.defaultStyle(hand, head)
        callback(it as AdyArmorStand)
    } as AdyArmorStand
}

fun EntityInstance.defaultStyle(hand: ItemStack, head: ItemStack) {
    this as AdyArmorStand
    setSmall(true)
    setBasePlate(false)
    setArms(true)
    setEquipment(EquipmentSlot.HAND, hand)
    setEquipment(EquipmentSlot.HEAD, head)
//    setEquipment(EquipmentSlot.CHEST, buildItem(XMaterial.LEATHER_CHESTPLATE) { this.color = color })
//    setEquipment(EquipmentSlot.LEGS, buildItem(XMaterial.LEATHER_LEGGINGS) { this.color = color })
//    setEquipment(EquipmentSlot.FEET, buildItem(XMaterial.LEATHER_BOOTS) { this.color = color })
}

fun Controllable.controllerLookAt(loc: Location) = controllerLookAt(loc.x, loc.y, loc.z)