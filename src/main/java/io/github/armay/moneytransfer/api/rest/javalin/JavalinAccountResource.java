package io.github.armay.moneytransfer.api.rest.javalin;

import io.github.armay.moneytransfer.dao.AccountDao;
import io.github.armay.moneytransfer.api.rest.AbstractAccountResource;
import io.github.armay.moneytransfer.domain.Account;
import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public final class JavalinAccountResource extends AbstractAccountResource implements JavalinConfiguration {

    public JavalinAccountResource(@NotNull AccountDao dao) {
        super(dao);
    }

    @Override
    public void configure(@NotNull Javalin http) {
        http.routes(() -> path("accounts", () -> {
            get(ctx -> {
                String phone = ctx.queryParam("phone");
                String pan = ctx.queryParam("pan");
                if (phone != null && !phone.isEmpty()) {
                    Optional<Account> account = findByPhone(phone);
                    if (account.isPresent()) {
                        ctx.json(account.get());
                    } else {
                        ctx.result("{}");
                    }
                } else if (pan != null && !pan.isEmpty()) {
                    Optional<Account> account = findByPan(pan);
                    if (account.isPresent()) {
                        ctx.json(account.get());
                    } else {
                        ctx.result("{}");
                    }
                } else {
                    ctx.result("{}");
                }
            });
        }));
    }

}
