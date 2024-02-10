package io.github.itsflicker.minion.common.type

import ink.ptms.adyeshach.core.bukkit.BukkitRotation
import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionImpl
import io.github.itsflicker.minion.common.MinionStatus
import io.github.itsflicker.minion.util.center
import io.github.itsflicker.minion.util.controllerLookAt
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import taboolib.common5.RandomList
import taboolib.common5.cint
import taboolib.expansion.Chain
import taboolib.expansion.ioc.annotation.Component
import taboolib.library.xseries.XItemStack
import java.util.*

/**
 * @author wlys
 * @since 2022/5/20 23:51
 */
@MinionImpl("mining")
@Component(index = "uuid")
class MiningMinion(
    uuid: UUID,
    type: String,
    owner: OfflinePlayer,
    location: Location,
    tier: Int,
    totalGenerated: Int,
    chestplate: ItemStack?,
    leggings: ItemStack?,
    boots: ItemStack?
) : BaseMinion(uuid, type, owner, location, tier, totalGenerated, chestplate, leggings, boots) {

    val baseBlock = Material.valueOf(info.conf.getString("base")!!.uppercase())
    val randomList = RandomList<Resource>()
    val resources = info.conf.getMapList("resources").map { map ->
        val weight = map["weight"].cint
        val block = Material.valueOf(map["block"]!!.toString().uppercase())
        val item = XItemStack.deserialize(map["item"] as Map<String, *>)
        Resource(weight, block, item).also { randomList.add(it, weight) }
    }
    val targetBlock = resources.map { it.block }

    override val workChain = Chain {
        val area = ArrayList<Block>()
        for (x in -2..2) {
            for (z in -2..2) {
                if (x != 0 || z != 0) {
                    area.add(entity.getLocation().subtract(x.toDouble(), 1.0, z.toDouble()).block)
                }
            }
        }
        val bases = area.filter { it.type == baseBlock }
        val blocks = area.filter { it.type in targetBlock }
        if (bases.isEmpty() && blocks.isEmpty()) {
            status = MinionStatus.NO_SPACE
            wait(100)
            return@Chain
        }
        status = MinionStatus.NULL
        if (bases.isNotEmpty()) {
            val target = bases.random()
            entity.controllerLookAt(target.location.center())
            // 抬手
            for (i in 5 downTo -85 step 10) {
                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
                entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(i.toDouble(), 0.0, -10.0))
                wait(1)
            }
            target.type = randomList.random()!!.block
            // 归位
            entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(-5.0, 0.0, 10.0))
            entity.setRotation(BukkitRotation.LEFT_ARM, EulerAngle(-5.0, 0.0, -10.0))
            return@Chain
        }
        if (resources.any { isFulled(it.item) }) {
            status = MinionStatus.FULL
            wait(200)
            return@Chain
        }
        status = MinionStatus.NULL
        if (blocks.isEmpty()) {
            return@Chain
        }
        val target = blocks.random()
        entity.controllerLookAt(target.location.center())
        // 挖掘
        (1..5).forEach { _ ->
            for (i in -85..-5 step 10) {
                entity.setRotation(BukkitRotation.RIGHT_ARM, EulerAngle(i.toDouble(), 0.0, 10.0))
                // TODO: 方块破坏动画
                wait(1)
            }
        }
        val item = resources.firstOrNull { it.block == target.type }?.item ?: return@Chain
        addItems(item)
        target.type = baseBlock
    }

    class Resource(val weight: Int, val block: Material, val item: ItemStack)

}