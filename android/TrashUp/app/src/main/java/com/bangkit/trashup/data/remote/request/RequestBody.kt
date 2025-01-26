package com.bangkit.trashup.data.remote.request

data class RequestBody (
    val contents: List<Content>
)

data class Content (
    val role: String,
    val parts: List<Part>
)

data class Part (
    val text: String
)