package com.rod.skrin.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alejandrolora.mylibrary.ToolbarActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query

import com.rod.skrin.R
import com.rod.skrin.adapters.FilmsAdapter
import com.rod.skrin.adapters.RatesAdapter
import com.rod.skrin.dialogues.RateDialog
import com.rod.skrin.extensions.toast
import com.rod.skrin.models.Film
import com.rod.skrin.models.NewRateEvent
import com.rod.skrin.models.Rate
import com.rod.skrin.utils.RxBus
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_rates.view.*
import com.google.firebase.database.DataSnapshot
import com.rod.skrin.activities.LoginActivity
import com.rod.skrin.extensions.gotoActivity
import kotlinx.android.synthetic.main.activity_main.*


class FilmsFragment : Fragment(){

    private lateinit var _view: View

    private lateinit var adapter: FilmsAdapter

    private val filmsList: ArrayList<Film> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    //private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var store = FirebaseDatabase.getInstance()
    //private lateinit var ratesDBRef: CollectionReference
    private lateinit var filmsDBRef: DatabaseReference

    //private var ratesSubsctiption : ListenerRegistration? = null
    private var filmsSubsctiption : ListenerRegistration? = null
    private lateinit var rateBusListener : Disposable

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _view =  inflater.inflate(R.layout.fragment_rates, container, false)
        setHasOptionsMenu(true)
        setUpFilmsDB()
        setUpCurrentUser()

        setUpRecyclerView()
        setUpFab()

        subscribeToFilms(1,10)
        //subscribeToNewRatings()



        return _view
    }

   /* private fun setUpRatesDB(){
        ratesDBRef = store.collection("rates")

    }*/

    private fun setUpFilmsDB(){
        store.setPersistenceEnabled(true)
        filmsDBRef = store.reference



    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!

    }

    private fun setUpRecyclerView(){

        val layoutManager = LinearLayoutManager(context)

        adapter = FilmsAdapter(filmsList)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.setItemViewCacheSize(15)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
        _view.recyclerView.setPadding(0,10,0,0)

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

                if(!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE) {
                    subscribeToFilms(filmsList.size, filmsList.size + 1)
                }
            }
        }

        _view.recyclerView.addOnScrollListener(scrollListener)


    }

    private fun setUpFab(){

        _view.fabRating.setOnClickListener{ RateDialog().show(fragmentManager!!,"")}

    }

 /*   private fun saveRate(rate:Rate){
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
    }*/

   /* private fun subscribeToRatings(){
        ratesSubsctiption = ratesDBRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(object: java.util.EventListener,EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?){
                    exception?:let {
                        activity!!.toast("Exception!!")
                        return
                    }

                    snapshot?.let {

                        filmsList.clear()

                        val films = it.toObjects(Film::class.java)

                        filmsList.addAll(films)
                        adapter.notifyDataSetChanged()
                        _view.recyclerView.smoothScrollToPosition(0)

                    }
                }
            })



    } */

    private fun subscribeToFilms(start:Int,end:Int){
      /*  filmsSubsctiption = filmsDBRef
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener(object: java.util.EventListener,EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?){
                    exception?:let {
                        activity!!.toast("Exception!!")
                        return
                    }

                    snapshot?.let {

                        filmsList.clear()

                        val films = it.toObjects(Film::class.java)

                        filmsList.addAll(films)
                        adapter.notifyDataSetChanged()
                        _view.recyclerView.smoothScrollToPosition(0)

                    }
                }
            }) */


        for (number in start..end) {

            filmsDBRef.child("0").child("films").child(number.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        var ds = snapshot
                        var film: Film? = Film()

                        val title: String? = ds.child("title").value.toString()
                        val director: String? = ds.child("director").value.toString()
                        val avg_vote: String? = ds.child("avg_vote").value.toString()




                        film!!.title = title!!
                        film!!.director = director!!
                        film!!.avg_vote = avg_vote!!.toFloat()


                        filmsList.add(film!!)



                        adapter.notifyDataSetChanged()

                    }


                })

        }





    }

    private fun searchFilms(titleSearch:String){

            filmsList.clear()

            filmsDBRef.child("0").child("films").orderByChild("title").equalTo(titleSearch).limitToFirst(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        Toast.makeText(context,"CHILDREN" +snapshot.childrenCount,Toast.LENGTH_SHORT).show()
                        snapshot.children.forEach {
                            //val ds = snapshot
                            val title: String? = it.child("title").value.toString()

                            val director: String? = it.child("director").value.toString()
                            val avg_vote: String? = it.child("avg_vote").value.toString()

                            var film: Film? = Film()

                            film!!.title = title!!
                            film!!.director = director!!
                            film!!.avg_vote = avg_vote!!.toFloat()


                            filmsList.add(film!!)



                            adapter.notifyDataSetChanged()
                        }


                    }


                })




    }
/*
    private fun subscribeToNewRatings(){
        rateBusListener = RxBus.listen(NewRateEvent::class.java).subscribe({
            saveRate(it.rate)
        })
    } */

     override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

         val searchItem = menu.findItem(R.id.app_bar_search)
         val searchView = searchItem.actionView as SearchView
         searchView.setQueryHint("Search View Hint")

         searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

             override fun onQueryTextChange(newText: String): Boolean {


                 return false
             }

             override fun onQueryTextSubmit(query: String): Boolean {
                 // task HERE
                 Toast.makeText(context,"CLICK",Toast.LENGTH_SHORT).show()
                 searchFilms(query)
                 searchView.clearFocus()
                 return false
             }

         })

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.app_bar_search).isVisible = true



    }
    override fun onDestroyView() {
        _view.recyclerView.removeOnScrollListener(scrollListener)
        rateBusListener.dispose()
        //ratesSubsctiption?.remove()


        super.onDestroyView()
    }


}
