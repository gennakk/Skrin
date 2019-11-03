package com.rod.skrin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rod.skrin.models.Film
import kotlinx.android.synthetic.main.info_film.*
import kotlinx.android.synthetic.main.info_film.view.*

class FilmInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_film)

        val intent: Intent = getIntent()

        val film: Film = intent.getSerializableExtra("film") as Film

        textViewTitle.text = film.title
        textViewDirector.text = film.director
        ratingBar.rating = (film.avg_vote/10)*5
    }
}
