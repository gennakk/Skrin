package com.rod.skrin.dialogues

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.rod.skrin.R
import com.rod.skrin.extensions.toast
import com.rod.skrin.models.NewRateEvent
import com.rod.skrin.models.Rate
import com.rod.skrin.utils.RxBus
import kotlinx.android.synthetic.main.dialog_rates.view.*
import java.util.*

class RateDialog : DialogFragment(){

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_rates,null)

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_title))
            .setView(view)
            .setPositiveButton(getString(R.string.dialog_ok)){ _, _ ->
                //toast
                activity!!.toast("Pressed OK")
                val textRate = view.editTextRateFeedback.text.toString()
                if(textRate.isNotEmpty()){
                    val imgURL = FirebaseAuth.getInstance().currentUser!!.photoUrl?.toString() ?: run {""}
                    val rate = Rate(textRate,view.ratingBarFeedback.rating, Date(),imgURL)

                    RxBus.publish(NewRateEvent(rate))
                }

            }
            .setNegativeButton(getString(R.string.dialog_cancel)){ _, _ ->
            //toast
                activity!!.toast("Pressed Cancel")
            }
            .create()


    }
}