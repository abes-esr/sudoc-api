###
# Image pour la compilation
FROM maven:3-eclipse-temurin-17 as build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales
RUN sed -i '/fr_FR.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen
ENV LANG fr_FR.UTF-8
ENV LANGUAGE fr_FR:fr
ENV LC_ALL fr_FR.UTF-8


# On lance la compilation Java
# On débute par une mise en cache docker des dépendances Java
# cf https://www.baeldung.com/ops/docker-cache-maven-dependencies
COPY pom.xml /build/sudoc/pom.xml
RUN mvn verify --fail-never
# et la compilation du code Java
COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package spring-boot:repackage


###
# Image pour le module API
#FROM tomcat:9-jdk17 as api-image
#COPY --from=build-image /build/web/target/*.war /usr/local/tomcat/webapps/ROOT.war
#CMD [ "catalina.sh", "run" ]
FROM eclipse-temurin:17-jre as sudoc-image
WORKDIR /app/
COPY --from=build-image /build/target/sudoc.jar /app/sudoc.jar
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENTRYPOINT ["java","-XX:MaxRAMPercentage=75","-XX:+UseG1GC","-XX:ConcGCThreads=5","-XX:+ExitOnOutOfMemoryError","-XX:MaxGCPauseMillis=100","-Dcom.sun.management.jmxremote=true", "-Dcom.sun.management.jmxremote.port=9090", "-Dcom.sun.management.jmxremote.rmi.port=9090", "-Dcom.sun.management.jmxremote.ssl=false", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.local.only=false", "-jar","/app/sudoc.jar"]
