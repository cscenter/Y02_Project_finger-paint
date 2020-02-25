package ru.cscenter.fingerpaint.authentication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import ru.cscenter.fingerpaint.R


class LoginActivity : AppCompatActivity() {

    private lateinit var authController: AuthenticateController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authController = AuthenticateController(this)
        sign_in_button.setOnClickListener {
            authController.login()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authController.onActivityResultSignIn(requestCode, data) { success ->
            if (success) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
