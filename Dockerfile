FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/campus-pet-foster-1.0.0.jar .
EXPOSE 8080
CMD ["java", "-jar", "campus-pet-foster-1.0.0.jar"]
