package io.github.itsflicker.minion.common

import taboolib.library.configuration.Conversion
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.MapConverter

data class MinionType(
    val id: String,
    val type: String,
    val tiers: List<MinionTier>
) {

    lateinit var conf: Configuration

    companion object {

        @JvmStatic
        val types = mutableListOf<MinionType>()
    }
}

data class MinionTier(
    val tier: Int,
    val color: String,
    val sleep: String,
    val storage: Int,
    val item: String,
    @Conversion(MapConverter::class)
    val hand: Map<String, *>
)