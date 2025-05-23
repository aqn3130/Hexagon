package com.hexagon.ddt.http.user

import com.hexagon.ddt.BaseTest
import com.hexagon.ddt.http.interactors.HttpUserInteractor
import com.hexagon.domain.models.Name
import com.hexagon.domain.models.User
import com.hexagon.handlebars.views.UserDetailViewModel
import com.natpryce.hamkrest.assertion.assertThat
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class ViewUserUseCaseTests : BaseTest() {

    @Test
    fun `given user account exists, user detail is returned`(){
        //given
        val userId = "5339c4c2-c42d-4290-a1eb-6a331499f5dd"
        val userName = Name("John", "Paul")
        val aUserAccount = User(userId, userName)

        //TODO: add route to get user by name and then complete this test

//        val mockUserViewModel = mockk<UserDetailViewModel>()

//        val createUserRequest = Request(Method.POST, "user?firstname=${userName.getFirstName()}&&lastname=${userName.getLastName()}")
//        app(createUserRequest)

        //when
//        every { mockUserViewModel.getUserViewModel() } returns aUserAccount
//        val getUserRequest = Request(Method.GET, "/user/${userId}")
//        val response = app(getUserRequest)
//
//        assertThat(response, hasStatus(Status.OK))

        HttpUserInteractor().`sees user first name`(userId, userName.getName())

    }
}