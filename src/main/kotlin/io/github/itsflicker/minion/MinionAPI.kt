package io.github.itsflicker.minion

import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionImpl
import taboolib.common.io.runningClasses
import taboolib.common.platform.Schedule

/**
 * @author wlys
 * @since 2022/5/9 15:15
 */
object MinionAPI {

    val registeredMinions = runningClasses
        .filter { it.isAnnotationPresent(MinionImpl::class.java) }
        .associateBy { it.getAnnotation(MinionImpl::class.java).value }

    val runningMinions = mutableListOf<BaseMinion>()

    val cachedMinions = mutableListOf<BaseMinion>()

    @Schedule(async = true, period = 20)
    fun e() {
        runningMinions.removeAll { it.markRemoved }
        runningMinions.forEach {
            it.onPreTick()
        }
        runningMinions.addAll(cachedMinions)
        cachedMinions.clear()
    }
}