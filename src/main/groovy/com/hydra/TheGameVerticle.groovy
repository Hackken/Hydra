package com.hydra

import groovy.json.JsonOutput
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler

/**
 * @author pawel.szetela
 * @since 21/12/2017
 */
class TheGameVerticle extends AbstractVerticle{

    @Override
    void start() throws Exception {
        def server = vertx.createHttpServer(
//                [
//                useAlpn:true,
//                ssl:true,
//                pemKeyCertOptions:[
//                        keyPath:"server-key.pem",
//                        certPath:"server-cert.pem"
//                ]
//        ]
        )

        def router = Router.router(vertx)
        router.route().handler(StaticHandler.create().setCachingEnabled(false))

        createEventBridge(router)

        // Register to listen for messages coming IN to the server
        def eb = vertx.eventBus()
        eb.consumer("chat.to.server").handler({ message ->
            def timestamp = java.text.DateFormat.getDateTimeInstance(java.text.DateFormat.SHORT, java.text.DateFormat.MEDIUM).format(java.util.Date.from(java.time.Instant.now()))
            eb.publish("chat.to.client", "${timestamp}: ${message.body()}")
        })
        router.post('/hydra/pariplay/launch/LaunchGame').handler(pariplayLaunchHandler)

        server.requestHandler(router.&accept).listen(4321)
    }

    def createEventBridge(def router) {
        def opts = [
                inboundPermitteds:[
                        [
                                address:"chat.to.server"
                        ]
                ],
                outboundPermitteds:[
                        [
                                address:"chat.to.client"
                        ]
                ]
        ]

        def ebHandler = SockJSHandler.create(vertx).bridge(opts)
        router.route("/eventbus/*").handler(ebHandler)
    }

    Handler<RoutingContext> pariplayLaunchHandler = { routingContext ->
        def response = routingContext.response()
        response.putHeader("content-type", "application/json")

        response.end(
                JsonOutput.toJson([Url: "http://localhost:4321/hydra.html",
                                   Token: UUID.randomUUID().toString()])
        )
    }
}
