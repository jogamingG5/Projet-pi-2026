package com.example.projectPi.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.example.projectPi.dto.ApiResponseDTO;

/**
 * Global Exception Handler for all REST endpoints
 * Provides consistent error response format using ApiResponse wrapper
 * Handles validation errors, not found errors, and general exceptions
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.badRequest("Validation failed", errors));
    }

    /**
     * Handle MatchNotFoundException
     */
    @ExceptionHandler(MatchNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMatchNotFound(
            MatchNotFoundException ex,
            WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.notFound(ex.getMessage()));
    }

    /**
     * Handle EventNotFoundException
     */
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleEventNotFound(
            EventNotFoundException ex,
            WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.notFound(ex.getMessage()));
    }

    /**
     * Handle InvalidInputException
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInvalidInput(
            InvalidInputException ex,
            WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.badRequest(ex.getMessage(), 
                       List.of(ex.getMessage())));
    }

    /**
     * Handle ResourceAlreadyExistsException
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex,
            WebRequest request) {
        
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDTO<>(409, ex.getMessage(), 
                       List.of(ex.getMessage())));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGeneric(
            Exception ex,
            WebRequest request) {
        
        List<String> errors = List.of(ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.serverError("Internal Server Error", errors));
    }
}
