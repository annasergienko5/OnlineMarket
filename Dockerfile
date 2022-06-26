
FROM openjdk:17.0.1-jdk-buster
COPY annasergienko-0.0.1.jar /placeMarket/annasergienko-0.0.1.jar
WORKDIR /placeMarket/
ENTRYPOINT ["java", "-jar", "annasergienko-0.0.1.jar"]