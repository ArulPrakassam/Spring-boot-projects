package com.employeemanagement.service;


import com.employeemanagement.exceptionhandling.NoEmployeeException;
import com.employeemanagement.model.Employee;
import com.employeemanagement.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo repo;

    //get all employees
    public List<Employee> getAllEmployees(int page,int size,String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams[1];

        // Create a Sort object based on the parameters
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sortObj = Sort.by(direction, sortField);

        // Create a PageRequest with pagination and sorting
        PageRequest pageRequest = PageRequest.of(page, size, sortObj);

        // Fetch the employees with pagination and sorting
        Page<Employee> employeePage = repo.findAll(pageRequest);

        return employeePage.getContent();
    }

    //add new employee
    public Employee addEmployee(Employee emp) {

        emp.setCreatedAt(LocalDateTime.now());
        emp.setUpdatedAt(LocalDateTime.now());

        return repo.save(emp);
    }

    //get single employee by id
    public Employee getEmployeeById(Long id) throws NoEmployeeException {
        Optional<Employee> emp=repo.findById(id);
        if(emp.isPresent()){
            return emp.get();
        }else{
            throw new NoEmployeeException("No Employee with id: "+id);
        }
    }

    //update single employee by id
    public Employee updateEmployeeById(Long id,Employee employee) throws NoEmployeeException {
        Optional<Employee> emp=repo.findById(id);

        if(emp.isPresent()){
            Employee existingEmployee = emp.get();

            existingEmployee.setName(employee.getName());
            existingEmployee.setDepartment(employee.getDepartment());
            existingEmployee.setAge(employee.getAge());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setSalary(employee.getSalary());
            existingEmployee.setUpdatedAt(LocalDateTime.now());

            repo.save(existingEmployee);
            return existingEmployee;
        }else{
            throw new NoEmployeeException("Not able to update employee data.  No Employee with id "+id+" found");
        }
    }

    //delete single employee by id
    public String deleteEmployeeById(Long id) throws NoEmployeeException {
        Optional<Employee> emp=repo.findById(id);
        if(emp.isPresent()){
            repo.delete(emp.get());

            return "Employee deleted successfully";
        }else{
            throw new NoEmployeeException("Not able to delete employee data.  No Employee with id "+id+" found");
        }
    }

    //search employees by name or department
    public List<Employee> searchEmployees(String query) {
        return repo.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(query,query);
    }
}
