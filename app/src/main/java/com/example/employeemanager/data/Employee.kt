package com.example.employeemanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_table")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val designation: String,
    val salary: Double
)
