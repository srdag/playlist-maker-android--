package com.example.playlistmaker.creator

import com.example.playlistmaker.data.dto.TrackDto
import java.io.IOException

class Storage {
    private val listTracks = listOf(
        TrackDto(
            trackName = "Владивосток 2000",
            artistName = "Мумий Троль",
            trackTimeMillis = 158000, // 2:38
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/04/13/c8/0413c8ad-7daa-2c41-c0c1-3da40b9b27f9/source/100x100bb.jpg"
        ),
        TrackDto(
            trackName = "Группа крови",
            artistName = "Кино",
            trackTimeMillis = 283000, // 4:43
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/3e/97/56/3e975675-1a9c-eb45-c39e-b85b62a0f6e4/source/100x100bb.jpg"
        ),
        // Намеренно без обложки — чтобы проверить плейсхолдер.
        TrackDto(
            trackName = "Не смотри назад",
            artistName = "Ария",
            trackTimeMillis = 312000, // 5:12
            artworkUrl100 = null
        ),
        TrackDto(
            trackName = "Звезда по имени Солнце",
            artistName = "Кино",
            trackTimeMillis = 225000,
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/3e/97/56/3e975675-1a9c-eb45-c39e-b85b62a0f6e4/source/100x100bb.jpg"
        ),
        TrackDto(
            trackName = "Лондон",
            artistName = "Аквариум",
            trackTimeMillis = 272000,
            // Намеренно битая ссылка — Coil покажет error-плейсхолдер.
            artworkUrl100 = "https://example.invalid/cover.jpg"
        ),
        TrackDto(
            trackName = "На заре",
            artistName = "Альянс",
            trackTimeMillis = 230000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackName = "Перемен",
            artistName = "Кино",
            trackTimeMillis = 296000,
            artworkUrl100 = "https://is1-ssl.mzstatic.com/image/thumb/Music124/v4/3e/97/56/3e975675-1a9c-eb45-c39e-b85b62a0f6e4/source/100x100bb.jpg"
        ),
        TrackDto(
            trackName = "Розовый фламинго",
            artistName = "Сплин",
            trackTimeMillis = 195000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackName = "Танцевать",
            artistName = "Мельница",
            trackTimeMillis = 222000,
            artworkUrl100 = null
        ),
        TrackDto(
            trackName = "Чёрный бумер",
            artistName = "Серега",
            trackTimeMillis = 241000,
            artworkUrl100 = null
        )
    )

    fun search(request: String): List<TrackDto> {
        // Триггеры для QA: позволяют проверить состояния ошибки и пустого ответа.
        when (request.trim().lowercase()) {
            "error" -> throw IOException("Имитация сетевой ошибки")
            "empty" -> return emptyList()
        }
        return listTracks.filter {
            it.trackName
                .lowercase()
                .contains(request.lowercase())
        }
    }
}
