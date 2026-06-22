package com.sam.myapplication.data

import java.time.LocalDate

enum class HolidayType {
    REGULAR, SPECIAL, LOCAL
}

data class Holiday(
    val name: String,
    val type: HolidayType,
    val date: LocalDate? = null, // For fixed dates
    val month: Int? = null,
    val day: Int? = null
)

object HolidayHelper {
    private val fixedHolidays = listOf(
        Holiday("New Year's Day", HolidayType.REGULAR, month = 1, day = 1),
        Holiday("Chinese New Year", HolidayType.SPECIAL, month = 2, day = 17), // 2026
        Holiday("EDSA Revolution Anniversary", HolidayType.SPECIAL, month = 2, day = 25),
        Holiday("Maundy Thursday", HolidayType.REGULAR, month = 4, day = 2), // 2026
        Holiday("Good Friday", HolidayType.REGULAR, month = 4, day = 3), // 2026
        Holiday("Araw ng Kagitingan", HolidayType.REGULAR, month = 4, day = 9),
        Holiday("Eid'l Fitr", HolidayType.REGULAR, month = 3, day = 20), // 2026 estimate
        Holiday("Labor Day", HolidayType.REGULAR, month = 5, day = 1),
        Holiday("Independence Day", HolidayType.REGULAR, month = 6, day = 12),
        Holiday("Eid'l Adha", HolidayType.REGULAR, month = 5, day = 27), // 2026 estimate
        Holiday("Ninoy Aquino Day", HolidayType.SPECIAL, month = 8, day = 21),
        Holiday("National Heroes Day", HolidayType.REGULAR, month = 8, day = 31), // Last Mon of Aug 2026
        Holiday("All Saints' Day", HolidayType.SPECIAL, month = 11, day = 1),
        Holiday("All Souls' Day", HolidayType.SPECIAL, month = 11, day = 2),
        Holiday("Bonifacio Day", HolidayType.REGULAR, month = 11, day = 30),
        Holiday("Feast of the Immaculate Conception", HolidayType.SPECIAL, month = 12, day = 8),
        Holiday("Christmas Eve", HolidayType.SPECIAL, month = 12, day = 24),
        Holiday("Christmas Day", HolidayType.REGULAR, month = 12, day = 25),
        Holiday("Rizal Day", HolidayType.REGULAR, month = 12, day = 30),
        Holiday("Last Day of the Year", HolidayType.SPECIAL, month = 12, day = 31),
        
        // Angeles City / Local
        Holiday("Burning of Angeles", HolidayType.LOCAL, month = 10, day = 8),
        Holiday("Apu Fiesta (Angeles City)", HolidayType.LOCAL, month = 10, day = 23), // 4th Friday of Oct 2026 is 23
        Holiday("Fiestang Kuliat", HolidayType.LOCAL, month = 10, day = 30) // Last Friday of Oct 2026 is 30
    )

    fun getHoliday(date: LocalDate): Holiday? {
        return fixedHolidays.find { 
            it.month == date.monthValue && it.day == date.dayOfMonth
        }
    }
    
    fun getHolidayInfo(date: LocalDate): String? {
        val holiday = getHoliday(date) ?: return null
        val typeStr = when(holiday.type) {
            HolidayType.REGULAR -> "Regular Holiday"
            HolidayType.SPECIAL -> "Special Non-Working Holiday"
            HolidayType.LOCAL -> "Local Holiday (Angeles City)"
        }
        return "[$typeStr] ${holiday.name}"
    }
}
