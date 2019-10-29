package com.rod.skrin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.rod.skrin.R
import com.rod.skrin.extensions.gotoActivity
import com.rod.skrin.extensions.isvalidEmail
import com.rod.skrin.extensions.toast
import com.rod.skrin.extensions.validate
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.editTextEmail
import kotlinx.android.synthetic.main.activity_login.*


class ForgotPasswordActivity : AppCompatActivity() {

    private val mAuth : FirebaseAuth by lazy{ FirebaseAuth.getInstance()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        editTextEmail.validate {
            editTextEmail.error = if(isvalidEmail(it)) null else "Email is not Valid"
        }

        buttonLogIn.setOnClickListener {
            gotoActivity<LoginActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        buttonForgot.setOnClickListener {
            val email = editTextEmail.text.toString()
            if(isvalidEmail(email)){
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                    toast("Email has been sent to reset your password.")
                    gotoActivity<LoginActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                }

            }else{

                toast("Please make sure the email address is correct.")
            }
        }
    }
}
