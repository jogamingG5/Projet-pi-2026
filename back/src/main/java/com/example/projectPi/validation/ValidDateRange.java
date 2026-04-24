package com.example.projectPi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Custom validator annotation to ensure dateDebut is before dateFin
 * Usage: Add @ValidDateRange on the class containing dateDebut and dateFin fields
 */
@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "Start date must be before end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    String startDateField() default "dateDebut";
    String endDateField() default "dateFin";
}
