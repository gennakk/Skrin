package com.rod.skrin.extensions

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import java.util.regex.Pattern

fun Activity.toast(message:CharSequence,duration : Int = Toast.LENGTH_SHORT) = Toast.makeText(this,message,duration)

fun Activity.toast(resourceId:Int,duration : Int = Toast.LENGTH_SHORT) = Toast.makeText(this,resourceId,duration)

fun ViewGroup.inflate(layoutId:Int) = LayoutInflater.from(context).inflate(layoutId,this,false)!!

inline fun <reified T : Activity> Activity.gotoActivity(noinline init: Intent.() -> Unit = {}){
    val intent = Intent(this, T::class.java)
    intent.init()
    startActivity(intent)
}

fun EditText.validate(validation : (String)-> Unit) {
    this.addTextChangedListener(object:TextWatcher{
        override fun afterTextChanged(editable: Editable?) {

            validation(editable.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    })
    }

fun Activity.isvalidEmail(email:String):Boolean {
    val pattern = Patterns.EMAIL_ADDRESS

    return pattern.matcher(email).matches()

}

fun Activity.isvalidPassword(password:String):Boolean {
    val passwordPattern = "(?=^.{8,}\$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*\$\n"
    val pattern = Pattern.compile(passwordPattern)

    return pattern.matcher(password).matches()

}
fun Activity.isvalidConfirmPassword(password:String,confirmPassword:String):Boolean {
    return password==confirmPassword

}