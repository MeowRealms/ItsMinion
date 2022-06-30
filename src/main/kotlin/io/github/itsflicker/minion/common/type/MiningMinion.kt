package io.github.itsflicker.minion.common.type

import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import io.github.itsflicker.minion.common.BaseMinion
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.util.EulerAngle
import taboolib.common.util.sync
import java.util.ArrayList

/**
 * @author wlys
 * @since 2022/5/20 23:51
 */
abstract class MiningMinion(owner: OfflinePlayer, location: Location) : BaseMinion(owner, location) {

    abstract val block: Material

    override fun onTick() {
        val area = ArrayList<Block>()
        for (x in -2..2){
            for (z in -2..2){
                if (x != 0 || z != 0) {
                    area.add(entity.getLocation().subtract(x.toDouble(), 1.0, z.toDouble()).block)
                }
            }
        }
        val airs = area.filter { it.type.isAir }
        val blocks = area.filter { it.type == block }
        if (airs.isEmpty() && blocks.isEmpty()){
            status = Status.NO_SPACE
            return
        }
        status = Status.NULL
        if (airs.isNotEmpty()) {
            val target = airs.random()
            entity.controllerLook(target.location.add(0.5, 0.0, 0.5))
            // 抬手
            for (i in 5 downTo -85) {
                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
                entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(i.toDouble(), 0.0, -10.0))
                Thread.sleep(5)
            }
            sync { target.type = block }
            // 归位
            entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(-5.0, 0.0, 10.0))
            entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(-5.0, 0.0, -10.0))
            return
        }
        if (info.amount >= maxStorage) {
            status = Status.FULL
            return
        }
        status = Status.NULL
        val target = blocks.random()
        entity.controllerLook(target.location.add(0.5, 0.0, 0.5))
        // 挖掘
        (1..5).forEach { _ ->
            for (i in -85..-5) {
                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
                // TODO: 方块破坏动画
                Thread.sleep(4)
            }
        }
        sync { target.type = Material.AIR }
        increaseAmount(1)
    }
}