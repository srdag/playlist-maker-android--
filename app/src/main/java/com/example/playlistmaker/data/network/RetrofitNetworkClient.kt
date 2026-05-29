package com.example.playlistmaker.data.network

import android.util.Log
import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.NetworkClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetrofitNetworkClient(private val storage: Storage) : NetworkClient {

    private val iTunesBaseUrl = "https://itunes.apple.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)

    override suspend fun doRequest(dto: Any): BaseResponse {
        if (dto is TracksSearchRequest) {
            val requestText = dto.expression.trim().lowercase()

            Log.d("RetrofitNetworkClient", "Searching for: ${dto.expression}")

            if (requestText == "error" || requestText == "empty") {
                try {
                    val searchList = storage.search(dto.expression)
                    return TracksSearchResponse(searchList).apply { resultCode = 200 }
                } catch (e: IOException) {
                    return BaseResponse().apply { resultCode = -1 }
                }
            }

            return try {
                val response = iTunesService.search(dto.expression)
                Log.d("RetrofitNetworkClient", "Response received: ${response.results.size} tracks")
                response.apply { resultCode = 200 }
            } catch (e: Exception) {
                Log.e("RetrofitNetworkClient", "Network error occurred", e)
                BaseResponse().apply { resultCode = -1 }
            }
        } else {
            return BaseResponse().apply { resultCode = 400 }
        }
    }
}
