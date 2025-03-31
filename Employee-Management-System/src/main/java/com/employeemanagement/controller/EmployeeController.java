package com.employeemanagement.controller;


import com.employeemanagement.exceptionhandling.AccessForbiddenException;
import com.employeemanagement.exceptionhandling.NoEmployeeException;
import com.employeemanagement.model.Employee;
import com.employeemanagement.dto.ResponseObject;
import com.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Validated
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService service;

    // /employees?page=0&size=10&sort=name,asc

    @GetMapping("/employees")
    public ResponseEntity<ResponseObject<List<Employee>>> getAllEmployees(@RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be 0 or greater") int page,
                                                                          @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
                                                                          @RequestParam(defaultValue = "name,asc") @Pattern(regexp = "^(id|name|department|age|salary),(asc|desc)$", message = "Invalid sort format. Use 'field,asc' or 'field,desc'") String sort) {
        List<Employee> employees = service.getAllEmployees(page, size, sort);
        ResponseObject<List<Employee>> response = new ResponseObject<>(true, 200, "Successful Request", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    @PostMapping("/employees")
    public ResponseEntity<ResponseObject<Employee>> addEmployeeById(@Valid @RequestBody Employee emp) throws NoEmployeeException {

        Employee newEmployee = service.addEmployee(emp);
        ResponseObject<Employee> response = new ResponseObject<>(true, 201, "Employee added successfully", newEmployee);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<Employee>> getEmployeeById(@PathVariable Long id) throws NoEmployeeException {

        Employee employee = service.getEmployeeById(id);
        ResponseObject<Employee> response = new ResponseObject<>(true, 200, "Successful Request", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<Employee>> updateEmployeeById(@PathVariable Long id, @Valid @RequestBody Employee emp) throws NoEmployeeException, AccessForbiddenException {

        Employee updatedEmployee = service.updateEmployeeById(id, emp);
        ResponseObject<Employee> response = new ResponseObject<>(true, 200, "Employee updated successfully", updatedEmployee);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<String>> deleteEmployeeById(@PathVariable Long id) throws NoEmployeeException {
        String result = service.deleteEmployeeById(id);
        ResponseObject<String> response = new ResponseObject<>(true, 200, "Successful Request");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

//    /employees/search?query={searchTerm}

    @GetMapping("/employees/search")
    public ResponseEntity<ResponseObject<List<Employee>>> searchEmployees(@RequestParam String query) {
        List<Employee> employees = service.searchEmployees(query);
        ResponseObject<List<Employee>> response = new ResponseObject<>(true, 200, "Successful Request", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
