package io.github.armay.moneytransfer.api.exception;

import io.github.armay.moneytransfer.domain.Transfer;

public final class RefundException extends TransferFailureException {

    public RefundException(Transfer transfer) {
        super("Refund: " + transfer);
    }

}
