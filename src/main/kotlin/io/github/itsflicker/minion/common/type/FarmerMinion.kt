package io.github.itsflicker.minion.common.type

import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionImpl
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import taboolib.expansion.ioc.annotation.Component
import java.util.*

/**
 * @author wlys
 * @since 2022/6/24 22:28
 */
//@MinionImpl("farmer")
@Component(index = "id")
class FarmerMinion(
    id: UUID,
    type: String,
    owner: OfflinePlayer,
    location: Location,
    tier: Int,
    totalGenerated: Int,
    chestplate: ItemStack?,
    leggings: ItemStack?,
    boots: ItemStack?
) : BaseMinion(id, type, owner, location, tier, totalGenerated, chestplate, leggings, boots) {

    override fun start() {
//        val dirtArea = ArrayList<Block>()
//        for (x in -2..2){
//            for (z in -2..2){
//                if (x != 0 || z != 0) {
//                    dirtArea.add(entity.getLocation().subtract(x.toDouble(), 1.0, z.toDouble()).block)
//                }
//            }
//        }
//        val cropArea = ArrayList<Block>()
//        for (x in -2..2){
//            for (z in -2..2){
//                if (x != 0 || z != 0) {
//                    cropArea.add(entity.getLocation().subtract(x.toDouble(), 0.0, z.toDouble()).block)
//                }
//            }
//        }
//        val dirt = dirtArea.filter { it.type == Material.DIRT }
//        val farmlands = dirtArea.filter { it.type == Material.FARMLAND }
//        val airs = cropArea.filter { it.type.isAir && it.location.subtract(0.0, 1.0, 0.0).block.run { type == Material.DIRT || type == Material.FARMLAND } }
//        val crops = cropArea.filter { it.type == seed }
//        if ((dirt.isEmpty() && farmlands.isEmpty()) || (airs.isEmpty() && crops.isEmpty())) {
//            status = Status.NO_SPACE
//            return
//        }
//        status = Status.NULL
//        farmlands.forEach { (it.blockData as Farmland).run { moisture = maximumMoisture } }
//        if (dirt.isNotEmpty()) {
//            val target = dirt.random()
//            val loc = target.location.center()
//            entity.controllerLookAt(loc.x, loc.y, loc.z)
//            for (i in 5 downTo -85) {
//                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
//                entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(i.toDouble(), 0.0, -10.0))
//                Thread.sleep(5)
//            }
//            sync { target.type = Material.FARMLAND }
//            entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(-5.0, 0.0, 10.0))
//            entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(-5.0, 0.0, -10.0))
//            return
//        }
//        if (airs.isNotEmpty()) {
//            val target = airs.random()
//            val loc = target.location.center()
//            entity.controllerLookAt(loc.x, loc.y, loc.z)
//            for (i in 5 downTo -85) {
//                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
//                entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(i.toDouble(), 0.0, -10.0))
//                Thread.sleep(5)
//            }
//            sync { target.type = seed }
//            entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(-5.0, 0.0, 10.0))
//            entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(-5.0, 0.0, -10.0))
//            return
//        }
//        crops.forEach {
//            val data = it.blockData as Ageable
//            if (data.age < data.maximumAge) {
//                data.age += 1
//            }
//        }
//        if (info.amount >= maxStorage) {
//            status = Status.FULL
//            return
//        }
//        status = Status.NULL
//        val target = crops.random()
//        entity.controllerLook(target.location.add(0.5, 0.0, 0.5))
//        (1..5).forEach { _ ->
//            for (i in -85..-5) {
//                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
//                // TODO: 方块破坏动画
//                Thread.sleep(4)
//            }
//        }
//        sync { target.type = Material.AIR }
//        increaseAmount(1)
    }
}