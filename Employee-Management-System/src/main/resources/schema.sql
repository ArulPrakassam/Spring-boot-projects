CREATE TABLE employees (
    id INT AUTO_INCREMENT PRIMARY KEY,               -- Unique identifier
    name VARCHAR(40) NOT NULL,                       -- Name (NOT NULL to reflect @NotBlank)
    department VARCHAR(50) NOT NULL,                 -- Department (instead of category, reflecting @NotBlank)
    age INT NOT NULL CHECK (age >= 18),               -- Age (with @Min(18) constraint)
    email VARCHAR(100) NOT NULL,                     -- Email (NOT NULL and should be validated with @Email)
    salary DECIMAL(10, 2) CHECK (salary >= 30000.00), -- Salary (with @DecimalMin constraint)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Created date (timestamp to reflect @CreatedDate)
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Updated date (timestamp to reflect @LastModifiedDate)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL
);

