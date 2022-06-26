FROM maven AS builder
WORKDIR /enrollment/
COPY . .
RUN mvn clean package



FROM openjdk:17.0.1-jdk-buster
COPY --from=builder /enrollment/target/annasergienko-0.0.1.jar /enrollment-1.0.jar
ENTRYPOINT ["java", "-Xmx128m", "-XX:+UseZGC", "-XX:ZUncommitDelay=60", "-XX:MaxHeapFreeRatio=30", "-XX:MinHeapFreeRatio=10", "-jar", "enrollment-1.0.jar"]