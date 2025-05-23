package com.hexagon.ddt.http.inmemory.interactors

interface UserInteractor {
    fun `sees user first name`(userId: String, userName: String)
}
