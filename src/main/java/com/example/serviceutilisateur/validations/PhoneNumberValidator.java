package com.example.serviceutilisateur.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String REGEX_CAMTEL = "^(6(?:8|7|5[1-4])\\d{9})$";
    private static final String REGEX_ORANGE = "^6((9\\d{7})|(5[5-9]\\d{6}))$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return Pattern.matches(REGEX_CAMTEL, value) || Pattern.matches(REGEX_ORANGE, value);
    }
    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) { }
}
