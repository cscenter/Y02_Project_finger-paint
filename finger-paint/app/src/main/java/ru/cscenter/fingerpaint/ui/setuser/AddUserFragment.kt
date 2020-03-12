package ru.cscenter.fingerpaint.ui.setuser

import ru.cscenter.fingerpaint.MainApplication

class AddUserFragment : BaseUpdateUserFragment() {
    override fun updateUser(name: String, onResult: (Boolean) -> Unit) {
        MainApplication.synchronizeController.addUser(name, onResult)
    }
}
