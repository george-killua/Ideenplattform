package com.killua.ideenplattform.data.models.api

import android.icu.text.DateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import com.killua.ideenplattform.data.models.local.CategoryCaching
import com.killua.ideenplattform.data.models.local.UserCaching
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

data class Idea(
    val id: String = "",
    //example: 76958aee-bd15-4308-b0eb-717fae97c136
    val author: UserCaching,
    val title: String = "",
    // example: Idea #1
    val released : Boolean,
    val category: CategoryCaching ,
    val description: String = "",
    // example: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi aliquam sed elit in pulvinar. Phasellus iaculis dictum lectus sed euismod. Suspendisse a auctor leo. Pellentesque in orci vehicula, semper felis mattis, imperdiet lorem. Donec quis erat quis ante facilisis finibus. Curabitur ultrices suscipit tellus at iaculis. Mauris hendrerit pharetra purus. Nunc feugiat vulputate ipsum sit amet bibendum. Proin sed varius ex. Morbi semper velit dolor, et lobortis elit finibus ut. Donec a dapibus risus. Cras lectus felis, porttitor et pulvinar quis, finibus eu nisi. Nunc eu mi sed velit ultricies cursus. Quisque dignissim felis non diam aliquet, ut rutrum nulla rutrum.
    val created: String = "",
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val lastUpdated: String = "",
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val comments: List<IdeaComment> = listOf(),
    val imageUrl: String = "",
    val ratings: List<IdeaRating> = listOf()
//  example: https://ideenmanagement.tailored-apps.com/image/idea/some-url.png
) {

    fun compareWithCreatedDate(other: Idea?): Int {
        val dateFormat =  DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
      return  LocalDateTime.parse(created,dateFormat).compareTo(LocalDateTime.parse(other?.created,dateFormat));

    }

    private val arrayRating: Int by lazy {
        val tempArray = arrayListOf(0)
        ratings.forEach {
            tempArray.add(it.rating)
        }
        tempArray.average().toInt()
    }
    val average = if (ratings.isEmpty()) {
        "not Rated yet"
    } else {
        "average $arrayRating"
    }
    val imageNull = imageUrl.isBlank()

 fun date(){
     val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
     val parsed= LocalDate.parse(created,formatter)

     val df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
     val str1: String = df1.format(parsed)
     println(str1)
     println(parsed)
 }
init {
    date()
}
}


