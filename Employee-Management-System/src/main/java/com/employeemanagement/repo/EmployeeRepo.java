package com.employeemanagement.repo;

import com.employeemanagement.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    List<Employee> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name,String department);
}
