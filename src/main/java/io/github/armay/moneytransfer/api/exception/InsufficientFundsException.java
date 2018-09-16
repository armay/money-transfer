package io.github.armay.moneytransfer.api.exception;

import io.github.armay.moneytransfer.domain.Event;

public final class InsufficientFundsException extends TransferFailureException {

    public InsufficientFundsException(Event event) {
        super("Insufficient funds: " + event);
    }

}
