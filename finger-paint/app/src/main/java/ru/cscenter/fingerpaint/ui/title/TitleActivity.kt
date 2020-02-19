package ru.cscenter.fingerpaint.ui.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.models.CurrentUserModel

class TitleActivity : AppCompatActivity() {

    private val onLoaded: () -> Unit = {
        GlobalScope.launch(Dispatchers.Main) {
            val currentUserModel: CurrentUserModel by viewModels()
            if (currentUserModel.hasCurrentUser()) {
                toMainActivity(this@TitleActivity)
            } else {
                MainApplication.isLoading = false
                val intent = Intent(this@TitleActivity, TitleChooseUserActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
    }

    override fun onResume() {
        super.onResume()
        if (MainApplication.isLoading) {
            MainApplication.synchronizeController.syncAll()
            handler.postDelayed(onLoaded, resources.getInteger(R.integer.loading_time).toLong())
        } else {
            toMainActivity(this)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }
}

fun toMainActivity(activity: Activity) {
    MainApplication.isLoading = false
    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}

fun toTitleChooseUserActivity(activity: Activity) {
    MainApplication.isLoading = true
    val intent = Intent(activity, TitleChooseUserActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}
