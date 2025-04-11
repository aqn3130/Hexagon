package com.hexagon.models

import org.http4k.template.ViewModel

data class UserViewModel(val id: String, val name: String) : ViewModel
