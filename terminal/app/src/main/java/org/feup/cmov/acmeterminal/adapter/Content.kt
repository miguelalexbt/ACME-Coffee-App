package org.feup.cmov.acmeterminal.adapter

data class Content<T>(
    val id: String,
    val content: T
)