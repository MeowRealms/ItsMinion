package io.github.itsflicker.minion.common

import taboolib.library.configuration.Conversion
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.MapConverter

data class MinionType(
    val id: String = "",
    val parent: String = "",
    val tiers: MutableList<MinionTier> = mutableListOf()
) {

    lateinit var conf: Configuration

    companion object {

        @JvmStatic
        val types = mutableListOf<MinionType>()
    }
}

data class MinionTier(
    val tier: Int = 0,
    val color: String = "",
    val sleep: String = "",
    val storage: Int = 0,
    val item: String = "",
    @Conversion(MapConverter::class)
    val hand: MutableMap<String, *> = mutableMapOf("material" to "stone")
)