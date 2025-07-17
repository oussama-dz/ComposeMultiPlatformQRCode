package com.testing.composemultiplatformqrcode

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform