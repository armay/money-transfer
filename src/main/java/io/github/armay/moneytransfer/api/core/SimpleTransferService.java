package io.github.armay.moneytransfer.api.core;

import io.github.armay.moneytransfer.api.exception.*;
import io.github.armay.moneytransfer.dao.AccountDao;
import io.github.armay.moneytransfer.dao.EventDao;
import io.github.armay.moneytransfer.domain.Account;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.StampedLock;

public final class SimpleTransferService implements TransferService {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleTransferService.class);

    private AccountDao accountDao;
    private EventDao eventDao;

    private ConcurrentHashMap<String, StampedLock> sendLocks;
    private ConcurrentHashMap<String, StampedLock> receiveLocks;
    private ExecutorService senders;
    private ExecutorService receivers;

    public SimpleTransferService(
        @NotNull AccountDao accountDao,
        @NotNull EventDao eventDao,
        @NotNull Integer sendersPoolSize,
        @NotNull Integer receiversPoolSize
    ) {
        this.accountDao = accountDao;
        this.eventDao = eventDao;
        this.senders = Executors.newFixedThreadPool(sendersPoolSize);
        this.receivers = Executors.newFixedThreadPool(receiversPoolSize);
        this.sendLocks = new ConcurrentHashMap<>(sendersPoolSize * 2);
        this.receiveLocks = new ConcurrentHashMap<>(receiversPoolSize * 2);
    }

    private Account findAccount(@NotNull String pan) {
        return accountDao.findByPan(pan).orElseThrow(() -> new CardNotFoundException(pan));
    }

    private void checkForDuplicates(@NotNull Transfer transfer) {
        if (!eventDao.findByTransferId(transfer.getId()).isEmpty()) {
            throw new DuplicateTransferException(transfer);
        }
    }

    private void checkForDuplicates(@NotNull Event event) {
        if (eventDao.findById(event.getId()).isPresent()) {
            throw new DuplicateEventException(event);
        }
    }

    private long createEvent(
        @NotNull Transfer transfer,
        @NotNull Event event,
        @NotNull StampedLock lock,
        long stamp
    ) {
        stamp = lock.tryConvertToWriteLock(stamp);
        if (stamp != 0) {
            try {
                eventDao.create(event);
            } finally {
                stamp = lock.tryConvertToOptimisticRead(stamp);
            }
        } else {
            throw new OptimisticLockException(transfer);
        }
        return stamp;
    }

    @Override
    public void alert(@NotNull Account account, @NotNull Event event) {
        LOG.info("Alert: {}, {}", account, event);
    }

    @Override
    public CompletableFuture<Event> send(@NotNull Transfer transfer) {
        LOG.info("Outgoing transfer: {}", transfer);
        return CompletableFuture.supplyAsync(() -> {
            Event debit;
            StampedLock lock = sendLocks.computeIfAbsent(transfer.getId(), it -> new StampedLock());
            long stamp = lock.tryOptimisticRead();
            try {
                Account sender = findAccount(transfer.getSenderPan());
                checkForDuplicates(transfer);
                debit = new Event(
                    transfer.getSenderPan(),
                    transfer.getValue().negate(),
                    transfer.getMessage(),
                    transfer.getId(),
                    ZonedDateTime.now(ZoneOffset.UTC)
                );
                stamp = createEvent(transfer, debit, lock, stamp);
                alert(sender, debit);
            } catch (Exception e) {
                LOG.error("Transfer failed: ", transfer, e);
                throw e;
            } finally {
                if (lock.validate(stamp)) {
                    sendLocks.remove(transfer.getId());
                }
            }
            if (transfer.isInternal()) {
                LOG.info("Internal transfer: {}", transfer);
                receivers.submit(() -> receive(transfer));
            } else {
                LOG.info("External transfer: ", transfer);
            }
            return debit;
        }, senders);
    }

    @Override
    public CompletableFuture<Event> receive(@NotNull Transfer transfer) {
        LOG.info("Incoming transfer: {}", transfer);
        return CompletableFuture.supplyAsync(() -> {
            Event credit;
            StampedLock lock = receiveLocks.computeIfAbsent(transfer.getId(), it -> new StampedLock());
            long stamp = lock.tryOptimisticRead();
            try {
                Account receiver = findAccount(transfer.getReceiverPan());
                credit = new Event(
                    transfer.getReceiverPan(),
                    transfer.getValue(),
                    transfer.getMessage(),
                    transfer.getId(),
                    ZonedDateTime.now(ZoneOffset.UTC)
                );
                checkForDuplicates(credit);
                stamp = createEvent(transfer, credit, lock, stamp);
                alert(receiver, credit);
            } catch (CardNotFoundException e) {
                if (transfer.isInternal()) {
                    LOG.info("Internal refund: {}. Reason: ", transfer, e);
                    Account sender = accountDao.findByPan(transfer.getSenderPan()).orElseThrow(() ->
                        new CardNotFoundException(transfer.getSenderPan()));
                    Event refund = new Event(
                        transfer.getSenderPan(),
                        transfer.getValue(),
                        transfer.getMessage(),
                        transfer.getId(),
                        ZonedDateTime.now(ZoneOffset.UTC)
                    );
                    eventDao.create(refund);
                    alert(sender, refund);
                } else {
                    LOG.info("External refund: {}", transfer, e);
                }
                throw new RefundException(transfer);
            } catch (Exception e) {
                LOG.error("Transfer failed: {}. Reason: ", transfer, e);
                throw e;
            } finally {
                if (lock.validate(stamp)) {
                    receiveLocks.remove(transfer.getId());
                }
            }
            return credit;
        }, receivers);
    }

}
