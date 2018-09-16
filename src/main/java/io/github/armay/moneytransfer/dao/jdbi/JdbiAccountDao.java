package io.github.armay.moneytransfer.dao.jdbi;

import io.github.armay.moneytransfer.dao.AccountDao;
import io.github.armay.moneytransfer.domain.Account;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class JdbiAccountDao implements AccountDao {

    private static final String SELECT_ACCOUNT_BY_PHONE = "SELECT * FROM ACCOUNT WHERE PHONE = :phone";
    private static final String SELECT_ACCOUNT_BY_PAN = "SELECT a.* FROM ACCOUNT a JOIN CARD c ON a.ID = c.ACCOUNT_ID WHERE c.PAN = :pan";

    private Jdbi jdbi;

    public JdbiAccountDao(@NotNull Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Optional<Account> findByPhone(@NotNull String phone) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_ACCOUNT_BY_PHONE)
                .bind("phone", phone)
                .mapToBean(Account.class)
                .findFirst()
        );
    }

    @Override
    public Optional<Account> findByPan(@NotNull String pan) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_ACCOUNT_BY_PAN)
                .bind("pan", pan)
                .mapToBean(Account.class)
                .findFirst()
        );
    }

}
