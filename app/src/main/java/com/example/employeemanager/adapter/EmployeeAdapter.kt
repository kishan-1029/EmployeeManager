package com.example.employeemanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.employeemanager.R
import com.example.employeemanager.data.Employee
import com.google.android.material.button.MaterialButton

class EmployeeAdapter(private val onUpdate: (Employee) -> Unit, private val onDelete: (Employee) -> Unit) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder>() {

    private var employees: List<Employee> = listOf()

    inner class EmployeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtDesignation: TextView = itemView.findViewById(R.id.txtDesignation)
        val txtSalary: TextView = itemView.findViewById(R.id.txtSalary)
        val btnUpdate: MaterialButton = itemView.findViewById(R.id.btnUpdate) // Use MaterialButton
        val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete) // Use MaterialButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.employee_item, parent, false)
        return EmployeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        val employee = employees[position]

        holder.txtName.text = employee.name
        holder.txtDesignation.text = employee.designation
        holder.txtSalary.text = employee.salary.toString()

        holder.btnUpdate.setOnClickListener {
            onUpdate(employee)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(employee)
        }
    }

    override fun getItemCount(): Int {
        return employees.size
    }

    fun setData(employees: List<Employee>) {
        this.employees = employees
        notifyDataSetChanged()
    }
}

