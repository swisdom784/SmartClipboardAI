package com.smartclipboard.ai.presentation.settings

object CollectionWindowPolicy {
    private const val HOUR_MILLIS = 60L * 60L * 1000L

    fun checkpointMillisFor(
        option: CollectionWindowOption,
        customHours: Int,
        nowMillis: Long
    ): Long? {
        val durationMillis = when (option) {
            CollectionWindowOption.FOLLOW_LAST_SYNC -> return null
            CollectionWindowOption.LAST_1_HOUR -> HOUR_MILLIS
            CollectionWindowOption.LAST_24_HOURS -> 24L * HOUR_MILLIS
            CollectionWindowOption.LAST_7_DAYS -> 7L * 24L * HOUR_MILLIS
            CollectionWindowOption.CUSTOM_HOURS -> customHours.coerceAtLeast(1) * HOUR_MILLIS
        }
        return (nowMillis - durationMillis).coerceAtLeast(0L)
    }
}
