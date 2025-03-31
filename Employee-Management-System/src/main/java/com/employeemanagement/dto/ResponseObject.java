package com.employeemanagement.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObject<T> {
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
}


