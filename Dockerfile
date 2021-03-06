###
# vert.x docker example using a Groovy verticle
# To build:
#  docker build -t hydra/game .
# To run:
#   docker run -t -i -p 4321:4321 hydra/game
###

# Extend vert.x image                       <1>
FROM vertx/vertx3

# Set the name of the verticle to deploy    <2>
ENV VERTICLE_NAME com.hydra.TheGameVerticle

# Set the location of the verticles         <3>
ENV VERTICLE_HOME /usr/verticles

EXPOSE 4321

# Copy your verticle to the container       <4>
COPY target/mock-game-0.0.1-SNAPSHOT.jar $VERTICLE_HOME/
COPY webroot/ $VERTICLE_HOME/webroot/

# Launch the verticle                       <5>
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]