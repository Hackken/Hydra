package com.hydra

import groovy.json.JsonOutput
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.client.WebClient

import java.util.logging.Logger
/**
 * @author pawel.szetela
 * @since 05/01/2018
 */
class PariplayLaunchHandlers {
    private static final Logger log = Logger.getLogger("PariplayLaunchHandlers");

    Vertx vertx

    Handler<RoutingContext> pariplayLaunchHandler = { routingContext ->

        String token = UUID.randomUUID().toString()
        String handlerAddress = "hydra.game.in.${token}"

        log.info("Launch game request ${routingContext.bodyAsString}")
        def payload = routingContext.bodyAsJson
        log.info("Register game handlers ${handlerAddress}")

        new PariplayGameHandlers(vertx: vertx, webClient: WebClient.create(vertx), token: token, gameCode: payload.GameCode)
                .registerHandlers(handlerAddress)

        def response = routingContext.response().putHeader("content-type", "application/json")
        response.end(
                JsonOutput.toJson([Url  : "http://localhost:4321/hydra.html?token=${token}&playerID=${payload.PlayerId}",
                                   Token: token])
        )
    }
}
