package com.example.projectPi.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Standard API Response Wrapper for all endpoints
 * Provides consistent response structure with status, message, data, and errors
 * 
 * Usage:
 *   Success: new ApiResponseDTO<>(data, "Operation successful")
 *   Error: new ApiResponseDTO<>(null, "Operation failed", List.of("Error details"))
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiResponseDTO", description = "Standard API Response wrapper")
public class ApiResponseDTO<T> {
    
    @Schema(description = "HTTP Status Code", example = "200")
    private int statusCode;
    
    @Schema(description = "Response message", example = "Request processed successfully")
    private String message;
    
    @Schema(description = "Response data payload")
    private T data;
    
    @Schema(description = "List of error messages (if any)")
    private List<String> errors;
    
    @Schema(description = "Timestamp of the response")
    private LocalDateTime timestamp;
    
    // Constructors
    
    public ApiResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor for successful response with data
     */
    public ApiResponseDTO(int statusCode, String message, T data) {
        this();
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Constructor for successful response
     */
    public ApiResponseDTO(T data, String message) {
        this(200, message, data);
    }
    
    /**
     * Constructor for error response
     */
    public ApiResponseDTO(int statusCode, String message, List<String> errors) {
        this();
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
    }
    
    /**
     * Static factory methods for fluent API
     */
    
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return new ApiResponseDTO<>(200, message, data);
    }
    
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(200, "Operation successful", data);
    }
    
    public static <T> ApiResponseDTO<T> created(T data, String message) {
        return new ApiResponseDTO<>(201, message, data);
    }
    
    public static <T> ApiResponseDTO<T> created(T data) {
        return new ApiResponseDTO<>(201, "Resource created successfully", data);
    }
    
    public static <T> ApiResponseDTO<T> badRequest(String message, List<String> errors) {
        return new ApiResponseDTO<>(400, message, errors);
    }
    
    public static <T> ApiResponseDTO<T> notFound(String message) {
        return new ApiResponseDTO<>(404, message, List.of(message));
    }
    
    public static <T> ApiResponseDTO<T> forbidden(String message) {
        return new ApiResponseDTO<>(403, message, List.of(message));
    }
    
    public static <T> ApiResponseDTO<T> serverError(String message, List<String> errors) {
        return new ApiResponseDTO<>(500, message, errors);
    }
    
    // Getters and Setters
    
    public int getStatusCode() {
        return statusCode;
    }
    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
