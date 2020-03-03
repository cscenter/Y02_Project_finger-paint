package ru.cscenter.fingerpaint.synchronization

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.cscenter.fingerpaint.api.ApiNewPatientStatistics
import ru.cscenter.fingerpaint.api.ApiPatient
import ru.cscenter.fingerpaint.db.DbController
import ru.cscenter.fingerpaint.db.User
import ru.cscenter.fingerpaint.db.UserStatus
import ru.cscenter.fingerpaint.network.*

class RequestsCache(private val api: FingerPaintApi, private val db: DbController) {

    fun syncCache(onResult: ResultHandler) {
        GlobalScope.launch(Dispatchers.IO) {
            val users = db.selectUsers()
            syncRenamedUsers(users.filter { it.status == UserStatus.UPDATED }) { renameSuccess ->
                if (renameSuccess) {
                    syncNewUsers(users.filter { it.status == UserStatus.NEW }) { newUsersSuccess ->
                        if (newUsersSuccess) syncStatistics(onResult)
                        else onResult(false)
                    }
                } else {
                    onResult(false)
                }
            }
        }
    }

    private fun syncRenamedUsers(renamedUsers: List<User>, onResult: ResultHandler) {
        val data = renamedUsers.map { ApiPatient(it.id.toLong(), it.name) }
        api.updatePatients(data).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.setUsers(renamedUsers.map {
                    it.status = UserStatus.SYNCHRONIZED
                    it
                })
                onResult(true)
            }
        }, { onResult(!codeIsError(it)) })
    }

    private fun syncNewUsers(newUsers: List<User>, onResult: ResultHandler) =
        GlobalScope.launch(Dispatchers.IO) {
            val data = newUsers.map {
                ApiNewPatientStatistics(it.name,
                    db.selectUserAllStatistics(it.id).map { s -> statisticToApi(s) }
                )
            }

            api.addOfflinePatients(data).executeAsync({
                GlobalScope.launch(Dispatchers.IO) {
                    db.setUsers(newUsers.map {
                        it.status = UserStatus.SYNCHRONIZED
                        it
                    })
                    onResult(true)
                }
            }, { onResult(false) })
        }

    private fun syncStatistics(onResult: ResultHandler) = GlobalScope.launch(Dispatchers.IO) {
        val cache = db.selectCachedResults().map { cachedResultToApiResult(it) }
        api.putStatistics(cache).executeAsync({
            GlobalScope.launch(Dispatchers.IO) {
                db.clearCache()
                onResult(true)
            }
        }, { onResult(!codeIsError(it)) })
    }
}