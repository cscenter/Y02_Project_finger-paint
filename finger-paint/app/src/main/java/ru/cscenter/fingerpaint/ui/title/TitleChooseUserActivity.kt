package ru.cscenter.fingerpaint.ui.title

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R

class TitleChooseUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_choose_user)
        MainApplication.synchronizeController.setActivity(this)
    }
}
