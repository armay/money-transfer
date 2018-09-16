package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.dao.AccountDao;
import io.github.armay.moneytransfer.domain.Account;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractAccountResource implements AccountResource {

    private AccountDao dao;

    protected AbstractAccountResource(@NotNull AccountDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<Account> findByPhone(@NotNull String phone) {
        return dao.findByPhone(phone);
    }

    @Override
    public Optional<Account> findByPan(@NotNull String pan) {
        return dao.findByPan(pan);
    }

}
