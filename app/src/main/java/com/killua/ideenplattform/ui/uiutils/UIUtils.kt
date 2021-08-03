package com.killua.ideenplattform.ui

import android.net.Uri
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.killua.ideenplattform.R
import com.killua.ideenplattform.applicationmanager.MyApplication
import com.killua.ideenplattform.ui.home.CategoryAdapter
import com.killua.ideenplattform.ui.home.IdeasAdapter
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


object PicassoFactory : KoinComponent {
    fun build(): Picasso {
        val client: OkHttpClient by inject()
        val picasso = Picasso.Builder(MyApplication.instance).downloader(OkHttp3Downloader(client))
        picasso.loggingEnabled(true)
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
    fun setCustomAdapter(view: AppCompatSpinner, currentArray: List<String>) {
        if (currentArray.isNotEmpty()) {
            val arraList = arrayListOf(view.context.getString(R.string.select_category))
            arraList.addAll(currentArray)
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
                view.context,
                android.R.layout.simple_spinner_item,
                arraList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            view.adapter = adapter
        }
    }

    @BindingAdapter("imagePath")
    @JvmStatic
    fun ImageView.setImagePath(imageUri: String?) {
        if (imageUri.isNullOrBlank()) {
            setImageURI(null)
        } else {
            val uri = Uri.parse(imageUri)
            PicassoFactory
                .build()
                .load(uri)
                .fit()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(this)
        }
    }

    @BindingAdapter("setupIdeaAdapter")
    @JvmStatic
    fun RecyclerView.setupIdeaAdapter(adapter: IdeasAdapter?) {
        if (adapter != null) {
        this.adapter=adapter
            this.layoutManager=LinearLayoutManager(this.context)
        }
    }
    @BindingAdapter("setupCategoriesAdapter")
    @JvmStatic
    fun RecyclerView.setupCategoriesAdapter(adapter: CategoryAdapter?) {
        if (adapter != null) {
        this.adapter=adapter
            this.layoutManager= LinearLayoutManager(
                this.context,
                LinearLayoutManager.HORIZONTAL,
                false);
        }
    }
}