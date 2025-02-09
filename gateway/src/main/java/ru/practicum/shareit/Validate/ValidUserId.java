package ru.practicum.shareit.Validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserIdValidator.class)
public @interface ValidUserId {
    String message() default "User ID должен быть больше 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
