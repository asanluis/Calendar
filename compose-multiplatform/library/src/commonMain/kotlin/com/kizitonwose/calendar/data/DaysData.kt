package com.kizitonwose.calendar.data

import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.plusDays
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil

internal fun getCalendarDaysData(
    desiredStartDate: LocalDate,
    desiredEndDate: LocalDate,
): Set<CalendarDay> {
    return List((desiredStartDate.daysUntil(desiredEndDate))) { index ->
        desiredStartDate.plusDays(index)
    }.map {  date -> CalendarDay(date, DayPosition.InDate) }.toSet()
}
