# Stage 1: Build the application (.jar)
# We use a Maven image with Java 21 to compile the code. 
# This image contains all the heavy tools needed for building.
# By this way, even Maven is locked to our desired version, avoiding version conflicts.
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and the source code to the container
COPY pom.xml .
COPY src ./src

# Run Maven to package the application into a .jar file, skipping tests to speed up the build
RUN mvn clean package -DskipTests

# Stage 2: Run the application
# Use a much lighter Java Runtime Environment (JRE) image to actually run the compiled code.
# Keeps the final image size small and secure.
FROM eclipse-temurin:21-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the compiled .jar file from the "build" stage above
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 so Docker knows the container listens on this port
EXPOSE 8080

# Define the command to start the Java application
ENTRYPOINT ["java", "-jar", "app.jar"]
