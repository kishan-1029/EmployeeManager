package com.example.employeemanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.employeemanager.data.Employee
import com.example.employeemanager.repository.EmployeeRepository
import com.example.employeemanager.data.EmployeeDatabase
import kotlinx.coroutines.launch

class EmployeeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: EmployeeRepository
    val allEmployees: LiveData<List<Employee>>

    init {
        val dao = EmployeeDatabase.getDatabase(application).employeeDao()
        repo = EmployeeRepository(dao)
        allEmployees = repo.allEmployees
    }

    // Insert a new employee
    fun insert(employee: Employee) = viewModelScope.launch {
        repo.insert(employee)
    }

    // Update an existing employee
    fun updateEmployee(employee: Employee) {
        viewModelScope.launch {
            repo.updateEmployee(employee)  // This assumes your repository has an update method
        }
    }

    // Delete an employee
    fun delete(employee: Employee) = viewModelScope.launch {
        repo.delete(employee)
    }

    // Search employees based on a query
    fun searchEmployees(query: String) = repo.searchEmployees(query)
}
