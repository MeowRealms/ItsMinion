package io.github.itsflicker.minion

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

/**
 * @author wlys
 * @since 2022/5/9 16:57
 */
@CommandHeader("minion", permission = "minion.access")
object MinionCommand {

    @CommandBody
    val main = mainCommand {

    }
}