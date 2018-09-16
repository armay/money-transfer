package io.github.armay.moneytransfer.dao.jdbi;

import io.github.armay.moneytransfer.dao.CardDao;
import io.github.armay.moneytransfer.domain.Account;
import io.github.armay.moneytransfer.domain.Card;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.result.RowView;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public final class JdbiCardDao implements CardDao {

    private static final String SELECT_CARD_BY_PAN = "SELECT c.PAN c_pan, c.BALANCE c_balance,"
        + " a.ID a_id, a.NAME a_name, a.PHONE a_phone, a.PRIMARY_PAN a_primary_pan"
        + " FROM CARD c JOIN ACCOUNT a ON c.ACCOUNT_ID = a.ID WHERE c.PAN = :pan";
    private static final String SELECT_CARD_BY_PHONE = "SELECT c.PAN c_pan, c.BALANCE c_balance,"
        + " a.ID a_id, a.NAME a_name, a.PHONE a_phone, a.PRIMARY_PAN a_primary_pan"
        + " FROM CARD c JOIN ACCOUNT a ON c.PAN = a.PRIMARY_PAN WHERE a.PHONE = :phone";

    private Jdbi jdbi;

    public JdbiCardDao(@NotNull Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Optional<Card> findByPan(@NotNull String pan) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_CARD_BY_PAN)
                .bind("pan", pan)
                .registerRowMapper(BeanMapper.factory(Card.class, "c"))
                .registerRowMapper(BeanMapper.factory(Account.class, "a"))
                .reduceRows((Map<String, Card> map, RowView rowView) -> {
                    Card card = map.computeIfAbsent(
                        rowView.getColumn("c_pan", String.class),
                        id -> rowView.getRow(Card.class)
                    );
                    card.setAccount(rowView.getRow(Account.class));
                })
                .findFirst()
        );
    }

    @Override
    public Optional<Card> findByPhone(@NotNull String phone) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_CARD_BY_PHONE)
                .bind("phone", phone)
                .registerRowMapper(BeanMapper.factory(Card.class, "c"))
                .registerRowMapper(BeanMapper.factory(Account.class, "a"))
                .reduceRows((Map<String, Card> map, RowView rowView) -> {
                    Card card = map.computeIfAbsent(
                        rowView.getColumn("c_pan", String.class),
                        id -> rowView.getRow(Card.class)
                    );
                    card.setAccount(rowView.getRow(Account.class));
                })
                .findFirst()
        );
    }

}
