package com.gaethering.gaetheringserver.core.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalDateValidator implements ConstraintValidator<LocalDateValid, String> {

    private String pattern;

    @Override
    public void initialize(LocalDateValid constraintAnnotation) {
        this.pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            LocalDate.from(LocalDate.parse(value, DateTimeFormatter.ofPattern(this.pattern)));
        } catch (DateTimeParseException e) {
            log.error("DateValidator : {}", e);
            return false;
        }
        return true;
    }
}
