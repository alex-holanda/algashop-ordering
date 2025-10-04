package com.algaworks.ordering.domain.valueobject;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

import static com.algaworks.ordering.domain.exception.ErrorMessages.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;

public record BirthDate(LocalDate value) {

    public BirthDate {
        Objects.requireNonNull(value);
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
    }

    public Integer age() {
        return Math.toIntExact(Duration.between(value, LocalDate.now()).toDays());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
