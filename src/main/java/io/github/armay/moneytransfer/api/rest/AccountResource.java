package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.domain.Account;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AccountResource extends Resource {

    Optional<Account> findByPhone(@NotNull String phone);

    Optional<Account> findByPan(@NotNull String pan);

}
