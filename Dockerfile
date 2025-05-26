From openjdk:21
EXPOSE 8080
ADD backend/target/bankingapplication.jar bankingapplication.jar
ENTRYPOINT ["java", "-jar", "bankingapplication.jar"]