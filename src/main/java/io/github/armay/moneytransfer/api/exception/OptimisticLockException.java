package io.github.armay.moneytransfer.api.exception;

import io.github.armay.moneytransfer.domain.Transfer;

public final class OptimisticLockException extends TransferFailureException {

    public OptimisticLockException(Transfer transfer) {
        super("Optimistic lock exception: " + transfer);
    }

}
