package com.killua.ideenplattform.data.requests

class CreateIdeeReq(
    val title: String,
    //  example: Idea #1
    val categoryId: String,
    //example: event
    val description: String
    //  example: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi aliquam sed elit in pulvinar.

)