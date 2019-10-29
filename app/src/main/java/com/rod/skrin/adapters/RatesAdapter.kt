package com.rod.skrin.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rod.skrin.R
import com.rod.skrin.extensions.inflate
import com.rod.skrin.models.Rate
import com.rod.skrin.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_rates_item.view.*
import java.text.SimpleDateFormat

class RatesAdapter(private val items: List<Rate>): RecyclerView.Adapter<RatesAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(
        R.layout.fragment_rates_item))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        fun bind(rate : Rate) = with(itemView){
            textViewRate.text = ""
            textViewStar.text = rate.rate.toString()
            textViewCalendar.text = SimpleDateFormat("dd,MMM,yyyy").format(rate.createdAt)

            if(rate.profileImgURL.isEmpty()){
                Picasso.get().load(R.drawable.ic_person).resize(100,100)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(imageViewProfile)
            }else {
                Picasso.get().load(rate.profileImgURL).resize(100, 100)
                    .centerCrop()
                    .transform(CircleTransform())
                    .into(imageViewProfile)
            }
        }

    }
}