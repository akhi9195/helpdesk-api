# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# Maven wrapper + pom first, so dependencies are cached between builds
COPY mvnw pom.xml ./
COPY .mvn .mvn
# Fix Windows line endings and the executable bit lost on Windows
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
RUN ./mvnw -B -q dependency:go-offline

# Then the source code
COPY src src
RUN ./mvnw -B -q package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre
WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring

COPY --from=build --chown=spring:spring /workspace/target/*.jar app.jar

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -XX:TieredStopAtLevel=1"
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]