package ru.cscenter.fingerpaint.db

import androidx.room.TypeConverter

@Suppress("unused")
enum class GameType(val id: Int) {
    CHOOSE_FIGURE(1),
    CHOOSE_LETTER(2),
    CHOOSE_FIGURE_COLOR(3),
    CHOOSE_LETTER_COLOR(4),
    CHOOSE_SPECIAL(5),
    DRAW_FIGURE(6),
    DRAW_LETTER(7),
    DRAW_SPECIAL(8)
}

@Suppress("unused")
class TypeConverter {
    @TypeConverter
    fun toGameType(id: Int): GameType? = GameType.values().find { it.id == id }

    @TypeConverter
    fun fromGameType(type: GameType) = type.id
}
