package com.example.echorollv2.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class NagerHolidayDto(
    val date: String?,
    val localName: String?,
    val name: String?,
    val countryCode: String?,
    val fixed: Boolean?,
    val global: Boolean?,
    val counties: List<String>?,
    val types: List<String>?
)

interface HolidayApi {
    @GET("api/v3/PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Path("year") year: Int,
        @Path("countryCode") countryCode: String
    ): List<NagerHolidayDto>

    companion object {
        private const val BASE_URL = "https://date.nager.at/"

        fun create(): HolidayApi {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(HolidayApi::class.java)
        }
    }
}
