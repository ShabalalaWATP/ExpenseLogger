package com.shabalala.expenselogger

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.shabalala.expenselogger.databinding.ActivityExpenseDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class ExpenseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseDetailBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var expenseId: Long = -1
    private var expense: Expense? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        // Get expense ID from intent
        expenseId = intent.getLongExtra("expenseId", -1)
        if (expenseId == -1L) {
            Toast.makeText(this, "Error loading expense", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadExpenseDetails()

        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun loadExpenseDetails() {
        expense = databaseHelper.getExpenseById(expenseId)
        expense?.let {
            // Display receipt image
            val bitmap = BitmapFactory.decodeFile(it.photoPath)
            binding.imageViewReceiptDetail.setImageBitmap(bitmap)

            // Display amount
            binding.textViewAmountDetail.text = "Amount: £%.2f".format(it.amount)

            // Display description
            binding.textViewDescriptionDetail.text =
                "Description: ${it.description.ifEmpty { "No description" }}"

            // Display timestamp
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            binding.textViewTimestampDetail.text =
                "Date: ${dateFormat.format(Date(it.timestamp))}"
        } ?: run {
            Toast.makeText(this, "Expense not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                deleteExpense()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteExpense() {
        val result = databaseHelper.deleteExpense(expenseId)
        if (result > 0) {
            Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error deleting expense", Toast.LENGTH_SHORT).show()
        }
    }
}