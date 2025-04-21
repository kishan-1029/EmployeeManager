package com.example.employeemanager.repository

import androidx.lifecycle.LiveData
import com.example.employeemanager.data.Employee
import com.example.employeemanager.data.EmployeeDao

class EmployeeRepository(private val employeeDao: EmployeeDao) {

    val allEmployees: LiveData<List<Employee>> = employeeDao.getAllEmployees()

    // Insert an employee
    suspend fun insert(employee: Employee) {
        employeeDao.insert(employee)
    }

    // Update an employee
    suspend fun updateEmployee(employee: Employee) {
        employeeDao.updateEmployee(employee) // Calls updateEmployee in DAO
    }

    // Delete an employee
    suspend fun delete(employee: Employee) {
        employeeDao.delete(employee)
    }

    // Search employees by name or designation
    fun searchEmployees(query: String): LiveData<List<Employee>> {
        return employeeDao.searchEmployees(query)
    }
}
