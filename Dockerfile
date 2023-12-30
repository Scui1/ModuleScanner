FROM gradle:jdk17 AS build
ARG GITHUB_USERNAME
ARG GITHUB_TOKEN
COPY --chown=gradle:gradle build.gradle.kts gradle.properties settings.gradle.kts /home/gradle/src/
RUN mkdir /home/gradle/src/src
COPY src /home/gradle/src/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre
RUN mkdir -p /app/modules
COPY --from=build /home/gradle/src/build/libs/modulescanner.jar /app/modulescanner.jar
EXPOSE 8080:8080
CMD ["java", "-jar", "/app/modulescanner.jar", "-port=8080", "-P:moduleDirectory=/app/modules"]