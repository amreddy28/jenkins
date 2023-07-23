FROM openjdk:17-jdk-alpine
VOLUME /tmp
ADD /target/jms-subscriber-0.0.1-SNAPSHOT.jar jms-subscriber-service.jar
ENTRYPOINT ["java","-jar","/jms-subscriber-service.jar"]