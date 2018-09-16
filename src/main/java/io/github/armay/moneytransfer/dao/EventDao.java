package io.github.armay.moneytransfer.dao;

import io.github.armay.moneytransfer.domain.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public interface EventDao {

    Optional<Event> findById(@NotNull String id);

    List<Event> findByPan(@NotNull String pan);

    List<Event> findByTransferId(@NotNull String transferId);

    void create(@NotNull Event event);

}
