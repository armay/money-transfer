package io.github.armay.moneytransfer.api.rest;

import io.github.armay.moneytransfer.api.core.TransferService;
import io.github.armay.moneytransfer.dao.EventDao;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractEventResource implements EventResource {

    private EventDao dao;
    private TransferService service;

    protected AbstractEventResource(@NotNull EventDao dao, @NotNull TransferService service) {
        this.dao = dao;
        this.service = service;
    }

    @Override
    public Optional<Event> findById(@NotNull String id) {
        return dao.findById(id);
    }

    @Override
    public List<Event> findByPan(@NotNull String pan) {
        return dao.findByPan(pan);
    }

    @Override
    public List<Event> findByTransferId(@NotNull String transferId) {
        return dao.findByTransferId(transferId);
    }

    @Override
    public CompletableFuture<Event> sendTransfer(@NotNull Transfer transfer) {
        return service.send(transfer);
    }

    @Override
    public CompletableFuture<Event> receiveTransfer(@NotNull Transfer transfer) {
        return service.receive(transfer);
    }

}
