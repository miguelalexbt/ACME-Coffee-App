package org.feup.cmov.acmeclient.data.cache

data class CachedOrder(
    val id: String = "",
    val items: Map<String, Int> = emptyMap(),
    val offerVouchers: Set<String> = emptySet(),
    val discountVoucher: String? = null
)