package io.github.itsflicker.minion.internal

import io.github.itsflicker.minion.common.MinionType
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.common.util.unsafeLazy
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Configuration.Companion.getObject
import java.io.File

object Loader {

    private val folder by unsafeLazy {
        val folder = File(getDataFolder(), "minions")
        if (!folder.exists()) {
            arrayOf(
                "cobblestone.yml"
            ).forEach { releaseResourceFile("minions/$it", replace = true) }
        }
        folder
    }

    fun loadTypes() {
        MinionType.types.clear()
        filterFiles(folder).forEach { file ->
            val conf = Configuration.loadFromFile(file)
            val type = conf.getObject<MinionType>("", ignoreConstructor = false)
            type.conf = conf
            MinionType.types += type
        }
    }

    private fun filterFiles(file: File): List<File> {
        return mutableListOf<File>().apply {
            if (file.isDirectory) {
                file.listFiles()?.forEach {
                    addAll(filterFiles(it))
                }
            } else if (file.extension.equals("yml", true)) {
                add(file)
            }
        }
    }

}