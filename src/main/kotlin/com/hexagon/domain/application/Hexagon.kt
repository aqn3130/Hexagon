package com.hexagon.domain.application

class Hexagon(accountRouter: AccountRouter) {
    val resources = accountRouter.routes
}