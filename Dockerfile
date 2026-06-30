FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

ENV GRADLE_OPTS="-Xmx512m -Dorg.gradle.daemon=false"

COPY gradlew ./
RUN chmod +x gradlew

COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl wget

COPY --from=build /app/build/libs/*.jar app.jar

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]