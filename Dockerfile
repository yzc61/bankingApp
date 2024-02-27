FROM maven:3.8.1 AS MAVEN_BUILD
ADD ./pom.xml pom.xml
ADD ./src src/
RUN mvn clean package 
FROM openjdk:11
COPY --from=MAVEN_BUILD target/bankingApp-0.0.1-SNAPSHOT.jar bankingApp.jar
EXPOSE 8080
CMD ["java", "-jar", "bankingApp.jar"]
