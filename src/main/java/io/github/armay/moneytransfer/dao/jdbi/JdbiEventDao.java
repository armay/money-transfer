package io.github.armay.moneytransfer.dao.jdbi;

import io.github.armay.moneytransfer.dao.EventDao;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.api.exception.CardNotFoundException;
import io.github.armay.moneytransfer.api.exception.InsufficientFundsException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public final class JdbiEventDao implements EventDao {

    private static final String SELECT_EVENT_BY_ID = "SELECT * FROM EVENT WHERE ID = :id";
    private static final String SELECT_EVENTS_BY_PAN = "SELECT * FROM EVENT WHERE PAN = :pan ORDER BY CREATED_AT DESC";
    private static final String SELECT_EVENTS_BY_TRANSFER_ID = "SELECT * FROM EVENT WHERE TRANSFER_ID = :transfer_id"
        + " ORDER BY CREATED_AT DESC";
    private static final String CREATE_EVENT = "INSERT INTO EVENT (ID, PAN, VALUE, DESCRIPTION, CREATED_AT, TRANSFER_ID)"
        + " VALUES (:id, :pan, :value, :description, :createdAt, :transferId)";
    private static final String SELECT_BALANCE_FOR_UPDATE = "SELECT BALANCE FROM CARD WHERE PAN = :pan FOR UPDATE";
    private static final String INCREMENT_BALANCE = "UPDATE CARD SET BALANCE = BALANCE + :value WHERE PAN = :pan";

    private Jdbi jdbi;

    public JdbiEventDao(@NotNull Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    @Override
    public Optional<Event> findById(@NotNull String id) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_EVENT_BY_ID)
                .bind("id", id)
                .registerRowMapper(ConstructorMapper.factory(Event.class))
                .mapTo(Event.class)
                .findFirst()
        );
    }

    @Override
    public List<Event> findByPan(@NotNull String pan) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_EVENTS_BY_PAN)
                .bind("pan", pan)
                .registerRowMapper(ConstructorMapper.factory(Event.class))
                .mapTo(Event.class)
                .list()
        );
    }

    @Override
    public List<Event> findByTransferId(@NotNull String transferId) {
        return jdbi.withHandle(handle ->
            handle.createQuery(SELECT_EVENTS_BY_TRANSFER_ID)
                .bind("transfer_id", transferId)
                .registerRowMapper(ConstructorMapper.factory(Event.class))
                .mapTo(Event.class)
                .list()
        );
    }

    @Override
    public void create(@NotNull Event event) {
        jdbi.useTransaction(handle -> {
            BigDecimal balance = handle.createQuery(SELECT_BALANCE_FOR_UPDATE)
                .bind("pan", event.getPan())
                .mapTo(BigDecimal.class)
                .findFirst()
                .orElseThrow(() -> new CardNotFoundException(event.getPan()));
            if (balance.add(event.getValue()).compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientFundsException(event);
            }
            handle.createUpdate(INCREMENT_BALANCE)
                .bind("value", event.getValue())
                .bind("pan", event.getPan())
                .execute();
            handle.createUpdate(CREATE_EVENT)
                .bindBean(event)
                .execute();
        });
    }

}
