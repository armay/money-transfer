package io.github.armay.moneytransfer.api.exception;

public class TransferFailureException extends RuntimeException {

    public TransferFailureException() {
        super();
    }

    public TransferFailureException(String message) {
        super(message);
    }

    public TransferFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
