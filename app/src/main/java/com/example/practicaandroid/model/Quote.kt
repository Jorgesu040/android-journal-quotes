package com.example.practicaandroid.model

data class Quote(
    val q: String = "", // quote text
    val a: String = "", // author name
    val i: String = "", // author image
    val c: String = "", // character count
    val h: String = ""  // pre-formatted HTML quote
)
