package io.github.armay.moneytransfer.api.core;

import io.github.armay.moneytransfer.domain.Account;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface TransferService {

    void alert(@NotNull Account account, Event event);

    CompletableFuture<Event> send(@NotNull Transfer transfer);

    CompletableFuture<Event> receive(@NotNull Transfer transfer);

}
