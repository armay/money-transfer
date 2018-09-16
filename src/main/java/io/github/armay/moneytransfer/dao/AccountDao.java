package io.github.armay.moneytransfer.dao;

import io.github.armay.moneytransfer.domain.Account;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AccountDao {

    Optional<Account> findByPhone(@NotNull String phone);

    Optional<Account> findByPan(@NotNull String pan);

}
