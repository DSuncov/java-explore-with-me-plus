package ru.practicum.event.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinLengthValidator.class)
public @interface MinLength {
    String message() default "Длина строки не может быть меньше указанного значения";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long value();
}
