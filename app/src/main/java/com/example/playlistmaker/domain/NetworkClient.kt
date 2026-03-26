package com.example.playlistmaker.domain

import com.example.playlistmaker.data.dto.BaseResponse

interface NetworkClient {
    fun doRequest(dto: Any): BaseResponse
}
