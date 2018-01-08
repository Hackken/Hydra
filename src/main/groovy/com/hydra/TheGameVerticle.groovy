package com.hydra

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.handler.sockjs.SockJSHandler

import java.util.logging.Logger

/**
 * @author pawel.szetela
 * @since 21/12/2017
 */
class TheGameVerticle extends AbstractVerticle{

    private static final Logger log = Logger.getLogger("TheGameVerticle");

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

        router.route().handler(BodyHandler.create());
//        router.post('/hydra/pariplay/launch/LaunchGame').handler(new PariplayLaunchHandlers(vertx: vertx).pariplayLaunchHandler)
        router.route().method(HttpMethod.POST).path("/hydra/pariplay/launch/LaunchGame").handler(new PariplayLaunchHandlers(vertx: vertx).pariplayLaunchHandler)

        server.requestHandler(router.&accept).listen(4321)
    }

    def createEventBridge(def router) {
        def opts = [
                inboundPermitteds:[
                        [
                                addressRegex:"hydra\\.game\\.in.*"
                        ]
                ],
                outboundPermitteds:[
                        [
                                addressRegex:"hydra\\.game\\.out.*"
                        ]
                ]
        ]

        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(opts))
    }


}
