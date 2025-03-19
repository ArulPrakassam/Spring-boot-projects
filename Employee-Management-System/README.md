# Employee Records Management System

A Spring boot Rest API for managing employee records. We can do CRUD operations.

The flow will be Controller-> Service-> Repo.

[API Documentation](https://employee-records-management.onrender.com/)

## API Endpoints

- Create an Employee - POST /api/employees
- Get Employee by ID - GET /api/employees/{id}
- Update Employee - PUT /api/employees/{id}
- Delete Employee - DELETE /api/employees/{id}
- Get All Employees (Paginated and Sorted) - GET /api/employees?page=0&size=10&sort=name,asc
- Search Employees by name or department - GET /api/employees/search?query={serachTerm}

## API Reference

#### Create an Employee

```http
  POST /api/employees
```

Sample Request body

```js
{
  "name": "Leodas",
  "department": "cyber",
  "age": 22,
  "email": "leo@example.com",
  "salary": 40000
}
```

#### Get Employee by ID

```http
  GET /api/employees/{id}
```

| Parameter | Type      | Description                           |
| :-------- | :-------- | :------------------------------------ |
| `id`      | `integer` | **Required**. Id of employee to fetch |

#### Update Employee

```http
  PUT /api/employees/{id}
```

| Parameter | Type      | Description                           |
| :-------- | :-------- | :------------------------------------ |
| `id`      | `integer` | **Required**. Id of employee to fetch |

Sample Request body

```js
{
  "name": "John Doe",
  "department": "Sales",
  "age": 26,
  "email": "johndoe@example.com",
  "salary": 50000
}
```

#### Delete Employee

```http
  DELETE /api/employees/{id}
```

| Parameter | Type      | Description                           |
| :-------- | :-------- | :------------------------------------ |
| `id`      | `integer` | **Required**. Id of employee to fetch |

#### Get All Employees (Paginated and Sorted)

```http
  GET /api/employees
```

```http
  GET /api/employees?page=0&size=10&sort=name,asc
```

| Parameter | Type      | Description                           |
| :-------- | :-------- | :------------------------------------ |
| `page`    | `integer` | **Optional**. page count              |
| `size`    | `integer` | **Optional**. size count              |
| `sort`    | `string`  | **Optional**. sorting field and order |

| Field        | Order         | Example             |
| :----------- | :------------ | :------------------ |
| `id`         | `asc or desc` | sort=id,asc         |
| `name`       | `asc or desc` | sort=name,desc      |
| `department` | `asc or desc` | sort=department,asc |
| `name`       | `asc or desc` | sort=name,desc      |
| `salary`     | `asc or desc` | sort=salary,asc     |

#### Search Employees by name or department

```http
  GET /api/employees/search?query={serachTerm}
```

| Parameter    | Type     | Description                            |
| :----------- | :------- | :------------------------------------- |
| `serachTerm` | `string` | **Optional**. name or department value |

## Data Model

Employee Data Model

```js
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Department is required")
    private String department;

    @Min(value = 18, message = "Age must be at least 18")
    private int age;

    @Email
    private String email;

    @DecimalMin(value = "30000.00", message = "Salary must be at least 30,000.00")
    private BigDecimal salary;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

```

ResponseObject

```js
    private boolean success;
    private int status;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // Constructor for error responses where we don't need the data
    public ResponseObject(boolean success, int status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.data = null;
    }
```

## Repository layer

We are using JpaRepository.

```js
@Repository
public interface EmployeeRepo extends JpaRepository<Employee,Long> {
    List<Employee> findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String name,String department);
}
```

## Create an Employee

Controller layer

```js
   @PostMapping("/employees")
    public ResponseEntity<ResponseObject<Employee>> addEmployeeById(@Valid @RequestBody Employee emp) throws NoEmployeeException {

        Employee newEmployee = service.addEmployee(emp);
        ResponseObject<Employee> response = new ResponseObject<>(true, 200, "Employee added successfully", newEmployee);
        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }
```

Service layer

```js
    //add new employee
    public Employee addEmployee(Employee emp) {

        emp.setCreatedAt(LocalDateTime.now());
        emp.setUpdatedAt(LocalDateTime.now());

        return repo.save(emp);
    }
```

## Get Employee by ID

Controller layer

```js
@GetMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<Employee>> getEmployeeById(@PathVariable Long id) throws NoEmployeeException {

        Employee employee = service.getEmployeeById(id);
        ResponseObject<Employee> response = new  ResponseObject<>(true, 200, "Successful Request", employee);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
```

Service layer

```js
    //get single employee by id
    public Employee getEmployeeById(Long id) throws NoEmployeeException {
        Optional<Employee> emp=repo.findById(id);
        if(emp.isPresent()){
            return emp.get();
        }else{
            throw new NoEmployeeException("No Employee with id: "+id);
        }
    }
```

## Update Employee

Controller layer

```js
   @PutMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<Employee>> updateEmployeeById(@PathVariable Long id, @Valid @RequestBody Employee emp) throws NoEmployeeException {

        Employee updatedEmployee = service.updateEmployeeById(id, emp);
        ResponseObject<Employee> response = new ResponseObject<>(true, 200, "Employee updated successfully", updatedEmployee);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
```

Service layer

```js
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
```

## Delete Employee

Controller layer

```js
@DeleteMapping("/employees/{id}")
    public ResponseEntity<ResponseObject<String>> deleteEmployeeById(@PathVariable Long id) throws NoEmployeeException {
        String result = service.deleteEmployeeById(id);
        ResponseObject<String> response = new  ResponseObject<>(true, 200, "Successful Request");
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
```

Service layer

```js
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
```

## Get All Employees

Controller layer

```js
    @GetMapping("/employees")
    public ResponseEntity<ResponseObject<List<Employee>>> getAllEmployees( @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be 0 or greater") int page,
                                                                           @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
                                                                           @RequestParam(defaultValue = "name,asc") @Pattern(regexp = "^(id|name|department|age|salary),(asc|desc)$", message = "Invalid sort format. Use 'field,asc' or 'field,desc'") String sort){
        List<Employee> employees = service.getAllEmployees(page,size,sort);
        ResponseObject<List<Employee>> response = new ResponseObject<>(true, 200, "Successful Request", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }
```

Service layer

```js
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
```

## Search Employees by name or department

Controller layer

```js
    @GetMapping("/employees/search")
    public ResponseEntity<ResponseObject<List<Employee>>> searchEmployees(@RequestParam String query){
        List<Employee> employees = service.searchEmployees(query);
        ResponseObject<List<Employee>> response = new ResponseObject<>(true, 200, "Successful Request", employees);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
```

Service layer

```js
    //search employees by name or department
    public List<Employee> searchEmployees(String query) {
        return repo.findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCase(query,query);
    }
```

## Exception Handling

### GlobalExceptionHandler

```js
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoEmployeeException.class)
    public ResponseEntity<ResponseObject<Object>> handleNoEmployeeException(NoEmployeeException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.NOT_FOUND.value(), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Object>> handleGeneralException(Exception e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(),"An error occurred: Internal Server Error");


        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle validation errors for @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request. "+errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors for @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        String errors = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request. "+errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}

```

### NoEmployeeException

```js
public class NoEmployeeException extends Exception{
    public NoEmployeeException(String message) {
        super(message);
    }
}
```

## Tech Stack

- Backend - Spring boot
- Database - H2 In-memory Database
- Hosting - Render
