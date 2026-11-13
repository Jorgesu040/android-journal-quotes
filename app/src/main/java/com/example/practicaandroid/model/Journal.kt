package com.example.practicaandroid.model

import java.io.Serializable

data class Journal (
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val imgURL: String = "",
    val mood: String = "",
    val timestamp: Long = 0
) : Serializable
