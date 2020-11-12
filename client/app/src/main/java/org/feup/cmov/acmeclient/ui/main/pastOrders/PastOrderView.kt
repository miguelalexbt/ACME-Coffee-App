package org.feup.cmov.acmeclient.ui.main.pastOrders

data class PastOrderView(
    val id: String,
    val number: Int,
    val itemsCount: Int,
    val createdAt: String,
    val total: Double,
    val items: Map<String, Int>,
    val vouchers: Set<String>
)