package ru.cscenter.fingerpaint.ui.title

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.MainActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.authentication.AuthenticateController
import ru.cscenter.fingerpaint.authentication.LoginActivity
import ru.cscenter.fingerpaint.models.CurrentUserModel

class TitleActivity : AppCompatActivity() {

    private fun onLoaded() {
        MainApplication.synchronizeController.syncAll()
        GlobalScope.launch(Dispatchers.Main) {
            val currentUserModel: CurrentUserModel by viewModels()
            if (currentUserModel.hasCurrentUser()) {
                toActivity(this@TitleActivity, MainActivity::class.java)
            } else {
                toActivity(this@TitleActivity, TitleChooseUserActivity::class.java)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOGIN_CODE) {
            if (resultCode == RESULT_OK) {
                onLoaded()
            } else {
                login()
            }
        }
    }

    private fun login() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivityForResult(intent, LOGIN_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
        MainApplication.synchronizeController.setActivity(this)

        val authController = AuthenticateController(this)

        authController.silentLogin { success ->
            if (success) {
                onLoaded()
            } else {
                login()
            }
        }
    }

    companion object {
        private const val LOGIN_CODE = 43
    }
}

fun <T : Activity> toActivity(fromActivity: Activity, toActivityClass: Class<T>) {
    val intent = Intent(fromActivity, toActivityClass)
    fromActivity.startActivity(intent)
    fromActivity.finish()
}
