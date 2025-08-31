package com.algaworks.ordering.domain.entity;

import com.algaworks.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.ordering.domain.valueobject.CustomerId;
import com.algaworks.ordering.domain.valueobject.FullName;
import com.algaworks.ordering.domain.valueobject.LoyaltyPoints;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Customer(
                            new CustomerId(),
                            new FullName("John", "Doe"),
                            LocalDate.of(1991, 10, 10),
                            "invalid",
                            "478-256-2504",
                            "255-08-0578",
                            true,
                            OffsetDateTime.now()
                    );
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomer_shouldGenerateException() {

        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1991, 10, 10),
                "john.doe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail("invalid");
                });
    }

    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1991, 10, 10),
                "john.doe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                OffsetDateTime.now()
        );

        customer.archive();

        Assertions.assertWith(customer,
                c -> assertThat(c.fullName().firstName()).isEqualTo("Anonymous"),
                c -> assertThat(c.email()).isNotEqualTo("john.doe@email.com"),
                c -> assertThat(c.phone()).isEqualTo("000-000-0000"),
                c -> assertThat(c.document()).isEqualTo("000-00-0000"),
                c -> assertThat(c.birthDate()).isNull(),
                c -> assertThat(c.isPromotionNotificationsAllowed()).isFalse()
        );
    }

    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("Anonymous", "Anonymous"),
                null,
                "anonymous@anonymous.com",
                "000-000-0000",
                "000-00-0000",
                false,
                true,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                new LoyaltyPoints(10)
        );

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                        .isThrownBy(customer::archive);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeEmail("email@gmail.com"));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changeName(new FullName("John", "Doe")));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> customer.changePhone("123-12-1234"));
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1991, 10, 10),
                "john.doe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                OffsetDateTime.now()
        );

        customer.addLoyaltyPoints(new LoyaltyPoints(20));
        customer.addLoyaltyPoints(new LoyaltyPoints(10));

        assertThat(customer.loyaltyPoints().value()).isEqualTo(30);
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1991, 10, 10),
                "john.doe@email.com",
                "478-256-2504",
                "255-08-0578",
                true,
                OffsetDateTime.now()
        );

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(LoyaltyPoints.ZERO));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }

}