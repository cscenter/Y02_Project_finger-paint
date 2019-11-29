package ru.cscenter.fingerpaint.ui.title

import android.os.Bundle
import ru.cscenter.fingerpaint.ActivitySetUserListenerContainer
import ru.cscenter.fingerpaint.R

class TitleChooseUserActivity : ActivitySetUserListenerContainer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title_choose_user)
    }
}
