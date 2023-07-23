FROM openjdk:17-jdk-alpine
VOLUME /tmp
ADD /target/jms-publisher-0.0.1-SNAPSHOT.jar jms-publisher-service.jar
ENTRYPOINT ["java","-jar","/jms-publisher-service.jar"]
