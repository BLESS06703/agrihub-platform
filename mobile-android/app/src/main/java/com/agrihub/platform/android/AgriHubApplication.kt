package com.agrihub.platform.android

import android.app.Application
import com.agrihub.platform.android.data.repository.AgriHubRepository
import com.agrihub.platform.android.ui.theme.AgriHubTheme

class AgriHubApplication : Application() {
    lateinit var repository: AgriHubRepository
        private set
    
    override fun onCreate() {
        super.onCreate()
        repository = AgriHubRepository()
    }
}
