package io.github.itsflicker.minion.common.impl

import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import io.github.itsflicker.minion.common.MinionImpl
import io.github.itsflicker.minion.common.type.FarmerMinion
import io.github.itsflicker.minion.util.createMinionEntity
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

/**
 * @author wlys
 * @since 2022/6/24 22:55
 */
@MinionImpl("小麦小人")
class WheatMinion(owner: OfflinePlayer, location: Location) : FarmerMinion(owner, location) {

    override val seed: Material
        get() = Material.WHEAT

    override val entity: AdyArmorStand by lazy {
        createMinionEntity(location, ItemStack(Material.WOODEN_HOE), item, color)
    }

    override val color: Color
        get() = Color.YELLOW

    override val resource: ItemStack
        get() = ItemStack(Material.WHEAT)

}