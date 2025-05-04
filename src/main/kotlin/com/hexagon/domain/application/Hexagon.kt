package com.hexagon.domain.application

import com.natpryce.krouton.http4k.ResourceRouter
import com.natpryce.krouton.http4k.plus

class Hexagon(accountRouter: AccountRouter, userRouter: UserRouter) {
    val resources = accountRouter.routes + userRouter.routes

    operator fun ResourceRouter.plus(that: ResourceRouter) =
        ResourceRouter(this.router + that.router)
}