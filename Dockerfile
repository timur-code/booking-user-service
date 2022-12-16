# Build stage
FROM maven:3.8.6-ibm-semeru-17-focal AS build
WORKDIR app
COPY pom.xml pom.xml
RUN mvn dependency:go-offline
COPY src src
RUN mvn clean package -DskipTests

# Run stage
FROM ibm-semeru-runtimes:open-17-jdk-focal
WORKDIR app
COPY --from=build /app/target/*.jar app.jar
CMD java -jar app.jar
