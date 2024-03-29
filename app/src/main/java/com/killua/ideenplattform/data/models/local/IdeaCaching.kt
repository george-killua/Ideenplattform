package com.killua.ideenplattform.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.killua.ideenplattform.data.models.api.Idea
import com.killua.ideenplattform.data.repository.MainRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

@Entity(tableName = "idea_table")
data class IdeaCaching(
    @PrimaryKey(autoGenerate = false)
    val ideaCachingId: String = "",
    //example: 76958aee-bd15-4308-b0eb-717fae97c136
    val authorId: String,
    val title: String = "",
    // example: Idea #1
    val categoryId: String,
    val description: String = "",
    val released: Boolean,
    // example: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi aliquam sed elit in pulvinar. Phasellus iaculis dictum lectus sed euismod. Suspendisse a auctor leo. Pellentesque in orci vehicula, semper felis mattis, imperdiet lorem. Donec quis erat quis ante facilisis finibus. Curabitur ultrices suscipit tellus at iaculis. Mauris hendrerit pharetra purus. Nunc feugiat vulputate ipsum sit amet bibendum. Proin sed varius ex. Morbi semper velit dolor, et lobortis elit finibus ut. Donec a dapibus risus. Cras lectus felis, porttitor et pulvinar quis, finibus eu nisi. Nunc eu mi sed velit ultricies cursus. Quisque dignissim felis non diam aliquet, ut rutrum nulla rutrum.
    val created: String = "",
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val lastUpdated: String = "",
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val imageUrl: String = "",
//  example: https://ideenmanagement.tailored-apps.com/image/idea/some-url.png

) {
    companion object {
        fun transmitIdea(
            ideaCaching: IdeaCaching,
            repository: MainRepository
        ): Idea {
            var userCaching = UserCaching()
            var categoryCaching = CategoryCaching()
            runBlocking {
                repository.getUserId(ideaCaching.authorId).collect { userCaching = it.data!! }
                repository.getCategoryWithId(ideaCaching.categoryId)
                    .collect { categoryCaching = it.data!! }
            }
            return Idea(
                id = ideaCaching.ideaCachingId,
                title = ideaCaching.title,
                author = userCaching,
                released = ideaCaching.released,
                category = categoryCaching,
                description = ideaCaching.description,
                created = ideaCaching.created,
                lastUpdated = ideaCaching.lastUpdated,
                ratings = listOf(),
                imageUrl = ideaCaching.imageUrl,
                comments = listOf()
            )
        }
    }
}
