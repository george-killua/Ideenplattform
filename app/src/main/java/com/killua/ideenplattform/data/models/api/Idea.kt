package com.killua.ideenplattform.data.models.api

data class Idea(
    val id: String,
    //example: 76958aee-bd15-4308-b0eb-717fae97c136
    val author: User,
    val title: String,
    // example: Idea #1
    val category: Category,
    val description: String,
    // example: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi aliquam sed elit in pulvinar. Phasellus iaculis dictum lectus sed euismod. Suspendisse a auctor leo. Pellentesque in orci vehicula, semper felis mattis, imperdiet lorem. Donec quis erat quis ante facilisis finibus. Curabitur ultrices suscipit tellus at iaculis. Mauris hendrerit pharetra purus. Nunc feugiat vulputate ipsum sit amet bibendum. Proin sed varius ex. Morbi semper velit dolor, et lobortis elit finibus ut. Donec a dapibus risus. Cras lectus felis, porttitor et pulvinar quis, finibus eu nisi. Nunc eu mi sed velit ultricies cursus. Quisque dignissim felis non diam aliquet, ut rutrum nulla rutrum.
    val created: String,
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val lastUpdated: String,
    //($yyyy-MM-dd'T'HH:mm:ss.SSSZ)
    val comments: CommentList?=null,
    val imageUrl: String,
//  example: https://ideenmanagement.tailored-apps.com/image/idea/some-url.png
    val rating: IdeaRating
)