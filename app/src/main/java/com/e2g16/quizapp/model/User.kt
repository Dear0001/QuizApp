package com.e2g16.quizapp.model

class User {
    var name = ""
    var age = 0
    var email = ""
    var password = ""

    constructor()
    constructor(name: String, age: Int, email: String, password: String) {
        this.name = name
        this.age = age
        this.email = email
        this.password = password
    }

}