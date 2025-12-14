package com.example.personaltaskmanager.features.calendar_events.screens

import java.time.LocalDate

data class CalendarDay(
    val day: Int,
    val isCurrentMonth: Boolean,
    val date: LocalDate,
    val isValid: Boolean = true,
    var hasEvent: Boolean = false
)
