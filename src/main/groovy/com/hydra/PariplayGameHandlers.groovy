package com.hydra

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.vertx.core.Handler
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.Message
import io.vertx.ext.web.client.WebClient

import java.util.logging.Logger

/**
 * @author pawel.szetela
 * @since 06/01/2018
 */
class PariplayGameHandlers {
    private static final Logger log = Logger.getLogger("PariplayGameHandlers");

    Vertx vertx
    WebClient webClient
    String token
    String gameCode

    int platformPort = 8080
    String platformHost = "localhost"

    public def registerHandlers(String address) {
        vertx.eventBus().consumer(address + ".balance").handler(balanceHandler)
        vertx.eventBus().consumer(address + ".debitCredit").handler(debitCreditHandler)
        vertx.eventBus().consumer(address + ".debit").handler(debitHandler)
        vertx.eventBus().consumer(address + ".credit").handler(creditHandler)
    }

    Handler<Message<String>> balanceHandler = { message ->
        log.info("Pariplay balance request: ${message.body()}")

        String apiUri = '/api/gameshub/pariplay/GetBalance'
        String payload = JsonOutput.toJson([
                            Account: [
                                UserName: "lottolandd",
                                Password: "lottoland123!"],
                           Token   : token]).toString()

        webClient.post(platformPort, platformHost, apiUri)
                .putHeader("content-type", "application/json")
                .putHeader("X-Unique-ID" , "HAPROXY_ID")
                .sendBuffer(Buffer.buffer(payload), { ar ->
                    if (ar.succeeded()) {
                        log.info("Pariplay balance response: ${ar.result().bodyAsString()}")
                        vertx.eventBus().send("hydra.game.out.${token}", ar.result().bodyAsString())
                    }
                    if (ar.failed()) {
                        log.info("Pariplay balance request error: ${ar.cause()}")
                        vertx.eventBus().send("hydra.game.out.${token}", ar.cause())
                    }
        })
    }

    Handler<Message<String>> creditHandler = { message ->
        log.info("Pariplay credit request: ${message.body()}")
    }

    Handler<Message<String>> debitHandler = { message ->
        log.info("Pariplay debit request: ${message.body()}")
    }

    Handler<Message<String>> debitCreditHandler = { message ->
        log.info("Pariplay debitCredit request: ${message.body()}")

        def debitCreditEvent = new JsonSlurper().parseText(message.body())

        String apiUri = '/api/gameshub/pariplay/DebitAndCredit'

        String payload = JsonOutput.toJson([
                RoundId: UUID.randomUUID().toString(),
                TransactionId: UUID.randomUUID().toString(),
                DebitAmount: debitCreditEvent.stake,
                CreditAmount: debitCreditEvent.win,
                CreditType: "NormalWin",
                Feature: "Normal",
                GameCode: gameCode,
                PlayerId: debitCreditEvent.playerId,
                Account: [
                        UserName: "lottolandd",
                        Password: "lottoland123!"],
                Token   : token]).toString()

        webClient.post(platformPort, platformHost, apiUri)
                .putHeader("content-type", "application/json")
                .putHeader("X-Unique-ID" , "HAPROXY_ID")
                .sendBuffer(Buffer.buffer(payload), { ar ->
            if (ar.succeeded()) {
                log.info("Pariplay balance response: ${ar.result().bodyAsString()}")
                vertx.eventBus().send("hydra.game.out.${token}", ar.result().bodyAsString())
            }
            if (ar.failed()) {
                log.info("Pariplay balance request error: ${ar.cause()}")
                vertx.eventBus().send("hydra.game.out.${token}", ar.cause())
            }
        })
    }
}
