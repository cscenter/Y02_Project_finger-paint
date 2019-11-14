package ru.cscenter.fingerpaint

import android.app.Application

class MainApplication : Application() {
    companion object {
        var isLoading = true
        var hasCurrentUser = false
    }
}
