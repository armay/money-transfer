package io.github.armay.moneytransfer.api.exception;

public final class CardNotFoundException extends TransferFailureException {

    public CardNotFoundException(String pan) {
        super("Card not found: " + pan);
    }

}
