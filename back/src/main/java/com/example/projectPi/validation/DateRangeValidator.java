package com.example.projectPi.validation;

import java.lang.reflect.Field;
import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation that checks if startDate is before endDate
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {
    
    private String startDateField;
    private String endDateField;
    
    @Override
    public void initialize(ValidDateRange annotation) {
        this.startDateField = annotation.startDateField();
        this.endDateField = annotation.endDateField();
    }
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        try {
            Field startField = value.getClass().getDeclaredField(startDateField);
            Field endField = value.getClass().getDeclaredField(endDateField);
            
            startField.setAccessible(true);
            endField.setAccessible(true);
            
            LocalDate startDate = (LocalDate) startField.get(value);
            LocalDate endDate = (LocalDate) endField.get(value);
            
            if (startDate == null || endDate == null) {
                return true; // Null values are handled by @NotNull
            }
            
            return startDate.isBefore(endDate) || startDate.isEqual(endDate);
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error validating date range", e);
        }
    }
}
