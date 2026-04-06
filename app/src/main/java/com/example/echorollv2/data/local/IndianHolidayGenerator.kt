package com.example.echorollv2.data.local

import com.example.echorollv2.data.local.entity.HolidayEntity

object IndianHolidayGenerator {

    /**
     * Generates a list of holidays for a specific Indian state and year.
     * Uses a mix of fixed-date and a pre-calculated lookup for moving festivals (2024-2030).
     */
    fun generate(year: Int, stateCode: String): List<HolidayEntity> {
        val holidays = mutableListOf<HolidayEntity>()

        // 1. FIXED NATIONAL HOLIDAYS (Every Year)
        holidays.add(HolidayEntity(date = "$year-01-26", name = "Republic Day", type = "Automatic"))
        holidays.add(HolidayEntity(date = "$year-08-15", name = "Independence Day", type = "Automatic"))
        holidays.add(HolidayEntity(date = "$year-10-02", name = "Gandhi Jayanti", type = "Automatic"))
        holidays.add(HolidayEntity(date = "$year-12-25", name = "Christmas Day", type = "Automatic"))
        holidays.add(HolidayEntity(date = "$year-04-14", name = "Ambedkar Jayanti", type = "Automatic"))
        holidays.add(HolidayEntity(date = "$year-01-14", name = "Makar Sankranti / Pongal", type = "Automatic"))

        // 2. STATE-SPECIFIC FIXED HOLIDAYS
        when (stateCode) {
            "WB" -> {
                holidays.add(HolidayEntity(date = "$year-01-23", name = "Netaji Birth Anniversary", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-15", name = "Poila Baisakh (Bengali New Year)", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-05-09", name = "Rabindra Jayanti", type = "Automatic"))
            }
            "KA" -> {
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Kannada Rajyotsava", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Vishu / Puthandu", type = "Automatic"))
            }
            "MH" -> {
                holidays.add(HolidayEntity(date = "$year-05-01", name = "Maharashtra Day", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-02-19", name = "Chhatrapati Shivaji Maharaj Jayanti", type = "Automatic"))
            }
            "KL" -> {
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Vishu", type = "Automatic"))
            }
            "TN" -> {
                holidays.add(HolidayEntity(date = "$year-01-14", name = "Pongal", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Tamil New Year", type = "Automatic"))
            }
            "PB" -> {
                holidays.add(HolidayEntity(date = "$year-04-13", name = "Baisakhi", type = "Automatic"))
            }
        }

        // 3. LUNISOLAR MOVING FESTIVALS (2024-2030 LOOKUP)
        getMovingFestivals(year, stateCode).forEach { 
            holidays.add(it)
        }

        return holidays.distinctBy { it.date + it.name }
    }

    private fun getMovingFestivals(year: Int, stateCode: String): List<HolidayEntity> {
        val list = mutableListOf<HolidayEntity>()
        
        when (year) {
            2024 -> {
                list.add(HolidayEntity(date = "2024-03-25", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2024-11-01", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2024-10-12", name = "Dussehra / Vijaya Dashami", type = "Automatic"))
                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2024-10-10", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2024-10-11", name = "Durga Puja (Ashtami)", type = "Automatic"))
                }
            }
            2025 -> {
                list.add(HolidayEntity(date = "2025-03-14", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2025-10-20", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2025-10-02", name = "Dussehra", type = "Automatic"))
                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2025-09-29", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2025-09-30", name = "Durga Puja (Ashtami)", type = "Automatic"))
                }
            }
            2026 -> {
                list.add(HolidayEntity(date = "2026-03-04", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-11-08", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-10-20", name = "Dussehra", type = "Automatic"))
                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2026-10-17", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2026-10-18", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2026-10-19", name = "Durga Puja (Navami)", type = "Automatic"))
                }
            }
            2027 -> {
                list.add(HolidayEntity(date = "2027-03-22", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-10-29", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-10-09", name = "Dussehra", type = "Automatic"))
            }
            2028 -> {
                list.add(HolidayEntity(date = "2028-03-11", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-10-17", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-09-28", name = "Dussehra", type = "Automatic"))
            }
            2029 -> {
                list.add(HolidayEntity(date = "2029-03-01", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-11-05", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-10-17", name = "Dussehra", type = "Automatic"))
            }
            2030 -> {
                list.add(HolidayEntity(date = "2030-03-19", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-10-26", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-10-06", name = "Dussehra", type = "Automatic"))
            }
        }
        
        return list
    }
}
