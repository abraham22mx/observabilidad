FROM eclipse-temurin:17-jre-alpine
# Descargamos el Agente de OpenTelemetry
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.32.0/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
WORKDIR /app
COPY target/obs-service-1.0.jar app.jar
ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-Xmx128m", "-jar", "app.jar"]