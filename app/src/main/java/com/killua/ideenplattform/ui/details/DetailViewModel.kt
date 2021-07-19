package com.killua.ideenplattform.ui.details

import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso

class DetailViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    fun gh() {
        Picasso.get().setIndicatorsEnabled(true)
    }
}