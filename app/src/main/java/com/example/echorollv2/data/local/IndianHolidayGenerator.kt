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
            "AP" -> { // Andhra Pradesh
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Andhra Pradesh Formation Day", type = "Automatic"))
            }
            "AR" -> { // Arunachal Pradesh
                holidays.add(HolidayEntity(date = "$year-02-20", name = "Statehood Day", type = "Automatic"))
            }
            "AS" -> { // Assam
                holidays.add(HolidayEntity(date = "$year-01-31", name = "Me-Dam-Me-Phi", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-11-24", name = "Lachit Divas", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-12-02", name = "Asom Divas (Assam Day)", type = "Automatic"))
            }
            "BR" -> { // Bihar
                holidays.add(HolidayEntity(date = "$year-03-22", name = "Bihar Diwas", type = "Automatic"))
            }
            "CG" -> { // Chhattisgarh
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Chhattisgarh Foundation Day", type = "Automatic"))
            }
            "GA" -> { // Goa
                holidays.add(HolidayEntity(date = "$year-05-30", name = "Goa Statehood Day", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-12-03", name = "Feast of St. Francis Xavier", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-12-19", name = "Goa Liberation Day", type = "Automatic"))
            }
            "GJ" -> { // Gujarat
                holidays.add(HolidayEntity(date = "$year-05-01", name = "Gujarat Day", type = "Automatic"))
            }
            "HR" -> { // Haryana
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Haryana Day", type = "Automatic"))
            }
            "HP" -> { // Himachal Pradesh
                holidays.add(HolidayEntity(date = "$year-01-25", name = "Statehood Day", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-15", name = "Himachal Day", type = "Automatic"))
            }
            "JH" -> { // Jharkhand
                holidays.add(HolidayEntity(date = "$year-11-15", name = "Jharkhand Foundation Day", type = "Automatic"))
            }
            "KA" -> { // Karnataka
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Kannada Rajyotsava", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Vishu / Puthandu", type = "Automatic")) // Shared cultural region
            }
            "KL" -> { // Kerala
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Vishu", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Kerala Piravi", type = "Automatic"))
            }
            "MP" -> { // Madhya Pradesh
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Madhya Pradesh Foundation Day", type = "Automatic"))
            }
            "MH" -> { // Maharashtra
                holidays.add(HolidayEntity(date = "$year-02-19", name = "Chhatrapati Shivaji Maharaj Jayanti", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-05-01", name = "Maharashtra Day", type = "Automatic"))
            }
            "MN" -> { // Manipur
                holidays.add(HolidayEntity(date = "$year-01-21", name = "Statehood Day", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-08-13", name = "Patriots' Day", type = "Automatic"))
            }
            "ML" -> { // Meghalaya
                holidays.add(HolidayEntity(date = "$year-01-21", name = "Meghalaya Day", type = "Automatic"))
            }
            "MZ" -> { // Mizoram
                holidays.add(HolidayEntity(date = "$year-02-20", name = "Statehood Day", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-06-30", name = "Remna Ni (Peace Agreement Day)", type = "Automatic"))
            }
            "NL" -> { // Nagaland
                holidays.add(HolidayEntity(date = "$year-12-01", name = "Statehood Day", type = "Automatic"))
            }
            "OR" -> { // Odisha
                holidays.add(HolidayEntity(date = "$year-04-01", name = "Utkal Divas (Odisha Day)", type = "Automatic"))
            }
            "PB" -> { // Punjab
                holidays.add(HolidayEntity(date = "$year-03-23", name = "Shaheedi Diwas", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-13", name = "Baisakhi", type = "Automatic"))
            }
            "RJ" -> { // Rajasthan
                holidays.add(HolidayEntity(date = "$year-03-30", name = "Rajasthan Day", type = "Automatic"))
            }
            "SK" -> { // Sikkim
                holidays.add(HolidayEntity(date = "$year-05-16", name = "Sikkim State Day", type = "Automatic"))
            }
            "TN" -> { // Tamil Nadu
                holidays.add(HolidayEntity(date = "$year-01-14", name = "Pongal", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-14", name = "Tamil New Year", type = "Automatic"))
            }
            "TG" -> { // Telangana
                holidays.add(HolidayEntity(date = "$year-06-02", name = "Telangana Formation Day", type = "Automatic"))
            }
            "TR" -> { // Tripura
                holidays.add(HolidayEntity(date = "$year-01-21", name = "Statehood Day", type = "Automatic"))
            }
            "UP" -> { // Uttar Pradesh
                holidays.add(HolidayEntity(date = "$year-01-24", name = "UP Diwas", type = "Automatic"))
            }
            "UK" -> { // Uttarakhand
                holidays.add(HolidayEntity(date = "$year-11-09", name = "Uttarakhand Foundation Day", type = "Automatic"))
            }
            "WB" -> { // West Bengal
                holidays.add(HolidayEntity(date = "$year-01-23", name = "Netaji Birth Anniversary", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-04-15", name = "Poila Baisakh (Bengali New Year)", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-05-09", name = "Rabindra Jayanti", type = "Automatic"))
            }
            // UTs
            "JK" -> { // Jammu & Kashmir
                holidays.add(HolidayEntity(date = "$year-10-26", name = "Accession Day", type = "Automatic"))
            }
            "PY" -> { // Puducherry
                holidays.add(HolidayEntity(date = "$year-08-16", name = "De Jure Transfer Day", type = "Automatic"))
            }
            "LA" -> { // Ladakh
                holidays.add(HolidayEntity(date = "$year-10-31", name = "Ladakh Foundation Day", type = "Automatic"))
            }
            "LD" -> { // Lakshadweep
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Lakshadweep Foundation Day", type = "Automatic"))
            }
            "DD", "DN" -> { // Dadra and Nagar Haveli & Daman and Diu
                holidays.add(HolidayEntity(date = "$year-08-02", name = "Liberation Day of Dadra & Nagar Haveli", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-12-19", name = "Liberation Day of Daman & Diu", type = "Automatic"))
            }
            "CH" -> { // Chandigarh
                // Chandigarh typically observes Punjab and Haryana holidays
                holidays.add(HolidayEntity(date = "$year-04-13", name = "Baisakhi", type = "Automatic"))
                holidays.add(HolidayEntity(date = "$year-11-01", name = "Haryana Day / Chandigarh Formation Day", type = "Automatic"))
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
            2026 -> {
                list.add(HolidayEntity(date = "2026-01-12", name = "Birth of Swami Vivekananda", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-01-22", name = "Day before Saraswati Puja", type = "Manual"))
                list.add(HolidayEntity(date = "2026-01-23", name = "Netaji Birth Anniversary and Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-02-04", name = "Shab-e-Barat", type = "Manual"))

                list.add(HolidayEntity(date = "2026-03-03", name = "Doljatra", type = "Manual"))
                list.add(HolidayEntity(date = "2026-03-04", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-03-17", name = "Birthday of Shri Harichand Thakur", type = "Manual"))
                list.add(HolidayEntity(date = "2026-03-20", name = "Day before Id-Ul-Fitr", type = "Manual"))
                list.add(HolidayEntity(date = "2026-03-21", name = "Id-Ul-Fitr", type = "Manual"))
                list.add(HolidayEntity(date = "2026-03-27", name = "Ram Navami", type = "Manual"))
                list.add(HolidayEntity(date = "2026-03-31", name = "Mahavir Jayanti", type = "Manual"))

                list.add(HolidayEntity(date = "2026-04-03", name = "Good Friday", type = "Manual"))

                list.add(HolidayEntity(date = "2026-05-01", name = "May Day and Buddha Purnima", type = "Manual"))
                list.add(HolidayEntity(date = "2026-05-26", name = "Day before Eid-ud-Zoha (Bakrid)", type = "Manual"))
                list.add(HolidayEntity(date = "2026-05-27", name = "Eid-ud-Zoha (Bakrid)", type = "Manual"))

                list.add(HolidayEntity(date = "2026-06-26", name = "Muharram", type = "Manual"))
                list.add(HolidayEntity(date = "2026-07-16", name = "Rathayatra", type = "Manual"))

                list.add(HolidayEntity(date = "2026-08-26", name = "Fateha-Dwaz-Daham", type = "Manual"))
                list.add(HolidayEntity(date = "2026-08-28", name = "Rakhi Bandhan", type = "Manual"))

                list.add(HolidayEntity(date = "2026-09-04", name = "Janmastami", type = "Manual"))
                list.add(HolidayEntity(date = "2026-10-20", name = "Dussehra", type = "Automatic"))

                list.add(HolidayEntity(date = "2026-11-08", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2026-11-11", name = "Bhatridwitiya", type = "Manual"))
                list.add(HolidayEntity(date = "2026-11-15", name = "Chhath Puja", type = "Manual"))
                list.add(HolidayEntity(date = "2026-11-16", name = "Day after Chhath Puja", type = "Manual"))
                list.add(HolidayEntity(date = "2026-11-24", name = "Guru Nanak's Birthday", type = "Manual"))

                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2026-10-17", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2026-10-18", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2026-10-19", name = "Durga Puja (Navami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2026-09-17", name = "Viswakarma Puja", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-21", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-22", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-23", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-24", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-25", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-26", name = "Laxmi Puja", type = "Manual"))

                    list.add(HolidayEntity(date = "2026-10-10", name = "Mahalaya", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-15", name = "Durga Puja (Panchami)", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-10-16", name = "Durga Puja (Sashti)", type = "Manual"))

                    list.add(HolidayEntity(date = "2026-11-09", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-11-10", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-11-12", name = "College Holiday", type = "Manual"))
                    list.add(HolidayEntity(date = "2026-11-13", name = "College Holiday", type = "Manual"))
                }
            }
            2027 -> {
                list.add(HolidayEntity(date = "2027-01-12", name = "Birth of Swami Vivekananda", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-02-10", name = "Day before Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-02-11", name = "Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-01-24", name = "Shab-e-Barat", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-03-21", name = "Doljatra", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-03-22", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-04-09", name = "Birthday of Shri Harichand Thakur", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-03-09", name = "Day before Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-03-10", name = "Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-04-15", name = "Ram Navami", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-04-20", name = "Mahavir Jayanti", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-03-26", name = "Good Friday", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-05-01", name = "May Day", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-05-20", name = "Buddha Purnima", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-05-16", name = "Day before Eid-ud-Zoha (Bakrid)", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-05-17", name = "Eid-ud-Zoha (Bakrid)", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-06-15", name = "Muharram", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-07-05", name = "Rathayatra", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-08-15", name = "Fateha-Dwaz-Daham", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-08-17", name = "Rakhi Bandhan", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-08-25", name = "Janmastami", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-10-09", name = "Dussehra", type = "Automatic"))

                list.add(HolidayEntity(date = "2027-10-29", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-10-31", name = "Bhatridwitiya", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-11-04", name = "Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-11-05", name = "Day after Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2027-11-14", name = "Guru Nanak's Birthday", type = "Automatic"))

                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2027-10-06", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-07", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-08", name = "Durga Puja (Navami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-09-17", name = "Viswakarma Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2027-10-11", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-12", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-13", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-14", name = "College Holiday", type = "Automatic"))

                    list.add(HolidayEntity(date = "2027-10-15", name = "Laxmi Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2027-09-30", name = "Mahalaya", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-04", name = "Durga Puja (Panchami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-10-05", name = "Durga Puja (Sashti)", type = "Automatic"))

                    list.add(HolidayEntity(date = "2027-11-01", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-11-02", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2027-11-03", name = "College Holiday", type = "Automatic"))
                }
            }
            2028 -> {
                list.add(HolidayEntity(date = "2028-01-12", name = "Birth of Swami Vivekananda", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-01-30", name = "Day before Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-01-31", name = "Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-01-14", name = "Shab-e-Barat", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-03-10", name = "Doljatra", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-03-11", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-03-28", name = "Birthday of Shri Harichand Thakur", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-02-27", name = "Day before Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-02-28", name = "Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-04-04", name = "Ram Navami", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-04-08", name = "Mahavir Jayanti", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-04-14", name = "Good Friday", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-05-01", name = "May Day", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-05-08", name = "Buddha Purnima", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-05-05", name = "Day before Eid-ud-Zoha (Bakrid)", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-05-06", name = "Eid-ud-Zoha (Bakrid)", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-06-04", name = "Muharram", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-06-24", name = "Rathayatra", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-08-04", name = "Fateha-Dwaz-Daham", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-08-05", name = "Rakhi Bandhan", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-08-12", name = "Janmastami", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-09-28", name = "Dussehra", type = "Automatic"))

                list.add(HolidayEntity(date = "2028-10-17", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-10-19", name = "Bhatridwitiya", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-10-24", name = "Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-10-25", name = "Day after Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2028-11-02", name = "Guru Nanak's Birthday", type = "Automatic"))

                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2028-09-25", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-26", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-27", name = "Durga Puja (Navami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-17", name = "Viswakarma Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2028-09-29", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-30", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-10-01", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-10-02", name = "College Holiday", type = "Automatic"))

                    list.add(HolidayEntity(date = "2028-10-03", name = "Laxmi Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2028-09-18", name = "Mahalaya", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-23", name = "Durga Puja (Panchami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-09-24", name = "Durga Puja (Sashti)", type = "Automatic"))

                    list.add(HolidayEntity(date = "2028-10-18", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-10-20", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2028-10-23", name = "College Holiday", type = "Automatic"))
                }
            }
            2029 -> {
                list.add(HolidayEntity(date = "2029-01-12", name = "Birth of Swami Vivekananda", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-02-17", name = "Day before Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-02-18", name = "Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-01-02", name = "Shab-e-Barat", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-02-28", name = "Doljatra", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-03-01", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-04-16", name = "Birthday of Shri Harichand Thakur", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-02-15", name = "Day before Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-02-16", name = "Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-04-23", name = "Ram Navami", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-04-27", name = "Mahavir Jayanti", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-03-30", name = "Good Friday", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-05-01", name = "May Day", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-05-27", name = "Buddha Purnima", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-04-24", name = "Day before Eid-ud-Zoha (Bakrid)", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-04-25", name = "Eid-ud-Zoha (Bakrid)", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-05-24", name = "Muharram", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-07-13", name = "Rathayatra", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-07-24", name = "Fateha-Dwaz-Daham", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-08-24", name = "Rakhi Bandhan", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-08-31", name = "Janmastami", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-10-17", name = "Dussehra", type = "Automatic"))

                list.add(HolidayEntity(date = "2029-11-05", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-11-07", name = "Bhatridwitiya", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-11-11", name = "Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-11-12", name = "Day after Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2029-11-21", name = "Guru Nanak's Birthday", type = "Automatic"))

                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2029-10-14", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-15", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-16", name = "Durga Puja (Navami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-09-17", name = "Viswakarma Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2029-10-18", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-19", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-20", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-21", name = "College Holiday", type = "Automatic"))

                    list.add(HolidayEntity(date = "2029-10-22", name = "Laxmi Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2029-10-07", name = "Mahalaya", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-12", name = "Durga Puja (Panchami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-10-13", name = "Durga Puja (Sashti)", type = "Automatic"))

                    list.add(HolidayEntity(date = "2029-11-06", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-11-08", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-11-09", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2029-11-10", name = "College Holiday", type = "Automatic"))
                }
            }
            2030 -> {
                list.add(HolidayEntity(date = "2030-01-12", name = "Birth of Swami Vivekananda", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-02-06", name = "Day before Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-02-07", name = "Saraswati Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-12-12", name = "Shab-e-Barat", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-03-18", name = "Doljatra", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-03-19", name = "Holi", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-04-05", name = "Birthday of Shri Harichand Thakur", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-02-04", name = "Day before Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-02-05", name = "Id-Ul-Fitr", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-04-12", name = "Ram Navami", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-04-17", name = "Mahavir Jayanti", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-04-19", name = "Good Friday", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-05-01", name = "May Day", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-05-16", name = "Buddha Purnima", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-04-13", name = "Day before Eid-ud-Zoha (Bakrid)", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-04-14", name = "Eid-ud-Zoha (Bakrid)", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-05-13", name = "Muharram", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-07-02", name = "Rathayatra", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-07-13", name = "Fateha-Dwaz-Daham", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-08-13", name = "Rakhi Bandhan", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-08-20", name = "Janmastami", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-10-06", name = "Dussehra", type = "Automatic"))

                list.add(HolidayEntity(date = "2030-10-26", name = "Diwali", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-10-28", name = "Bhatridwitiya", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-10-31", name = "Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-11-01", name = "Day after Chhath Puja", type = "Automatic"))
                list.add(HolidayEntity(date = "2030-11-10", name = "Guru Nanak's Birthday", type = "Automatic"))

                if (stateCode == "WB") {
                    list.add(HolidayEntity(date = "2030-10-03", name = "Durga Puja (Saptami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-04", name = "Durga Puja (Ashtami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-05", name = "Durga Puja (Navami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-09-17", name = "Viswakarma Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2030-10-07", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-08", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-09", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-10", name = "College Holiday", type = "Automatic"))

                    list.add(HolidayEntity(date = "2030-10-11", name = "Laxmi Puja", type = "Automatic"))

                    list.add(HolidayEntity(date = "2030-09-27", name = "Mahalaya", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-01", name = "Durga Puja (Panchami)", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-02", name = "Durga Puja (Sashti)", type = "Automatic"))

                    list.add(HolidayEntity(date = "2030-10-27", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-29", name = "College Holiday", type = "Automatic"))
                    list.add(HolidayEntity(date = "2030-10-30", name = "College Holiday", type = "Automatic"))
                }
            }
        }

        return list
    }
}