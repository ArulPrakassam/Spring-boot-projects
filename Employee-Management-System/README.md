# Employee Records Management System

A Spring boot Rest API for managing employee records. We can do CRUD operations.

The routes are protected by spring security and JWT token.

The flow will be Controller-> Service-> Repo.

[API Documentation](https://employee-records-management.onrender.com/)

## API Endpoints

- Register - POST /auth/register
- Login - POST /auth/login
- Create an Employee - POST /api/employees
- Get Employee by ID - GET /api/employees/{id}
- Update Employee - PUT /api/employees/{id}
- Delete Employee - DELETE /api/employees/{id}
- Get All Employees (Paginated and Sorted) - GET /api/employees?page=0&size=10&sort=name,asc
- Search Employees by name or department - GET /api/employees/search?query={serachTerm}

## API Reference

#### Register

Register as an ADMIN or USER. ADMIN will have full access. USER will be able to access only GET routes.

```http
  POST /auth/register
```

Sample Request body

```js
{
  "name": "David",
  "email": "david@example.com",
  "password": "david1",
  "role": "ADMIN"
}
```

#### Login

```http
  POST /auth/login
```

Sample Request body

```js
{
  "email": "david@example.com",
  "password": "david1"
}
```

#### Authorization

Except Register and Login routes, all other routes need JWT token in Authorization header to access.

```js
Bear ${token}
```

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

User Data model

```js
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role; // ROLE_USER or ROLE_ADMIN
```

LoginRequest

```js
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;
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

```js
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

## Register

Controller layer

```js
   @PostMapping("/register")
    public ResponseEntity<ResponseObject<Object>> register(@Valid @RequestBody User registerUser)
            throws UserAlreadyExistsException {
        User user = authService.registerUser(registerUser);
        ResponseObject<Object> response = new ResponseObject<>(true, 201, "User Created Successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
```

Service layer

```js
    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with this email already exists.");

        }


        user.setPassword(encoder.encode(user.getPassword())); // Encrypt password
        user.setRole("ROLE_" + user.getRole());

        return userRepo.save(user);
    }
```

## Login

Controller layer

```js
@PostMapping("/login")
    public ResponseEntity<ResponseObject<Map<String, String>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, String> token = authService.loginUser(loginRequest);
        ResponseObject<Map<String, String>> response = new ResponseObject<>(true, 200, "Successful Request", token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
```

Service layer

```js
public Map<String, String> loginUser(LoginRequest loginRequest) {
        Map<String, String> tokenValue = new HashMap<>();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // After successful authentication, generate the JWT token
        String token = jwtService.generateToken(authentication.getName(), authentication.getAuthorities().toString());

        tokenValue.put("token", token);
        return tokenValue;
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

## Security

Using spring security and JWT token for authorization.

### SecurityConfig.java

```js
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        // public routes
                        .requestMatchers("/", "/*.html", "/swagger.yaml", "/auth/register", "/auth/login",
                                "/h2-console/**")
                        .permitAll()
                        // User can access GET, but not PUT, POST, DELETE
                        .requestMatchers(HttpMethod.GET, "/api/employees/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/employees/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/{id}").hasRole("ADMIN")

                        // Any other requests must be authenticated
                        .anyRequest().authenticated())
                // error response for access denied for user roles
                .exceptionHandling(customizer -> customizer.accessDeniedHandler(
                        (request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");

                            String errorResponse = "{\"success\": false, \"status\": 403, \"message\": \"Access Denied.  Not able to perform operation.\"}";
                            response.getWriter().write(errorResponse);

                        }))

                // for h2 console, enabling frames
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### JwtFilter.java

```js
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // Skip token validation for login and register routes
        if (isPublicRoute(request)) {
            filterChain.doFilter(request, response);  // Skip token validation
            return;
        }

        String header = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);

        }

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"No authentication token specified in x-auth-token header\"}");
            return;  // Return response and don't proceed further
        }

        try {
            userName = jwtService.extractUsername(token);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = context.getBean(CustomUserDetailsService.class).loadUserByUsername(userName);

                if (jwtService.validateToken(token, jwtService.extractUsername(token))) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
                    response.setContentType("application/json");
                    response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"Access token is not valid or has expired, you will need to login\"}");
                    return;  // Return response and don't proceed further
                }
            }
        } catch (MalformedJwtException | SignatureException | ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"status\": 401, \"message\": \"Access token is not valid or has expired, you will need to login\"}");
            return;  // Return response and don't proceed further
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicRoute(HttpServletRequest request) {
        // Skip token validation for specific public routes
        String uri = request.getRequestURI();
        return uri.equals("/") ||
                uri.equals("/index.html") ||
                uri.equals("/swagger.html") ||
                uri.equals("/swagger.yaml") ||
                uri.equals("/h2-console/") ||
                uri.equals("/auth/login") ||
                uri.equals("/auth/register");
    }
}
```

### JwtService.java

```js
@Service
public class JwtService {

    private static final long EXPIRATION_TIME = 86400000L; // 24 hours

    private String secretKey;

    public String generateSecretKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    public JwtService() {
        secretKey = generateSecretKey();
    }

    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }
}

```

### CustomUserDetailsService.java

```js
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user;

    }
}

```

### User extends UserDetails

```js
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String role; // ROLE_USER or ROLE_ADMIN

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
```

## Exception Handling

### GlobalExceptionHandler

```js
@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle if employee data is not found
    @ExceptionHandler(NoEmployeeException.class)
    public ResponseEntity<ResponseObject<Object>> handleNoEmployeeException(NoEmployeeException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.NOT_FOUND.value(), e.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // when trying to access restricted resource
    @ExceptionHandler(AccessForbiddenException.class)
    public ResponseEntity<ResponseObject<Object>> handleAccessForBiddenException(AccessForbiddenException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.FORBIDDEN.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // Handle validation errors for @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseObject<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        // String errors = e.getBindingResult().getFieldErrors()
        // .stream()
        // .map(DefaultMessageSourceResolvable::getDefaultMessage)
        // .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors for @RequestParam, @PathVariable
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseObject<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        // String errors = e.getConstraintViolations()
        // .stream()
        // .map(ConstraintViolation::getMessage)
        // .collect(Collectors.joining(", "));

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(), "Bad Request");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // handle if the registering user is already there
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseObject<Object>> handleInvalidInputException(UserAlreadyExistsException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.BAD_REQUEST.value(),
                "Bad Request. " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }

    // handle for bad credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseObject<Object>> handleBadCredentialsException(BadCredentialsException e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized Request. " + e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);

    }

    // all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Object>> handleGeneralException(Exception e) {

        ResponseObject<Object> response = new ResponseObject<>(false, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An error occurred: Internal Server Error");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
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

### UserAlreadyExistsException

```js
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
```

### AccessForbiddenException

```js
public class AccessForbiddenException extends Exception {
    public AccessForbiddenException(String message) {
        super(message);
    }
}
```

## Tech Stack

- Backend - Spring boot
- Database - H2 In-memory Database
- Hosting - Render, Docker
