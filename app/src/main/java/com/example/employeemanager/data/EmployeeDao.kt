package com.example.employeemanager.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EmployeeDao {

    @Insert
    suspend fun insert(employee: Employee)

    @Update
    suspend fun updateEmployee(employee: Employee)

    @Delete
    suspend fun delete(employee: Employee)

    @Query("SELECT * FROM employee_table WHERE name LIKE :query OR designation LIKE :query")
    fun searchEmployees(query: String): LiveData<List<Employee>>

    @Query("SELECT * FROM employee_table")
    fun getAllEmployees(): LiveData<List<Employee>>
}
