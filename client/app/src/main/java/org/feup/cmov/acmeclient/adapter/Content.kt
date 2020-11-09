package org.feup.cmov.acmeclient.adapter

data class Content<T>(
    val id: String,
    val content: T
)