package io.github.itsflicker.minion

import io.github.itsflicker.minion.common.BaseMinion
import io.github.itsflicker.minion.common.MinionImpl
import taboolib.common.io.runningClassesWithoutLibrary
import taboolib.common.platform.Schedule
import taboolib.expansion.ioc.linker.linkedIOCList

/**
 * @author wlys
 * @since 2022/5/9 15:15
 */
object MinionAPI {

    val registeredMinions = runningClassesWithoutLibrary
        .filter { it.isAnnotationPresent(MinionImpl::class.java) }
        .associateBy { it.getAnnotation(MinionImpl::class.java).value }

    val runningMinions = linkedIOCList<BaseMinion>()

}