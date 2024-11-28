#!/bin/bash

export HOSTNAME=${HOSTNAME}
java -XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:ConcGCThreads=5 -XX:+ExitOnOutOfMemoryError -XX:MaxGCPauseMillis=100 -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.port=9090 -Dcom.sun.management.jmxremote.rmi.port=9090 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.local.only=false -Djava.rmi.server.hostname=${HOSTNAME} -jar /app/sudoc.jar