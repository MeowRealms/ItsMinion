package io.github.itsflicker.minion.common

import java.util.*

/**
 * @author wlys
 * @since 2022/5/20 23:57
 */
class MinionInfo(
    val uuid: UUID,
    val level: Int,
    var amount: Int,
    val totalGenerated: Int,
    val maxStorage: Int,
    val delay: Long
)