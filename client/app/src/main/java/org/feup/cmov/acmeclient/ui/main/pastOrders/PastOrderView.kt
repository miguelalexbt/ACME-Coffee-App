package org.feup.cmov.acmeclient.ui.main.pastOrders

data class PastOrderView(
    val id: String,
    val itemsCount: Int,
    val createAt: String,
    val total: Double,
)