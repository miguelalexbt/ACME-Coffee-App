package org.feup.cmov.acmeclient.adapter

interface ClickListener<T> {
    fun onClick(content: T)
}