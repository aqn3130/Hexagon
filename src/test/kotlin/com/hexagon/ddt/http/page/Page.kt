package com.hexagon.ddt.http.page

import java.net.URI

interface Page : PageFragment {
    val pageUri: URI
}