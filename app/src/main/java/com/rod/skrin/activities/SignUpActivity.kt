package com.rod.skrin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.rod.skrin.extensions.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {


    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance()}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.rod.skrin.R.layout.activity_sign_up)

        buttonGoLogIn.setOnClickListener {
            gotoActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)

        }

        buttonSignUp.setOnClickListener {
            if (isvalidEmail(editTextEmail.text.toString())
                && isvalidPassword(editTextPassword.text.toString())
                && isvalidConfirmPassword(editTextPassword.text.toString(),editTextConfirmPassword.text.toString())){

                signUpByEmail(editTextEmail.text.toString(),editTextPassword.text.toString())
            }else{
                toast("Plrease make sure all data is correct")
            }
        }

        editTextEmail.validate{
            editTextEmail.error = if(isvalidEmail(it)) null else "The email is not valid"
        }

        editTextPassword.validate{
            editTextEmail.error = if(isvalidPassword(it)) null else "The password is not valid"
        }

        editTextConfirmPassword.validate{
            editTextEmail.error = if(isvalidConfirmPassword(editTextPassword.text.toString(),it)) null else "The password is not valid"
        }
    }



    override fun onConnectionFailed(p0: ConnectionResult) {



    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth?.getCurrentUser()
        if (currentUser != null ){
            toast("User is not logged in")
            createAccount("rodrigominola@gmial.com","123456789")

        }else{
            toast("User is logged in")

        }

    }

    private fun createAccount(email : String,password : String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"Create user with email :success",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = mAuth.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "reate user with email :failure",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
    }

    private fun signUpByEmail(email : String,password : String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                        toast("An email has been sent to you. Please confirm before sign in.")
                        gotoActivity<LoginActivity> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    }



                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "An unexpected error ocurred, please try again.",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
    }

    private fun isValidEmainAndPassword():Boolean{
        return !editTextEmail.text.isNullOrEmpty()
                && !editTextEmail.text.isNullOrEmpty()
                && !editTextConfirmPassword.text.isNullOrEmpty()
                && editTextConfirmPassword.text.toString() == editTextPassword.text.toString()
    }



}
