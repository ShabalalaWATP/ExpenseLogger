package com.shabalala.expenselogger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.shabalala.expenselogger.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private var currentPhotoPath: String? = null
    private val expenses = mutableListOf<Expense>()

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val CAMERA_PERMISSION_REQUEST = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        setupRecyclerView()
        loadExpenses()
        updateTotalAmount()

        binding.fabAddExpense.setOnClickListener {
            checkCameraPermissionAndCapture()
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(expenses) { expense ->
            // Handle item click - could add edit functionality here
            showExpenseDetails(expense)
        }
        binding.recyclerViewExpenses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
        }
    }

    private fun checkCameraPermissionAndCapture() {
        Toast.makeText(this, "Camera button clicked", Toast.LENGTH_SHORT).show()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Requesting camera permission", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            Toast.makeText(this, "Camera permission already granted", Toast.LENGTH_SHORT).show()
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Launching camera...", Toast.LENGTH_SHORT).show()
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            // Check if there's a camera app available
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                Toast.makeText(this, "Camera app found, creating file...", Toast.LENGTH_SHORT).show()

                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_LONG).show()
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    Toast.makeText(this, "File created, launching camera...", Toast.LENGTH_SHORT).show()
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.shabalala.expenselogger.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            } else {
                Toast.makeText(this, "No camera app found on this device!", Toast.LENGTH_LONG).show()
                // For testing on emulator - bypass camera
                createTestExpense()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Camera error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Test function to bypass camera on emulator
    private fun createTestExpense() {
        Toast.makeText(this, "Using test mode (no camera)", Toast.LENGTH_SHORT).show()
        // Create a dummy file path for testing
        val testPath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "test_receipt.jpg").absolutePath
        currentPhotoPath = testPath

        val intent = Intent(this, AddExpenseActivity::class.java).apply {
            putExtra("photoPath", testPath)
        }
        startActivity(intent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "EXPENSE_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentPhotoPath?.let { path ->
                val intent = Intent(this, AddExpenseActivity::class.java).apply {
                    putExtra("photoPath", path)
                }
                startActivity(intent)
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Toast.makeText(this, "Camera cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadExpenses() {
        expenses.clear()
        expenses.addAll(databaseHelper.getAllExpenses())
        expenseAdapter.notifyDataSetChanged()
    }

    private fun updateTotalAmount() {
        val total = expenses.sumOf { it.amount }
        binding.textViewTotal.text = "Total to claim: £%.2f".format(total)
    }

    private fun showExpenseDetails(expense: Expense) {
        val intent = Intent(this, ExpenseDetailActivity::class.java).apply {
            putExtra("expenseId", expense.id)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        loadExpenses()
        updateTotalAmount()
    }
}