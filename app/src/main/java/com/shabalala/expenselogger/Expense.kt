package com.shabalala.expenselogger

data class Expense(
    val id: Long,
    val amount: Double,
    val description: String,
    val photoPath: String,
    val timestamp: Long
)