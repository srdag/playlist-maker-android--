package com.example.playlistmaker.creator

import com.example.playlistmaker.data.dto.TrackDto
import java.io.IOException

class Storage {
    fun search(request: String): List<TrackDto> {

        when (request.trim().lowercase()) {
            "error" -> throw IOException("Имитация сетевой ошибки")
            "empty" -> return emptyList()
        }
        return emptyList()
    }
}



