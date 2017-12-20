vertx.createHttpServer().requestHandler({ request ->
    request.response().end("!!! Hail Hydra !!!")
}).listen(8080)
