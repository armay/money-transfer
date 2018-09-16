package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.dao.CardDao;
import io.github.armay.moneytransfer.domain.Card;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class AbstractCardResource implements CardResource {

    private CardDao dao;

    protected AbstractCardResource(@NotNull CardDao dao) {
        this.dao = dao;
    }

    @Override
    public Optional<Card> findByPan(@NotNull String pan) {
        return dao.findByPan(pan);
    }

    @Override
    public Optional<Card> findByPhone(@NotNull String phone) {
        return dao.findByPhone(phone);
    }

}
