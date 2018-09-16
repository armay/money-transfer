package io.github.armay.moneytransfer.api.rest.javalin;

import io.github.armay.moneytransfer.dao.CardDao;
import io.github.armay.moneytransfer.api.rest.AbstractCardResource;
import io.github.armay.moneytransfer.domain.Card;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinCardResource extends AbstractCardResource implements JavalinConfiguration {

    public JavalinCardResource(@NotNull CardDao dao) {
        super(dao);
    }

    @Override
    public void configure(@NotNull Javalin http) {
        http.routes(() -> path("cards", () -> {
            get(ctx -> {
                String phone = ctx.queryParam("phone");
                if (phone != null && !phone.isEmpty()) {
                    Optional<Card> card = findByPhone(phone);
                    if (card.isPresent()) {
                        ctx.json(card.get());
                    } else {
                        ctx.result("{}");
                    }
                } else {
                    ctx.result("{}");
                }
            });
            path(":pan", () -> {
                get(ctx -> {
                    String pan = ctx.pathParam("pan");
                    Optional<Card> card = findByPan(pan);
                    if (card.isPresent()) {
                        ctx.json(card.get());
                    } else {
                        ctx.result("{}");
                    }
                });
            });
        }));
    }

}
