package ru.cscenter.fingerpaint.ui.setuser

import ru.cscenter.fingerpaint.models.UsersModel

class AddUserFragment : BaseUpdateUserFragment() {
    override suspend fun updateUser(name: String, model: UsersModel) = model.insertUser(name)
}
