# Use an official OpenJDK 21 runtime as a parent image
FROM eclipse-temurin:21-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the application's jar file to the container
COPY target/backend-0.0.1-SNAPSHOT.jar /app/app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","/app/app.jar"]