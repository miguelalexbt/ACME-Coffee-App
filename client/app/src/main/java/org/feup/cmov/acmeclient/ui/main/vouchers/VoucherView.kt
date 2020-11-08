package org.feup.cmov.acmeclient.ui.main.vouchers

data class VoucherView(
    val id: String,
    val type: Char,
    val isSelected: Boolean,
    val canBeSelected: Boolean,
)