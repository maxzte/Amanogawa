package id.anison.amanogawa.core.common

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

object ScheduleParser {
    private val daysMap = mapOf(
        "понеділок" to DayOfWeek.MONDAY,
        "вівторок" to DayOfWeek.TUESDAY,
        "середа" to DayOfWeek.WEDNESDAY,
        "четвер" to DayOfWeek.THURSDAY,
        "п'ятниця" to DayOfWeek.FRIDAY,
        "субота" to DayOfWeek.SATURDAY,
        "неділя" to DayOfWeek.SUNDAY
    )

    /**
     * Checks if the current time is within the release window defined by the schedule string.
     * Format: "~ вівторок 23:00 — середа 4:00"
     */
    fun isCurrentlyInWindow(schedule: String?, now: LocalDateTime = LocalDateTime.now()): Boolean {
        if (schedule.isNullOrBlank()) return false

        try {
            // Normalize: remove prefix and split by dash (standard or long dash)
            val cleanSchedule = schedule.replace("~", "").trim()
            val dash = if (cleanSchedule.contains("—")) "—" else "-"
            val parts = cleanSchedule.split(dash).map { it.trim() }
            
            if (parts.size != 2) return false

            val startPoint = parsePoint(parts[0]) ?: return false
            val endPoint = parsePoint(parts[1]) ?: return false

            // Find the occurrence of startPoint that is closest to 'now' but not in the future
            var actualStart = now.with(TemporalAdjusters.previousOrSame(startPoint.dayOfWeek))
                .withHour(startPoint.hour)
                .withMinute(startPoint.minute)
                .withSecond(0)
                .withNano(0)

            if (actualStart.isAfter(now)) {
                actualStart = actualStart.minusDays(7)
            }

            // Calculate 'actualEnd' relative to 'actualStart'
            // We use nextOrSame because the end date is always after or on the same day as start date
            var actualEnd = actualStart.with(TemporalAdjusters.nextOrSame(endPoint.dayOfWeek))
                .withHour(endPoint.hour)
                .withMinute(endPoint.minute)
                .withSecond(0)
                .withNano(0)
            
            // If end is before start (e.g. Tue 23:00 - Tue 01:00), it must be next week's Tuesday
            if (actualEnd.isBefore(actualStart)) {
                actualEnd = actualEnd.plusDays(7)
            }

            return !now.isBefore(actualStart) && !now.isAfter(actualEnd)
        } catch (e: Exception) {
            Log.e(tag = "ScheduleParser") { "Failed to parse schedule: $schedule, error: $e" }
            return false
        }
    }

    private fun parsePoint(point: String): SchedulePoint? {
        // Expected format: "вівторок 23:00"
        val regex = Regex("(\\w+)\\s+(\\d{1,2}):(\\d{2})")
        val match = regex.find(point.lowercase()) ?: return null

        val dayName = match.groupValues[1]
        val hour = match.groupValues[2].toInt()
        val minute = match.groupValues[3].toInt()

        val dayOfWeek = daysMap[dayName] ?: return null
        return SchedulePoint(dayOfWeek, hour, minute)
    }

    private data class SchedulePoint(val dayOfWeek: DayOfWeek, val hour: Int, val minute: Int)
}
