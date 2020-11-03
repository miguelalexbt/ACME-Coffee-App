package org.feup.cmov.acmeclient.data.model

// TODO CachedOrder ?

data class Order(
    val id: String = "",
    val items: Map<String, Int> = emptyMap(),
    val offerVouchers: Set<String> = emptySet(),
    val discountVoucher: String? = null
)