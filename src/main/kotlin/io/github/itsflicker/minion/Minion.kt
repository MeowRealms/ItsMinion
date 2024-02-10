package io.github.itsflicker.minion

import io.github.itsflicker.minion.internal.Loader
import taboolib.common.io.runningExactClasses
import taboolib.common.platform.Plugin
import taboolib.expansion.ioc.IOCReader
import taboolib.module.configuration.Configuration

object Minion : Plugin() {

//    @Config
    lateinit var conf: Configuration
        private set

    override fun onEnable() {
        Loader.loadTypes()
        IOCReader.readRegister(runningExactClasses)
    }

    override fun onDisable() {
        ArrayList(MinionAPI.runningMinions).forEach {
            it.destroy()
        }
    }
}