package bot

import bot.consts.TIME_FORMATTER
import java.time.LocalDateTime

fun Boolean.toYesNo() = if (this) "yes" else "no"

fun String.constToEng() = replace("_", " ").toLowerCase()

fun <T : Enum<T>> Enum<T>.constToEng() = name.replace("_", " ").toLowerCase()

fun LocalDateTime.localWithoutWeekday() = format(TIME_FORMATTER).drop(4)