package io.github.itsflicker.minion.internal

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.entity.EntityTypes
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import org.bukkit.Location
import java.util.concurrent.ConcurrentHashMap

/**
 * @author wlys
 * @since 2022/5/25 20:29
 */
abstract class PublicHologram<T> {

    protected val map = ConcurrentHashMap<Int, T>()

    fun create(location: Location, content: List<String>) {
        content.forEachIndexed { index, line ->
            map[index] = create(location.clone().add(0.0, (((content.size - 1) - index) * 0.3), 0.0), line)
        }
    }

    fun teleport(location: Location) {
        map.forEach { (index, obj) ->
            teleport(obj, location.clone().add(0.0, (((map.size - 1) - index) * 0.3), 0.0))
        }
    }

    fun update(content: List<String>) {
        content.forEachIndexed { index, line ->
            if (index < map.size) {
                update(map[index]!!, line)
            }
        }
    }

    fun delete() {
        map.forEach { delete(it.value) }
    }

    protected abstract fun create(location: Location, line: String): T

    protected abstract fun update(obj: T, line: String)

    protected abstract fun teleport(obj: T, location: Location)

    protected abstract fun delete(obj: T)

    class AdyeshachImpl : PublicHologram<AdyArmorStand>() {

        override fun create(location: Location, line: String): AdyArmorStand {
            return AdyeshachAPI.getEntityManagerPublicTemporary().create(EntityTypes.ARMOR_STAND, location) {
                val npc = it as AdyArmorStand
                npc.setSmall(true)
                npc.setMarker(true)
                npc.setBasePlate(false)
                npc.setInvisible(true)
                npc.setCustomName(line)
                npc.setCustomNameVisible(true)
            } as AdyArmorStand
        }

        override fun update(obj: AdyArmorStand, line: String) {
            if (!obj.isDeleted) {
                obj.setCustomName(line)
            }
        }

        override fun teleport(obj: AdyArmorStand, location: Location) {
            if (!obj.isDeleted) {
                obj.teleport(location)
            }
        }

        override fun delete(obj: AdyArmorStand) {
            obj.delete()
        }
    }
}