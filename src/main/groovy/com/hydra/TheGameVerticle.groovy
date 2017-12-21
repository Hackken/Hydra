package com.hydra

import io.vertx.core.AbstractVerticle
/**
 * @author pawel.szetela
 * @since 21/12/2017
 */
class TheGameVerticle extends AbstractVerticle{

    @Override
    void start() throws Exception {
        vertx.createHttpServer().requestHandler({ request ->
            request.response().end("!!! Hail Hydra !!!")
        }).listen(4321);
    }
}
