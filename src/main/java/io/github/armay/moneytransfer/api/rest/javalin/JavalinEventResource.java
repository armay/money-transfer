package io.github.armay.moneytransfer.api.rest.javalin;

import io.github.armay.moneytransfer.api.core.TransferService;
import io.github.armay.moneytransfer.dao.EventDao;
import io.github.armay.moneytransfer.api.rest.AbstractEventResource;
import io.github.armay.moneytransfer.domain.Event;
import io.github.armay.moneytransfer.domain.Transfer;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public final class JavalinEventResource extends AbstractEventResource implements JavalinConfiguration {

    private static Logger LOG = LoggerFactory.getLogger(JavalinEventResource.class);

    public JavalinEventResource(@NotNull EventDao dao, @NotNull TransferService service) {
        super(dao, service);
    }

    @Override
    public void configure(@NotNull Javalin http) {
        http.routes(() -> path("events", () -> {
            get(ctx -> {
                String transferId = ctx.queryParam("transfer_id");
                if (transferId != null && !transferId.isEmpty()) {
                    ctx.json(findByTransferId(transferId));
                } else {
                    ctx.result("[]");
                }
            });
            post(ctx -> {
                Transfer transfer = ctx.bodyAsClass(Transfer.class);
                String action = ctx.queryParam("action", "");
                assert action != null;
                switch (action) {
                    case "send": ctx.result(sendTransfer(transfer).thenApplyAsync(JavalinJackson.INSTANCE::toJson));
                        break;
                    case "receive": ctx.result(receiveTransfer(transfer).thenApplyAsync(JavalinJackson.INSTANCE::toJson));
                        break;
                    default: ctx.result(sendTransfer(transfer).thenApplyAsync(JavalinJackson.INSTANCE::toJson));
                        break;
                }
            });
            path(":id", () -> {
                get(ctx -> {
                    String id = ctx.pathParam("id");
                    Optional<Event> event = findById(id);
                    if (event.isPresent()) {
                        ctx.json(event.get());
                    } else {
                        ctx.result("{}");
                    }
                });
            });
        })).exception(Exception.class, (e, ctx) -> {
            ctx.json(new ExceptionWrapper(e));
        });
    }

}
