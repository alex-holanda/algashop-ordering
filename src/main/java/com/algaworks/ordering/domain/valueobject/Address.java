package com.algaworks.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

import static com.algaworks.ordering.domain.validator.FieldValidations.requiresNonBlank;

@Builder(toBuilder = true)
public record Address(
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        ZipCode zipCode
) {

    public Address {
        requiresNonBlank(street);
        requiresNonBlank(number);
        requiresNonBlank(neighborhood);
        requiresNonBlank(city);
        requiresNonBlank(state);
        Objects.requireNonNull(zipCode);
    }
}
