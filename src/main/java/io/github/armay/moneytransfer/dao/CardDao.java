package io.github.armay.moneytransfer.dao;

import io.github.armay.moneytransfer.domain.Card;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface CardDao {

    Optional<Card> findByPan(@NotNull String pan);

    Optional<Card> findByPhone(@NotNull String phone);

}
