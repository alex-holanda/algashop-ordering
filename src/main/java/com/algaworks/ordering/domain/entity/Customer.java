package com.algaworks.ordering.domain.entity;

import com.algaworks.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.ordering.domain.validator.FieldValidations;
import com.algaworks.ordering.domain.valueobject.CustomerId;
import com.algaworks.ordering.domain.valueobject.FullName;
import com.algaworks.ordering.domain.valueobject.LoyaltyPoints;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.algaworks.ordering.domain.exception.ErrorMessages.*;


public class Customer {

    private CustomerId id;
    private FullName fullName;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String document;
    private Boolean promotionNotificationsAllowed;
    private Boolean archived;
    private OffsetDateTime registredAt;
    private OffsetDateTime archivedAt;
    private LoyaltyPoints loyaltyPoints;

    public Customer(CustomerId id, FullName fullName, LocalDate birthDate, String email, String phone, String document, Boolean promotionNotificationsAllowed, Boolean archived, OffsetDateTime registredAt, OffsetDateTime archivedAt, LoyaltyPoints loyaltyPoints) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(archived);
        this.setRegistredAt(registredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
    }

    public Customer(CustomerId id, FullName fullName, LocalDate birthDate, String email, String phone, String document, Boolean promotionNotificationsAllowed, OffsetDateTime registredAt) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setRegistredAt(registredAt);
        this.setArchived(false);
        this.setLoyaltyPoints(LoyaltyPoints.ZERO);
    }

    public void addLoyaltyPoints(LoyaltyPoints loyalPointsAdded) {
        verifyIfChangeable();
        this.setLoyaltyPoints(this.loyaltyPoints().add(loyalPointsAdded));
    }

    public void archive() {
        verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(OffsetDateTime.now());
        this.setFullName(new FullName("Anonymous", "Anonymous"));
        this.setPhone("000-000-0000");
        this.setDocument("000-00-0000");
        this.setEmail(UUID.randomUUID()+"@anonymous.com");
        this.setBirthDate(null);
        this.setPromotionNotificationsAllowed(false);
    }

    public void enablePromotionNotifications() {
        verifyIfChangeable();
        this.setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications() {
        verifyIfChangeable();
        this.setPromotionNotificationsAllowed(false);
    }

    public void changeName(FullName fullName) {
        verifyIfChangeable();
        this.setFullName(fullName);
    }

    public void changeEmail(String email) {
        verifyIfChangeable();
        this.setEmail(email);
    }

    public void changePhone(String phone) {
        verifyIfChangeable();
        this.setPhone(phone);
    }

    public CustomerId id() {
        return id;
    }

    public FullName fullName() {
        return fullName;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public String document() {
        return document;
    }

    public Boolean isPromotionNotificationsAllowed() {
        return promotionNotificationsAllowed;
    }

    public Boolean isArchived() {
        return archived;
    }

    public OffsetDateTime registredAt() {
        return registredAt;
    }

    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints;
    }

    private void setId(CustomerId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setFullName(FullName fullName) {
        Objects.requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
        this.fullName = fullName;
    }

    private void setBirthDate(LocalDate birthDate) {
        if (Objects.isNull(birthDate)) {
            this.birthDate = null;
            return;
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
        this.birthDate = birthDate;
    }

    private void setEmail(String email) {
        FieldValidations.requiresValidEmail(email, VALIDATION_ERROR_EMAIL_IS_INVALID);
        this.email = email;
    }

    private void setPhone(String phone) {
        Objects.requireNonNull(phone);
        if (phone.isBlank()) {
            throw new IllegalArgumentException(VALIDATION_ERROR_PHONE_IS_NULL);
        }
        this.phone = phone;
    }

    private void setDocument(String document) {
        Objects.requireNonNull(document);
        if (document.isBlank()) {
            throw new IllegalArgumentException(VALIDATION_ERROR_DOCUMENT_IS_NULL);
        }
        this.document = document;
    }

    private void setPromotionNotificationsAllowed(Boolean promotionNotificationsAllowed) {
        Objects.requireNonNull(promotionNotificationsAllowed);
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
    }

    private void setArchived(Boolean archived) {
        Objects.requireNonNull(archived);
        this.archived = archived;
    }

    private void setRegistredAt(OffsetDateTime registredAt) {
        Objects.requireNonNull(registredAt);
        this.registredAt = registredAt;
    }

    private void setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(LoyaltyPoints loyaltyPoints) {
        Objects.requireNonNull(loyaltyPoints);
        this.loyaltyPoints = loyaltyPoints;
    }

    private void verifyIfChangeable() {
        if (Boolean.TRUE.equals(this.isArchived())) {
            throw new CustomerArchivedException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
