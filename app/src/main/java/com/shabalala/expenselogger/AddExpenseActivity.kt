package com.shabalala.expenselogger

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shabalala.expenselogger.databinding.ActivityAddExpenseBinding
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var databaseHelper: DatabaseHelper
    private var photoPath: String? = null
    private var timestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)

        // Get photo path from intent
        photoPath = intent.getStringExtra("photoPath")
        timestamp = System.currentTimeMillis()

        // Display the photo
        photoPath?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            binding.imageViewReceipt.setImageBitmap(bitmap)
        }

        // Display timestamp
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        binding.textViewTimestamp.text = "Taken on: ${dateFormat.format(Date(timestamp))}"

        // Setup button listeners
        binding.buttonSave.setOnClickListener {
            saveExpense()
        }

        binding.buttonCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveExpense() {
        val amountText = binding.editTextAmount.text.toString()
        val description = binding.editTextDescription.text.toString()

        if (amountText.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val amount = amountText.toDouble()
            val expense = Expense(
                id = 0,
                amount = amount,
                description = description,
                photoPath = photoPath ?: "",
                timestamp = timestamp
            )

            val id = databaseHelper.addExpense(expense)
            if (id != -1L) {
                Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show()
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
        }
    }
}