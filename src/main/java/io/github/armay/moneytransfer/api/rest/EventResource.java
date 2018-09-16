package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface EventResource extends Resource {

    Optional<Event> findById(@NotNull String id);

    List<Event> findByPan(@NotNull String pan);

    List<Event> findByTransferId(@NotNull String transferId);

    CompletableFuture<Event> sendTransfer(@NotNull Transfer transfer);

    CompletableFuture<Event> receiveTransfer(@NotNull Transfer transfer);

}
