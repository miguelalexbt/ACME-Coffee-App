package org.feup.cmov.acmeclient.ui.main.home

data class ItemView(
    val id: String,
    val name: String,
    val price: Double,
    val isSelected: Boolean,
    val isFavorite: Boolean,
    val usersFavorite: Set<String>
)