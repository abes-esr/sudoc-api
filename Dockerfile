###
# Image pour la compilation
FROM maven:3-eclipse-temurin-21 as build-image
WORKDIR /build/
# Installation et configuration de la locale FR
RUN apt update && DEBIAN_FRONTEND=noninteractive apt -y install locales

COPY ./   /build/

RUN mvn --batch-mode \
        -Dmaven.test.skip=true \
        -Duser.timezone=Europe/Paris \
        -Duser.language=fr \
        package -Passembly

###
# Image pour le module API
FROM ossyupiik/java:21.0.8 as api-image
WORKDIR /
COPY --from=build-image /build/target/sudoc-distribution.tar.gz /
RUN tar xvfz sudoc-distribution.tar.gz
RUN rm -f /sudoc-distribution.tar.gz

ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

CMD ["java", "-jar", "/sudoc/lib/sudoc.jar"]
