FROM gradle:7-jdk17-alpine AS cache
RUN mkdir -p /home/gradle/cache_home
ENV GRADLE_USER_HOME /home/gradle/cache_home
COPY --chown=gradle:gradle build.gradle.kts gradle.properties /home/gradle/src/
WORKDIR /home/gradle/src
RUN gradle clean build


FROM gradle:7-jdk17-alpine AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar


FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*-all.jar /app/api-all.jar

ENTRYPOINT ["java", "-jar", "/app/api-all.jar"]