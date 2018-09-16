package io.github.armay.moneytransfer.api.exception;

import io.github.armay.moneytransfer.domain.Event;

public final class DuplicateEventException extends TransferFailureException {

    public DuplicateEventException(Event event) {
        super("Duplicate event: " + event);
    }

}
