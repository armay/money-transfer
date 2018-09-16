package io.github.armay.moneytransfer.api.rest.javalin;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;

public interface JavalinConfiguration {

    void configure(@NotNull Javalin http);

}
