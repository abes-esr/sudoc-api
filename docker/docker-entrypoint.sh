#!/bin/bash

export HOSTNAME=${HOSTNAME}

echo "Démarrage de jstatd..."
jstatd -J-Djava.security.policy=jstatd.all.policy -J-Djava.security.manager=allow -J-Djava.rmi.server.hostname=${HOSTNAME} &
JSTATD_PID=$!

echo "Démarrage de l'application Java..."
java -XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:ConcGCThreads=5 -XX:+ExitOnOutOfMemoryError -XX:MaxGCPauseMillis=100 -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=${HOSTNAME} -jar /app/sudoc.jar
JAVA_APP_PID=$!

# Attendre que les processus se terminent
wait $JAVA_APP_PID
kill $JSTATD_PID