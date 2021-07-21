package com.killua.ideenplattform.data.models.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.killua.ideenplattform.data.models.api.Idea

@Entity(tableName = "idea_table")
data class IdeaCaching(
    @PrimaryKey(autoGenerate = false)
    val ideaCachingId: String = "",
    //example: 76958aee-bd15-4308-b0eb-717fae97c136
    @Embedded
    val author: UserCaching,
    val title: String,
    // example: Idea #1
    @Embedded
    val category: CategoryCaching,
    val description: String,
    // example: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi aliquam sed elit in pulvinar. Phasellus iaculis dictum lectus sed euismod. Suspendisse a auctor leo. Pellentesque in orci vehicula, semper felis mattis, imperdiet lorem. Donec quis erat quis ante facilisis finibus. Curabitur ultrices suscipit tellus at iaculis. Mauris hendrerit pharetra purus. Nunc feugiat vulputate ipsum sit amet bibendum. Proin sed varius ex. Morbi semper velit dolor, et lobortis elit finibus ut. Donec a dapibus risus. Cras lectus felis, porttitor et pulvinar quis, finibus eu nisi. Nunc eu mi sed velit ultricies cursus. Quisque dignissim felis non diam aliquet, ut rutrum nulla rutrum.
    val created: String,
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val lastUpdated: String,
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val imageUrl: String,
//  example: https://ideenmanagement.tailored-apps.com/image/idea/some-url.png
    @Embedded
    val rating: Array<IdeaRatingCaching>
) {
    companion object {
        fun arrayFromIdeas(list: List<Idea>): ArrayList<IdeaCaching> {
            val cachesArray: ArrayList<IdeaCaching> = arrayListOf()
            list.forEach { idea ->
                cachesArray.add(
                    IdeaCaching(
                        ideaCachingId = idea.id,
                        author = UserCaching.fromUser(idea.author)!!,
                        title = idea.title,
                        category = CategoryCaching.fromCategory(idea.category)!!,
                        description = idea.description,
                        created = idea.created,
                        lastUpdated = idea.lastUpdated,
                        imageUrl = idea.imageUrl,
                        rating = IdeaRatingCaching.arrayFromIdeaRatings(idea.rating)
                    )
                )
            }
            return cachesArray
        }

        fun fromIdea(idea: Idea) = IdeaCaching(
            ideaCachingId = idea.id,
            author = UserCaching.fromUser(idea.author)!!,
            title = idea.title,
            category = CategoryCaching.fromCategory(idea.category)!!,
            description = idea.description,
            created = idea.created,
            lastUpdated = idea.lastUpdated,
            imageUrl = idea.imageUrl,
            rating = IdeaRatingCaching.arrayFromIdeaRatings(idea.rating)
        )

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdeaCaching

        if (ideaCachingId != other.ideaCachingId) return false
        if (author != other.author) return false
        if (title != other.title) return false
        if (category != other.category) return false
        if (description != other.description) return false
        if (created != other.created) return false
        if (lastUpdated != other.lastUpdated) return false
        if (imageUrl != other.imageUrl) return false
        if (!rating.contentEquals(other.rating)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ideaCachingId.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + lastUpdated.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + rating.contentHashCode()
        return result
    }
}