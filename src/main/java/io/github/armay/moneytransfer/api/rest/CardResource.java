package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.domain.Card;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CardResource extends Resource {

    Optional<Card> findByPan(@NotNull String pan);

    Optional<Card> findByPhone(@NotNull String phone);

}
