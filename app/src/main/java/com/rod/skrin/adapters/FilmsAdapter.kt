package com.rod.skrin.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.rod.skrin.FilmInfoActivity
import com.rod.skrin.R
import com.rod.skrin.extensions.inflate
import com.rod.skrin.fragments.FilmsFragment
import com.rod.skrin.models.Film

import com.rod.skrin.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.info_film.view.*
//import kotlinx.android.synthetic.main.fragment_films_item.view.*
import java.text.SimpleDateFormat

class FilmsAdapter(private val items: List<Film>): RecyclerView.Adapter<FilmsAdapter.ViewHolder>(){

    var context : Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder  {
        context = parent.context

        //return ViewHolder(parent.inflate(R.layout.fragment_films_item))
        return ViewHolder(parent.inflate(R.layout.info_film_short))
    }

    override fun getItemCount(): Int = items.size


    override fun onBindViewHolder(holder: FilmsAdapter.ViewHolder, position: Int) {
        val film = items[position]
        holder.bind(items[position])
        holder.itemView.setOnClickListener{
            val intent_info_film =  Intent(context, FilmInfoActivity::class.java).apply {
                putExtra("film",items[position] as Film)
            }

            startActivity(context!!,intent_info_film,null)
        }
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bind(film : Film) = with(itemView){

            textViewTitle.text = film.title




/*
            Picasso.get().load(R.drawable.ic_person).resize(100,100)
                .centerCrop()
                .transform(CircleTransform())
                .into(imageViewFriend)

            textViewUsuarioFriend.text = film.title

            Picasso.get().load(R.drawable.ic_person).resize(100,100)
                .centerCrop()
                .transform(CircleTransform())
                .into(imageViewPelicula)


            if(film.profileImgURL.isEmpty()){
                Picasso.get().load(R.drawable.ic_person).resize(100,100)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(imageViewUser)
            }else {
                Picasso.get().load(film.profileImgURL).resize(100, 100)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(imageViewUser)
            }
        */
        }


    }
}


