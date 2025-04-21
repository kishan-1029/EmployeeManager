package com.example.employeemanager

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.employeemanager.adapter.EmployeeAdapter
import com.example.employeemanager.data.Employee
import com.example.employeemanager.viewmodel.EmployeeViewModel
import com.google.android.material.textfield.TextInputLayout
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.Phrase
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Element
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: EmployeeAdapter
    private val viewModel: EmployeeViewModel by viewModels()
    private val CHANNEL_ID = "pdf_download_channel"
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnExportPDF = findViewById<Button>(R.id.btnExportPDF)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val searchView = findViewById<SearchView>(R.id.searchView)
        val tilName = findViewById<TextInputLayout>(R.id.tilName)
        val tilDesignation = findViewById<TextInputLayout>(R.id.tilDesignation)
        val tilSalary = findViewById<TextInputLayout>(R.id.tilSalary)

        val etName = findViewById<EditText>(R.id.etName)
        val etDesignation = findViewById<EditText>(R.id.etDesignation)
        val etSalary = findViewById<EditText>(R.id.etSalary)

        adapter = EmployeeAdapter(
            onUpdate = { showEditDialog(it) },
            onDelete = { viewModel.delete(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        viewModel.allEmployees.observe(this) { employees ->
            adapter.setData(employees)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val desig = etDesignation.text.toString().trim()
            val salaryText = etSalary.text.toString().trim()

            var isValid = true

            tilName.error = null
            tilDesignation.error = null
            tilSalary.error = null

            if (name.isEmpty()) {
                tilName.error = "Please enter employee name"
                isValid = false
            }

            if (desig.isEmpty()) {
                tilDesignation.error = "Please enter designation"
                isValid = false
            }

            if (salaryText.isEmpty() || salaryText.toDoubleOrNull() == null) {
                tilSalary.error = "Enter valid salary"
                isValid = false
            }

            if (!isValid) return@setOnClickListener

            val employee = Employee(name = name, designation = desig, salary = salaryText.toDouble())
            viewModel.insert(employee)

            etName.text?.clear()
            etDesignation.text?.clear()
            etSalary.text?.clear()

            Toast.makeText(this, "Employee saved successfully", Toast.LENGTH_SHORT).show()
        }

        btnExportPDF.setOnClickListener {
            requestNotificationPermission()
            viewModel.allEmployees.value?.let { exportToPDF(it) }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    viewModel.searchEmployees(newText).observe(this@MainActivity) {
                        adapter.setData(it)
                    }
                } else {
                    viewModel.allEmployees.observe(this@MainActivity) {
                        adapter.setData(it)
                    }
                }
                return true
            }
        })

        createNotificationChannel()



        sharedPref = getSharedPreferences("theme_pref", Context.MODE_PRIVATE)
        val switchTheme = findViewById<SwitchMaterial>(R.id.switchTheme)

// Apply saved theme
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        switchTheme.isChecked = isDarkMode

// Theme toggle switch listener
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPref.edit().putBoolean("dark_mode", isChecked).apply()
        }

    }

    private fun exportToPDF(employees: List<Employee>) {
        val fileName = "employees_${System.currentTimeMillis()}.pdf"
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)

        try {
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Title
            val titleFont = Font(Font.FontFamily.HELVETICA, 20f, Font.BOLD)
            val title = Paragraph("Employee List", titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)

            // Table with 4 columns
            val table = PdfPTable(4)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(1f, 3f, 4f, 3f))

            // Header
            val headerFont = Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD)
            val headers = listOf("ID", "Name", "Designation", "Salary")
            for (header in headers) {
                val cell = PdfPCell(Phrase(header, headerFont))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.backgroundColor = BaseColor.LIGHT_GRAY
                table.addCell(cell)
            }

            // Employee rows
            val rowFont = Font(Font.FontFamily.HELVETICA, 12f)
            for (emp in employees) {
                table.addCell(Phrase(emp.id.toString(), rowFont))
                table.addCell(Phrase(emp.name, rowFont))
                table.addCell(Phrase(emp.designation, rowFont))
                table.addCell(Phrase("₹ ${emp.salary}", rowFont))
            }

            document.add(table)
            document.close()

            Toast.makeText(this, "PDF saved to Downloads", Toast.LENGTH_SHORT).show()
            showNotification(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error exporting PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNotification(file: File) {
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Download Complete")
            .setContentText("Tap to open the PDF")
            .setSmallIcon(R.drawable.ic_download) // Add your icon in drawable
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PDF Download"
            val desc = "Notifies when a PDF is downloaded"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = desc
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }

    private fun showEditDialog(employee: Employee) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_employee, null)
        val etEditName = dialogView.findViewById<EditText>(R.id.etEditName)
        val etEditDesignation = dialogView.findViewById<EditText>(R.id.etEditDesignation)
        val etEditSalary = dialogView.findViewById<EditText>(R.id.etEditSalary)

        etEditName.setText(employee.name)
        etEditDesignation.setText(employee.designation)
        etEditSalary.setText(employee.salary.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Employee")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = etEditName.text.toString().trim()
                val desig = etEditDesignation.text.toString().trim()
                val salaryText = etEditSalary.text.toString().trim()

                if (name.isEmpty() || desig.isEmpty() || salaryText.isEmpty()) {
                    Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updated = employee.copy(
                    name = name,
                    designation = desig,
                    salary = salaryText.toDoubleOrNull() ?: employee.salary
                )

                viewModel.updateEmployee(updated)
                Toast.makeText(this, "Employee updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
