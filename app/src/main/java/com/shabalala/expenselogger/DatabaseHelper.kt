package com.shabalala.expenselogger

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ExpenseDatabase.db"
        private const val DATABASE_VERSION = 1

        // Table and column names
        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_PHOTO_PATH = "photo_path"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PHOTO_PATH TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun addExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_DESCRIPTION, expense.description)
            put(COLUMN_PHOTO_PATH, expense.photoPath)
            put(COLUMN_TIMESTAMP, expense.timestamp)
        }

        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return id
    }

    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_EXPENSES ORDER BY $COLUMN_TIMESTAMP DESC"
        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val expense = Expense(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    photoPath = it.getString(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                expenses.add(expense)
            }
        }

        db.close()
        return expenses
    }

    fun getExpenseById(id: Long): Expense? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_EXPENSES WHERE $COLUMN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))

        var expense: Expense? = null
        cursor.use {
            if (it.moveToFirst()) {
                expense = Expense(
                    id = it.getLong(it.getColumnIndexOrThrow(COLUMN_ID)),
                    amount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    description = it.getString(it.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    photoPath = it.getString(it.getColumnIndexOrThrow(COLUMN_PHOTO_PATH)),
                    timestamp = it.getLong(it.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
            }
        }

        db.close()
        return expense
    }

    fun deleteExpense(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun getTotalAmount(): Double {
        val db = readableDatabase
        val query = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES"
        val cursor = db.rawQuery(query, null)

        var total = 0.0
        cursor.use {
            if (it.moveToFirst()) {
                total = it.getDouble(0)
            }
        }

        db.close()
        return total
    }
}