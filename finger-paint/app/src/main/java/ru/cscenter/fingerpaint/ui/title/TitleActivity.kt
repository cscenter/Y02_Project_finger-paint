package ru.cscenter.fingerpaint.ui.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.cscenter.fingerpaint.MainActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.models.CurrentUserModel

class TitleActivity : AppCompatActivity() {

    private val onLoaded = {
        val currentUserModel: CurrentUserModel by viewModels()
        currentUserModel.currentUser.observe(this, Observer { currentUser ->
            if (currentUser == null) {
                val intent = Intent(this, TitleChooseUserActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                toMainActivity(this)
            }
        })
    }

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
    }

    override fun onResume() {
        super.onResume()
        if (MainApplication.isLoading) {
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
