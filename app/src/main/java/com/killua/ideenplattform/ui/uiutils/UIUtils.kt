package com.killua.ideenplattform.ui

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
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
        if (Picasso.get() == null) Picasso.setSingletonInstance(picasso.build())
        return picasso.build()
    }
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }

}

object DataBindingAdapters {


    @BindingAdapter("errorSetter")
    @JvmStatic
    fun setEtError(textInputLayout: TextInputLayout, errorMessage: String?) {
        if (errorMessage.isNullOrBlank()) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = errorMessage

        }
    }

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUri(view: ImageView, imageUri: String?) {
        if (imageUri.isNullOrBlank()) {
            view.setImageURI(null)
        } else {
            PicassoFactory.build().load(imageUri).fit().placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
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