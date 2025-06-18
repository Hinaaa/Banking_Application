From openjdk:21
ADD backend/target/bankingapplication.jar bankingapplication.jar
ENTRYPOINT ["java", "-jar", "bankingapplication.jar"]