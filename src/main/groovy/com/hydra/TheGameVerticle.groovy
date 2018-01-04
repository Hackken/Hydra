package com.hydra

import io.vertx.core.AbstractVerticle
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.StaticHandler

/**
 * @author pawel.szetela
 * @since 21/12/2017
 */
class TheGameVerticle extends AbstractVerticle{

    @Override
    void start() throws Exception {
        def server = vertx.createHttpServer()
        def router = Router.router(vertx)

        router.route('/hydra/launch/').handler({ routingContext ->

            // This handler will be called for every request
            def response = routingContext.response()
            response.putHeader("content-type", "text/plain")

            // Write to the response and end it
            response.end("!!! Hail Hydra !!!")
        })

        router.route('/hydra/pariplay/launch/').handler({ routingContext ->

            // This handler will be called for every request
            def response = routingContext.response()
            response.putHeader("content-type", "text/plain")


            // Write to the response and end it
            response.end("!!!Pariplay Hail Hydra !!!")
        })

        router.route().handler(StaticHandler.create().setCachingEnabled(false))

        server.requestHandler(router.&accept).listen(4321)
    }
}
