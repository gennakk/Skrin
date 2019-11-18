package com.rod.skrin.models

import android.graphics.drawable.Drawable
import android.os.Parcelable
import java.io.Serializable


data class Film (
    var title: String = "",
    var avg_vote: Float = 0f,
    var year: Int = 0,
    var director: String = "",
    var actors: String = "",
    var genre: String = "",
    var duration: Int = 0,
    var description: String = "",
    var profileImgURL: String = "",
    var imagen : Drawable? = null
): Serializable