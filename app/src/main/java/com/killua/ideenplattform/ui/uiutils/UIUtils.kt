package com.killua.ideenplattform.ui

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections
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

    @BindingAdapter("setCustomAdapter")
    @JvmStatic
    fun setCustomAdapter(view: AppCompatSpinner, currentArray: List<String>){
        if(currentArray.isNotEmpty()){
            val arraList= arrayListOf(view.context.getString(R.string.select_category))
            arraList.addAll(currentArray)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                view.context,
                android.R.layout.simple_spinner_item,
                arraList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            view.adapter = adapter
        }
    }


    @BindingAdapter("imageUrl")
    @JvmStatic
    fun setImageUri(view: ImageView, imageUri: String?) {
        if (imageUri.isNullOrBlank()) {
            view.setImageURI(null)
        } else {
            PicassoFactory.build().load(imageUri).fit().placeholder(com.killua.ideenplattform.R.drawable.placeholder)
                .error(com.killua.ideenplattform.R.drawable.placeholder)
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