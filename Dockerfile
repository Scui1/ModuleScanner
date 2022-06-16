FROM gradle:jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM ghcr.io/graalvm/jdk:java17-21.3.0
EXPOSE 8080:8080
RUN mkdir -p /app/modules
COPY --from=build /home/gradle/src/build/libs/*.jar /app/modulescanner.jar
ENTRYPOINT ["java","-jar","/app/modulescanner.jar", "-port=8080", "-P:moduleDirectory=/app/modules"]