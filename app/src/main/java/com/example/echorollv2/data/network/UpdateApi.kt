package com.example.echorollv2.data.network

import retrofit2.http.GET

interface UpdateApi {
    @GET("repos/KBasu21/EchoRoll/releases/latest")
    suspend fun getLatestRelease(): GitHubRelease
}
