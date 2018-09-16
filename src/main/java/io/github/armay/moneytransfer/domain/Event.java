package io.github.armay.moneytransfer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Event {

    private static final String FORMAT = "{ \"id\": \"%s\", \"pan\": \"%s\", \"value\": %s,"
        + " \"description\": \"%s\", \"createdAt\": \"%s\", \"transferId\": \"%s\" }";
    private static final String HASH_TEMPLATE = "{ \"pan\": \"%s\", \"value\": %s, \"createdAt\": %d, \"transferId\": \"%s\" }";

    @NotNull
    private final String id;
    @NotNull
    private final String pan;
    @NotNull
    private final BigDecimal value;
    @NotNull
    private final String description;
    @NotNull
    private final ZonedDateTime createdAt;
    @NotNull
    private final String transferId;

    @JsonCreator
    public Event(
        @NotNull @JsonProperty("pan") String pan,
        @NotNull @JsonProperty("value") BigDecimal value,
        @NotNull @JsonProperty("description") String description,
        @NotNull @JsonProperty("transferId") String transferId,
        @NotNull @JsonProperty("createdAt") ZonedDateTime createdAt
    ) {
        this.pan = pan;
        this.value = value;
        this.description = description;
        this.transferId = transferId;
        this.createdAt = createdAt;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(String.format(
                HASH_TEMPLATE,
                this.pan,
                this.value.toPlainString(),
                this.createdAt.toEpochSecond(),
                this.transferId
            ).getBytes(StandardCharsets.UTF_8));
            id = Hex.toHexString(sha1.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getPan() {
        return pan;
    }

    @NotNull
    public BigDecimal getValue() {
        return value;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @NotNull
    public String getTransferId() {
        return transferId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Event)) return false;
        return Objects.equals(this.id, ((Event) that).id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format(
            FORMAT,
            id,
            pan,
            value.toPlainString(),
            description,
            createdAt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
            transferId
        );
    }

}
