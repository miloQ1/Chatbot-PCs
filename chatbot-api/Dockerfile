# --- Etapa 1: build ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos primero el pom.xml para aprovechar el cache de capas de Docker:
# si solo cambia el codigo (no las dependencias), no se vuelven a descargar.
COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package -DskipTests

# --- Etapa 2: runtime ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos solo el jar ya construido desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]