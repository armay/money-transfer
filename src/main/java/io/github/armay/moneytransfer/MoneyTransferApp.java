package io.github.armay.moneytransfer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.armay.moneytransfer.api.core.SimpleTransferService;
import io.github.armay.moneytransfer.api.rest.javalin.JavalinAccountResource;
import io.github.armay.moneytransfer.api.rest.javalin.JavalinCardResource;
import io.github.armay.moneytransfer.api.rest.javalin.JavalinConfiguration;
import io.github.armay.moneytransfer.api.rest.javalin.JavalinEventResource;
import io.github.armay.moneytransfer.dao.AccountDao;
import io.github.armay.moneytransfer.dao.CardDao;
import io.github.armay.moneytransfer.dao.EventDao;
import io.github.armay.moneytransfer.dao.jdbi.JdbiAccountDao;
import io.github.armay.moneytransfer.dao.jdbi.JdbiCardDao;
import io.github.armay.moneytransfer.dao.jdbi.JdbiEventDao;
import io.github.armay.moneytransfer.api.core.TransferService;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class MoneyTransferApp {

    private static final Logger LOG = LoggerFactory.getLogger(MoneyTransferApp.class);

    private Javalin http;
    private Properties properties;

    static String readResourceAsString(String resource) {
        String result;
        try(InputStream in = MoneyTransferApp.class.getClassLoader().getResourceAsStream(resource)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            result = out.toString(StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            LOG.error("Failed to read resource '{}'", resource, e);
            throw new RuntimeException(e);
        }
        return result;
    }

    static Properties readProperties() throws IOException {
        Properties result;
        try(InputStream in = MoneyTransferApp.class.getClassLoader().getResourceAsStream("application.properties")) {
            result = new Properties();
            result.load(in);
        } catch (IOException e) {
            LOG.error("Failed to load properties", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            MoneyTransferApp app = new MoneyTransferApp();
            app.start();
        } catch (IOException e) {
            LOG.error("Failed to start application", e);
        }
    }

    void start() throws IOException {
        properties = readProperties();

        Jdbi jdbi = Jdbi.create(properties.getProperty("database.url"));
        jdbi.useHandle(handle -> {
            String[] scripts = properties.getProperty("database.do_on_startup").split(",");
            for (String script : scripts) {
                String sql = readResourceAsString(script);
                handle.createScript(sql).execute();
            }
        });

        http = Javalin.create()
            .defaultContentType("application/json")
            .disableStartupBanner()
            .port(Integer.parseInt(properties.getProperty("http.port")));
        JavalinJackson.configure(new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        );

        AccountDao accountDao = new JdbiAccountDao(jdbi);
        JavalinConfiguration accountResource = new JavalinAccountResource(accountDao);
        accountResource.configure(http);

        CardDao cardDao = new JdbiCardDao(jdbi);
        JavalinConfiguration cardResource = new JavalinCardResource(cardDao);
        cardResource.configure(http);

        EventDao eventDao = new JdbiEventDao(jdbi);
        TransferService transferService = new SimpleTransferService(
            accountDao,
            eventDao,
            Integer.parseInt(properties.getProperty("transfer.senders")),
            Integer.parseInt(properties.getProperty("transfer.receivers"))
        );
        JavalinConfiguration eventResource = new JavalinEventResource(eventDao, transferService);
        eventResource.configure(http);

        http.start();

        LOG.info("Application started");
    }

    void stop() {
        http.stop();
        LOG.info("Application stopped");
    }

}
