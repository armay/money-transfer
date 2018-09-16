package io.github.armay.moneytransfer.api.exception;

import io.github.armay.moneytransfer.domain.Transfer;

public final class DuplicateTransferException extends TransferFailureException {

    public DuplicateTransferException(Transfer transfer) {
        super("Duplicate transfer: " + transfer);
    }

}
