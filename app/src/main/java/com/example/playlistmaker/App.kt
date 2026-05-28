package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.ui.search.Creator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализируем DI-провайдер app-контекстом: тут он строит Room-базу
        // и DataStore-инстанс, которые потом раздаёт во все ViewModel-ы.
        Creator.init(this)
    }
}
