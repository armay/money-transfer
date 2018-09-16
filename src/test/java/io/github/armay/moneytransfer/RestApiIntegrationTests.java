package io.github.armay.moneytransfer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.github.armay.moneytransfer.domain.Account;
import io.github.armay.moneytransfer.domain.Card;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.junit.jupiter.api.*;
import org.opentest4j.AssertionFailedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import static java.net.URLEncoder.encode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("REST API Integration")
final class RestApiIntegrationTests {

    private static final Logger LOG = LoggerFactory.getLogger(RestApiIntegrationTests.class);
    private static final String UTF8 = "UTF-8";

    private static MoneyTransferApp app;
    private static String baseUrl;
    private static Jdbi jdbi;

    @BeforeAll
    static void setupAll() throws IOException {
        app = new MoneyTransferApp();
        app.start();

        Properties properties = MoneyTransferApp.readProperties();
        baseUrl = String.format("http://localhost:%s", properties.getProperty("http.port"));

        jdbi = Jdbi.create(properties.getProperty("database.url"));
        List<Account> accounts = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM ACCOUNT")
            .mapToBean(Account.class).list());
        List<Card> cards = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM CARD")
            .mapToBean(Card.class).list());
        List<Event> events = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM EVENT")
            .registerRowMapper(ConstructorMapper.factory(Event.class))
            .mapTo(Event.class).list());
        LOG.info("Initial data: \n - Accounts: {} \n - Cards: {} \n - Events: {}", accounts, cards, events);

        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Unirest.setDefaultHeader("Accept", "application/json");
    }

    @BeforeEach
    void setup() {
    }

    @AfterAll
    static void cleanupAll() throws IOException {
        app.stop();
        Unirest.shutdown();
    }

    @AfterEach
    void cleanup() {}

    @Test
    @DisplayName("GET: /accounts?[phone or pan]")
    void getAccounts() throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/accounts";
        String phone = "+71234567890";
        String pan = "6123456789012345";

        Account alice = Unirest.get(url)
            .queryString("phone", phone)
            .asObject(Account.class).getBody();
        assertNotNull(alice);
        assertEquals("Alice", alice.getName());
        LOG.info("GET: {}?phone={} -> {}", url, encode(phone, UTF8), alice);

        Account bob = Unirest.get(baseUrl + "/accounts")
            .queryString("pan", pan)
            .asObject(Account.class).getBody();
        assertNotNull(bob);
        assertEquals("Bob", bob.getName());
        LOG.info("GET: {}?pan={} -> {}", url, pan, bob);
    }

    @Test
    @DisplayName("GET: /cards?[phone]")
    void getCards() throws UnirestException, UnsupportedEncodingException {
        String url = baseUrl + "/cards";
        String phone = "+71234567890";

        Card alice = Unirest.get(url)
            .queryString("phone", phone)
            .asObject(Card.class).getBody();
        assertNotNull(alice);
        assertEquals("1234567890123456", alice.getPan());
        assertEquals("Alice", alice.getAccount().getName());
        LOG.info("GET: {}?phone={} -> {}", url, encode(phone, UTF8), alice);
    }

    @Test
    @DisplayName("GET: /cards/:pan")
    void getCardByPan() throws UnirestException {
        String url = baseUrl + "/cards/{pan}";
        String pan = "1234567890123456";

        Card alice = Unirest.get(url)
            .routeParam("pan", pan)
            .asObject(Card.class).getBody();
        assertNotNull(alice);
        assertEquals("1234567890123456", alice.getPan());
        assertEquals("Alice", alice.getAccount().getName());
        LOG.info("GET: {} -> {}", url.replace("{pan}", pan), alice);
    }

    @Test
    @DisplayName("GET: /events?[transfer_id]")
    void getEvents() throws UnirestException {
        String url = baseUrl + "/events";
        String transferId = "96e69a45afafe95e9daf1ce120280e9975281de4";

        Event[] events = Unirest.get(url)
            .queryString("transfer_id", transferId)
            .asObject(Event[].class).getBody();
        LOG.info("GET: {}?transfer_id={} -> {}", url, transferId, events);
    }

    @Test
    @DisplayName("GET: /events/:id")
    void getEventById() throws UnirestException {
        String url = baseUrl + "/events/{id}";
        String id = "dde476be9322785985ac4c660ad53d84dc30ad06";

        Event event = Unirest.get(url)
            .routeParam("id", id)
            .asObject(Event.class).getBody();
        assertNotNull(event);
        assertEquals(id, event.getId());
        assertEquals("1234567890123456", event.getPan());
        LOG.info("GET: {} -> {}", url.replace("{id}", id), event);
    }

    @Test
    @DisplayName("POST: /events?[action]")
    void postEvent() throws UnirestException {
        String url = baseUrl + "/events";
        Boolean internal = true;
        String senderPan = "1234567890123456";
        String receiverPan = "6123456789012345";
        String message = "Hello";
        BigDecimal value = new BigDecimal("10.00");
        ZonedDateTime createdAt = ZonedDateTime.now(ZoneOffset.UTC);

        Transfer transfer = new Transfer(internal, senderPan, receiverPan, message, value, createdAt);
        String cardsUrl = baseUrl + "/cards/{pan}";
        Card alice = Unirest.get(cardsUrl)
            .routeParam("pan", transfer.getSenderPan())
            .asObject(Card.class).getBody();
        Card bob = Unirest.get(cardsUrl)
            .routeParam("pan", transfer.getReceiverPan())
            .asObject(Card.class).getBody();

        Future<HttpResponse<Event>> future = Unirest.post(url)
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .body(transfer)
            .asObjectAsync(Event.class, new Callback<Event>() {

            public void failed(UnirestException e) {
                fail("Request failed: ", e);
            }

            public void completed(HttpResponse<Event> response) {
                Event event = response.getBody();
                assertNotNull(event);
                assertEquals(transfer.getId(), event.getTransferId());
                assertEquals(transfer.getSenderPan(), event.getPan());
                assertEquals(0, event.getValue().compareTo(transfer.getValue().negate()));
                LOG.info("POST: {} - body = {} -> {}", url, transfer, event);
            }

            public void cancelled() {
                fail("Request cancelled");
            }

        });

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Unexpected exception", e);
        }

        if (future.isDone()) {
            try {
                BigDecimal expected = alice.getBalance().add(transfer.getValue().negate());
                BigDecimal actual = Unirest.get(cardsUrl)
                    .routeParam("pan", transfer.getSenderPan())
                    .asObject(Card.class).getBody().getBalance();
                assertNotNull(actual);
                assertEquals(0, expected.compareTo(actual));
            } catch (UnirestException| AssertionFailedError e) {
                fail("Balance check failed for sender: ", e);
            }

            try {
                BigDecimal expected = bob.getBalance().add(transfer.getValue());
                BigDecimal actual = Unirest.get(cardsUrl)
                    .routeParam("pan", transfer.getReceiverPan())
                    .asObject(Card.class).getBody().getBalance();
                assertNotNull(actual);
                assertEquals(0, expected.compareTo(actual));
            } catch (UnirestException| AssertionFailedError e) {
                fail("Balance check failed for receiver: ", e);
            }
        } else {
            fail("Timeout");
        }

        List<Account> accounts = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM ACCOUNT")
                .mapToBean(Account.class).list());
        List<Card> cards = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM CARD")
                .mapToBean(Card.class).list());
        List<Event> events = jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM EVENT")
                .registerRowMapper(ConstructorMapper.factory(Event.class))
                .mapTo(Event.class).list());
        LOG.info("Actual data: \n - Accounts: {} \n - Cards: {} \n - Events: {}", accounts, cards, events);
    }

}
