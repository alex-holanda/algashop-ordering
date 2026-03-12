ARG JAR_NAME=ordering.jar

# ---------- Stage 1: analyze dependencies ----------
FROM eclipse-temurin:21-jdk-alpine AS deps
ARG JAR_NAME

WORKDIR /app

COPY build/libs/${JAR_NAME} .

RUN set -eux \
 && jar -xf ${JAR_NAME} \
 && (jdeps \
      --ignore-missing-deps \
      --recursive \
      --multi-release 21 \
      --print-module-deps \
      --class-path "BOOT-INF/lib/*" \
      BOOT-INF/classes \
      || echo "java.base") > /modules.txt

# ---------- Stage 2: build custom runtime ----------
FROM eclipse-temurin:21-jdk-alpine AS jre-build

COPY --from=deps /modules.txt /modules.txt

RUN set -eux \
 && jlink \
      --add-modules "$(cat /modules.txt),jdk.crypto.ec" \
      --strip-debug \
      --no-man-pages \
      --no-header-files \
      --compress=2 \
      --output /jre

# ---------- Stage 3: runtime ----------
FROM alpine:3.23
ARG JAR_NAME

ENV JAVA_HOME=/jre \
    PATH="/jre/bin:$PATH" \
    TZ=America/Sao_Paulo \
    JAR_NAME=${JAR_NAME} \
    SERVER_PORT=8080

WORKDIR /app

RUN set -eux \
 && addgroup -S spring \
 && adduser -S -G spring spring \
 && apk add --no-cache tzdata curl \
 && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
 && echo "$TZ" > /etc/timezone

COPY --from=jre-build /jre /jre
COPY --chown=spring:spring build/libs/${JAR_NAME} .
COPY --chown=spring:spring --chmod=755 docker/docker-entrypoint.sh .

USER spring

HEALTHCHECK --interval=15s --timeout=5s --start-period=20s --retries=3 \
 CMD curl -fs http://localhost:${SERVER_PORT}/actuator/health | grep UP || exit 1

EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]