package com.rod.skrin.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

import com.rod.skrin.R
import com.rod.skrin.adapters.RatesAdapter
import com.rod.skrin.dialogues.RateDialog
import com.rod.skrin.extensions.toast
import com.rod.skrin.models.NewRateEvent
import com.rod.skrin.models.Rate
import com.rod.skrin.utils.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rates.view.*


class RatesFragment : Fragment() {

    private lateinit var _view: View

    private lateinit var adapter: RatesAdapter

    private val ratesList: ArrayList<Rate> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var ratesDBRef: CollectionReference

    private var ratesSubsctiption : ListenerRegistration? = null

    private lateinit var rateBusListener : Disposable

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _view =  inflater.inflate(R.layout.fragment_rates, container, false)

        setUpRatesDB()
        setUpCurrentUser()

        setUpRecyclerView()
        setUpFab()

        subscribeToRatings()
        subscribeToNewRatings()


        return _view
    }

    private fun setUpRatesDB(){
        ratesDBRef = store.collection("rates")

    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!

    }

    private fun setUpRecyclerView(){

        val layoutManager = LinearLayoutManager(context)

        adapter = RatesAdapter(ratesList)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter

        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if(dy > 0 || dy < 0 && _view.fabRating.isShown){
                    _view.fabRating.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    _view.fabRating.show()
                }

            }
        }

        _view.recyclerView.addOnScrollListener(scrollListener)

    }

    private fun setUpFab(){

        _view.fabRating.setOnClickListener{ RateDialog().show(fragmentManager!!,"")}

    }

    private fun saveRate(rate:Rate){
        val newRating = HashMap<String,Any>()
        newRating["text"] = rate.text
        newRating["rate"] = rate.rate
        newRating["createdAt"] = rate.createdAt
        newRating["profileImgURL"] = rate.profileImgURL

        ratesDBRef.add(newRating)
            .addOnCompleteListener{
            activity!!.toast("Rating added!!")
            }
            .addOnFailureListener{
                activity!!.toast("Rating error, try again!!")
            }
    }

    private fun subscribeToRatings(){
        ratesSubsctiption = ratesDBRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(object: java.util.EventListener,EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?){
                    exception?:let {
                        activity!!.toast("Exception!!")
                        return
                    }

                    snapshot?.let {

                        ratesList.clear()

                        val rates = it.toObjects(Rate::class.java)

                        ratesList.addAll(rates)
                        adapter.notifyDataSetChanged()
                        _view.recyclerView.smoothScrollToPosition(0)

                    }
                }
            })



    }

    private fun subscribeToNewRatings(){
        rateBusListener = RxBus.listen(NewRateEvent::class.java).subscribe({
            saveRate(it.rate)
        })
    }

    override fun onDestroyView() {
        _view.recyclerView.removeOnScrollListener(scrollListener)
        rateBusListener.dispose()
        ratesSubsctiption?.remove()


        super.onDestroyView()
    }


}
