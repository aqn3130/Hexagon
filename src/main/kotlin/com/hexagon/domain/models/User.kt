package com.hexagon.domain.models

data class User(val id: String, val name: Name)

class Name(private val firstName: String, private val lastName: String) {
    fun getName() = "$firstName $lastName"
    fun nameLength() = getName().length
    fun getFirstName() = firstName
    fun getLastName() = lastName
}
