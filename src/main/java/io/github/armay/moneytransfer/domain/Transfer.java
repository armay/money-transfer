package io.github.armay.moneytransfer.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bouncycastle.util.encoders.Hex;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class Transfer {

    private static final String FORMAT = "{ \"id\": \"%s\", \"internal\": %b,"
        + " \"senderPan\": \"%s\", \"receiverPan\": \"%s\","
        + " \"message\": \"%s\", \"value\": %s, \"createdAt\": \"%s\" }";
    private static final String HASH_TEMPLATE = "{ \"internal\": %b,"
        + " \"senderPan\": \"%s\", \"receiverPan\": \"%s\","
        + " \"value\": %s, \"createdAt\": %d }";

    @NotNull
    private final String id;
    @NotNull
    private final Boolean internal;
    @NotNull
    private final String senderPan;
    @NotNull
    private final String receiverPan;
    @NotNull
    private final String message;
    @NotNull
    private final BigDecimal value;
    @NotNull
    private final ZonedDateTime createdAt;

    @JsonCreator
    public Transfer(
        @NotNull @JsonProperty("internal") Boolean internal,
        @NotNull @JsonProperty("senderPan") String senderPan,
        @NotNull @JsonProperty("receiverPan") String receiverPan,
        @NotNull @JsonProperty("message") String message,
        @NotNull @JsonProperty("value") BigDecimal value,
        @NotNull @JsonProperty("createdAt") ZonedDateTime createdAt
    ) {
        this.internal = internal;
        this.senderPan = senderPan;
        this.receiverPan = receiverPan;
        this.message = message;
        this.value = value;
        this.createdAt = createdAt;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(String.format(
                HASH_TEMPLATE,
                internal,
                senderPan,
                receiverPan,
                value.toPlainString(),
                createdAt.toEpochSecond()
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
    public Boolean isInternal() {
        return internal;
    }

    @NotNull
    public String getSenderPan() {
        return senderPan;
    }

    @NotNull
    public String getReceiverPan() {
        return receiverPan;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public BigDecimal getValue() {
        return value;
    }

    @NotNull
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (!(that instanceof Transfer)) return false;
        return Objects.equals(this.id, ((Transfer) that).id);
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
            internal,
            senderPan,
            receiverPan,
            message,
            value,
            createdAt.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
        );
    }

}
