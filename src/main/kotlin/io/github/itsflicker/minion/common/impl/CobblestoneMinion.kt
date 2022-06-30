package io.github.itsflicker.minion.common.impl

import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import io.github.itsflicker.minion.common.type.MiningMinion
import io.github.itsflicker.minion.common.MinionImpl
import io.github.itsflicker.minion.util.createMinionEntity
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack

/**
 * @author wlys
 * @since 2022/5/9 14:38
 */
@MinionImpl("圆石小人")
class CobblestoneMinion(owner: OfflinePlayer, location: Location) : MiningMinion(owner, location) {

    override val block: Material
        get() = Material.COBBLESTONE

    override val entity: AdyArmorStand by lazy {
        createMinionEntity(location, ItemStack(Material.WOODEN_PICKAXE), item, color)
    }

    override val color: Color
        get() = Color.GRAY

    override val resource: ItemStack
        get() = ItemStack(Material.COBBLESTONE)

}