FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src
RUN chmod +x gradlew && ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --uid 10001 budgetai && chown budgetai /app
COPY --from=build --chown=budgetai /workspace/build/libs/budgetai-spring-1.0.0.jar /app/app.jar
USER budgetai
ENV SERVER_ADDRESS=0.0.0.0
ENV PORT=8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65.0 -XX:+UseSerialGC"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
