FROM gradle:jdk17-alpine AS build
COPY --chown=gradle:gradle build.gradle.kts gradle.properties settings.gradle.kts /home/gradle/src/
RUN mkdir /home/gradle/src/src
COPY src /home/gradle/src/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM scui2/graalvm-native-image-muslc:latest AS buildNative
RUN mkdir -p /app/modules
COPY --from=build /home/gradle/src/build/libs/*.jar /build/modulescanner.jar
COPY reflection-config.json resource-config.json jni-config.json /build/
RUN cd /build/ && native-image --static --initialize-at-build-time=io.ktor,kotlinx,kotlin,org.slf4j,ch.qos.logback --allow-incomplete-classpath --no-fallback --enable-url-protocols=http,https --enable-all-security-services --libc=musl -jar modulescanner.jar -H:+ReportExceptionStackTraces -H:Name=output -H:ReflectionConfigurationFiles=/build/reflection-config.json -H:ResourceConfigurationFiles=/build/resource-config.json -H:JNIConfigurationFiles=/build/jni-config.json

FROM scratch
EXPOSE 8080:8080
COPY --from=buildNative /app/modules /app/modules
COPY --from=buildNative /build/output /opt/output
CMD ["/opt/output", "-port=8080", "-P:moduleDirectory=/app/modules"]