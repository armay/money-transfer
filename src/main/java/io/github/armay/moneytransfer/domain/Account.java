package io.github.armay.moneytransfer.domain;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Account {

    private static final String FORMAT = "{ \"id\": %d, \"name\": \"%s\", \"phone\": \"%s\", \"primaryPan\": \"%s\" }";

    private Long id;
    private String name;
    private String phone;
    private String primaryPan;

    public Account() {}

    @NotNull
    public Long getId() {
        return id;
    }

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(@NotNull String phone) {
        this.phone = phone;
    }

    @NotNull
    public String getPrimaryPan() {
        return primaryPan;
    }

    public void setPrimaryPan(@NotNull String primaryPan) {
        this.primaryPan = primaryPan;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Account)) return false;
        return Objects.equals(this.phone, ((Account) that).phone);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(phone);
    }

    @Override
    public String toString() {
        return String.format(
            FORMAT,
            id,
            name,
            phone,
            primaryPan
        );
    }

}
