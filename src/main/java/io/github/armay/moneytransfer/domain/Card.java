package io.github.armay.moneytransfer.domain;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Objects;

public final class Card {

    private static final String FORMAT = "{ \"pan\": \"%s\", \"balance\": %s, \"account\": %s }";

    private String pan;
    private BigDecimal balance;
    private Account account;

    public Card() {}

    @NotNull
    public String getPan() {
        return pan;
    }

    public void setPan(@NotNull String pan) {
        this.pan = pan;
    }

    @NotNull
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(@NotNull BigDecimal balance) {
        this.balance = balance;
    }

    @NotNull
    public Account getAccount() {
        return account;
    }

    public void setAccount(@NotNull Account account) {
        this.account = account;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Card)) return false;
        return Objects.equals(this.pan, ((Card) that).pan);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pan);
    }

    @Override
    public String toString() {
        return String.format(
            FORMAT,
            pan,
            balance,
            account
        );
    }

}
