package ru.cscenter.fingerpaint.authentication

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import ru.cscenter.fingerpaint.MainApplication
import ru.cscenter.fingerpaint.R
import ru.cscenter.fingerpaint.synchronization.ResultHandler
import ru.cscenter.fingerpaint.ui.title.TitleActivity
import ru.cscenter.fingerpaint.ui.title.toActivity


class AuthenticateController(private val activity: Activity) {

    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(activity.getString(R.string.server_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun logout(onResult: ResultHandler) {
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true)
                toActivity(activity, TitleActivity::class.java)
            } else {
                onResult(false)
            }
        }
    }

    fun silentLogin(onResult: ResultHandler) {
        googleSignInClient.silentSignIn().addOnSuccessListener(activity) { account ->
            onAccountUpdated(account)
            onResult(true)
        }.addOnFailureListener {
            onResult(false)
        }
    }

    fun login() {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, SIGN_IN_CODE)
    }

    fun onActivityResultSignIn(requestCode: Int, data: Intent?, onResult: ResultHandler) {
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                onAccountUpdated(account)
                onResult(true)
            } catch (e: ApiException) {
                Log.e("FingerPaint", "signInResult:failed code=" + e.statusCode)
                onResult(false)
            }
        }
    }

    private fun onAccountUpdated(account: GoogleSignInAccount?) {
        val idToken = account?.idToken
        if (idToken != null) {
            MainApplication.synchronizeController.setIdToken(idToken)
        }
    }

    companion object {
        const val SIGN_IN_CODE = 42
    }
}
