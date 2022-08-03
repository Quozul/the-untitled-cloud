FROM gradle:7-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/api-all.jar

RUN addgroup --system --gid 1001 mc_host
RUN adduser --system --uid 1001 mc_host
USER 1001

ENTRYPOINT ["java", "-jar", "/app/api-all.jar"]