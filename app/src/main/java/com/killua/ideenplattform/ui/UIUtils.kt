package com.killua.ideenplattform.ui

import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.databinding.BindingAdapter
import com.killua.ideenplattform.R
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.squareup.picasso.Callback
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object PicassoFactory : KoinComponent {
    fun build(): Picasso {
        val client: OkHttpClient by inject()
        val picasso = Picasso.Builder(MyApplication.instance).downloader(OkHttp3Downloader(client))
        picasso.indicatorsEnabled(true)
        Picasso.setSingletonInstance(picasso.build())
        return picasso.build()
    }
}

object DataBindingAdapters {
    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUri(view: ImageView, imageUri: String?) {
        if (imageUri.isNullOrBlank()) {
            view.setImageURI(null)
        } else {
            PicassoFactory.build().load(imageUri).fit().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)
                .into(view, object : Callback {
                    override fun onSuccess() {
                        Log.d("PICASSO", "It should have loaded...")
                    }
                    override fun onError(e: Exception) {
                        Log.e("PICASSO", e.toString())
                    }
                })
        }
    }
}