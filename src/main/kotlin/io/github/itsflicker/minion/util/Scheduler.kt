package io.github.itsflicker.minion.util

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import taboolib.common.util.unsafeLazy
import taboolib.platform.util.bukkitPlugin
import java.lang.Runnable
import kotlin.coroutines.CoroutineContext

val coroutineScope: CoroutineScope by unsafeLazy { CoroutineScope(SupervisorJob()) }

// store an internal reference to the bukkit scheduler
val bukkitScheduler by lazy {
    Bukkit.getScheduler()
}

@OptIn(InternalCoroutinesApi::class)
class BukkitDispatcher(val async: Boolean = false) : CoroutineDispatcher(), Delay {

    private val runTaskLater: (Plugin, Runnable, Long) -> BukkitTask =
        if (async)
            bukkitScheduler::runTaskLaterAsynchronously
        else
            bukkitScheduler::runTaskLater
    private val runTask: (Plugin, Runnable) -> BukkitTask =
        if (async)
            bukkitScheduler::runTaskAsynchronously
        else
            bukkitScheduler::runTask

    @ExperimentalCoroutinesApi
    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val task = runTaskLater(
            bukkitPlugin,
            Runnable {
                continuation.apply { resumeUndispatched(Unit) }
            },
            timeMillis / 50
        )
        continuation.invokeOnCancellation { task.cancel() }
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        if (!context.isActive) {
            return
        }

        if (!async && Bukkit.isPrimaryThread()) {
            block.run()
        } else {
            runTask(bukkitPlugin, block)
        }
    }
}