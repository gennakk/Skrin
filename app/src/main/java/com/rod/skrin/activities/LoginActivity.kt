package com.rod.skrin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rod.skrin.MainActivity
import com.rod.skrin.R
import com.rod.skrin.extensions.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private val mAuth : FirebaseAuth by lazy{ FirebaseAuth.getInstance()}
    private val mGoogleApiClient: GoogleApiClient by lazy{getGoogleApiClient()}
    private val RC_GOOGLE_SIGN_IN = 99


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        buttonLogIn.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (isvalidEmail(email)
                && isvalidPassword(password)){
                logInByEmail(email,password)
            }else{
                toast("Plrease make sure all data is correct")
            }

        }

        textViewForgotPassword.setOnClickListener{
            gotoActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }
        buttonCreateAccount.setOnClickListener {
            gotoActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        buttonLogInGoogle.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent,RC_GOOGLE_SIGN_IN)
        }

        editTextEmail.validate{
            editTextEmail.error = if(isvalidEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate{
            editTextEmail.error = if(isvalidPassword(it)) null else "The password is not valid"
        }

    }

    private fun logInByEmail(email:String,password:String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){task ->
            if(task.isSuccessful){
                if(mAuth.currentUser!!.isEmailVerified) {
                    gotoActivity<MainActivity>{
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }else{
                    toast("User must confirm email")

                }

            }else{
                toast("An unexpected error occurred, please try again.")
            }
        }
    }

    private fun loginByGoogleAccountIntoFirebase(googleAccount : GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(googleAccount.idToken,null)
        mAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (mGoogleApiClient.isConnected) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            }
            gotoActivity<MainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    private fun getGoogleApiClient():GoogleApiClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleApiClient.Builder(this)
            .enableAutoManage(this,this)
            .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
            .build()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_GOOGLE_SIGN_IN){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

            if (result.isSuccess){
                val account = result.signInAccount

                loginByGoogleAccountIntoFirebase(account!!)

            }
        }
    }
}
