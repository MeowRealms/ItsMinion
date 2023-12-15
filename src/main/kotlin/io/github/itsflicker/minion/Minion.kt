package io.github.itsflicker.minion

import ink.ptms.adyeshach.impl.DefaultAdyeshachBooster
import io.github.itsflicker.minion.internal.Loader
import io.github.itsflicker.minion.util.coroutineScope
import kotlinx.coroutines.cancel
import taboolib.common.LifeCycle
import taboolib.common.TabooLibCommon
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.io.runningClassesWithoutLibrary
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.disablePlugin
import taboolib.expansion.ioc.IOCReader
import taboolib.module.configuration.Configuration

@RuntimeDependencies(
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    ),
    RuntimeDependency(
        value = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3",
        relocate = ["!kotlin.", "!kotlin@kotlin_version_escape@."]
    )
)
object Minion : Plugin() {

    init {
        TabooLibCommon.postpone(LifeCycle.LOAD) {
            try {
                DefaultAdyeshachBooster.startup()
            } catch (ex: Throwable) {
                ex.printStackTrace()
                disablePlugin()
            }
        }
    }

//    @Config
    lateinit var conf: Configuration
        private set

    override fun onEnable() {
        coroutineScope
        Loader.loadTypes()
        IOCReader.readRegister(runningClassesWithoutLibrary)
    }

    override fun onDisable() {
        ArrayList(MinionAPI.runningMinions).forEach {
            it.viewings.forEach { player ->
                player.closeInventory()
            }
            it.destroy()
        }
        coroutineScope.cancel()
    }
}