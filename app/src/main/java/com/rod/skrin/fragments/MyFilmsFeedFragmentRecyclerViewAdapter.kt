package com.rod.skrin.fragments

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.common.io.Resources
import com.rod.skrin.R


import com.rod.skrin.fragments.FilmsFeedFragmentFragment.OnListFragmentInteractionListener
import com.rod.skrin.fragments.dummy.DummyContent.DummyItem
import com.rod.skrin.models.Film

import kotlinx.android.synthetic.main.fragment_filmsfeedfragment.view.*
import org.intellij.lang.annotations.Identifier

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyFilmsFeedFragmentRecyclerViewAdapter(
    private val mValues: List<Film>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyFilmsFeedFragmentRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {

        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Film
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_filmsfeedfragment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val view = holder.mView

        view.imageViewPelicula.setImageDrawable(item.imagen)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {



    }
}
