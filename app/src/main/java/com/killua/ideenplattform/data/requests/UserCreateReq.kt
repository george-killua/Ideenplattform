package com.killua.ideenplattform.data.requests

class UserCreateReq(
    val email: String,
    //example: max.mustermann@example.org
    val firstname: String,
    //example: Max
    val lastname: String,
    //example: Mustermann
)