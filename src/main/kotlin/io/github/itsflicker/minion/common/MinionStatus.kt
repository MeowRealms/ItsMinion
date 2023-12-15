package io.github.itsflicker.minion.common

enum class MinionStatus(val text: List<String>) {
    NULL(emptyList()),

    NO_SPACE(listOf("§c/!\\", "§c没有工作空间! :(")),

    FULL(listOf("§c/!\\", "§c我的背包满了! :("))
}