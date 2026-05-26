FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system aegisflow && useradd --system --gid aegisflow aegisflow

COPY --from=build /workspace/target/aegisflow-api-*.jar /app/aegisflow-api.jar

USER aegisflow
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-XX:+UseContainerSupport", "-jar", "/app/aegisflow-api.jar"]
