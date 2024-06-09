FROM openjdk:17

WORKDIR /server

COPY gradlew build.gradle settings.gradle /server/
COPY gradle /server/gradle
COPY src /server/src

RUN chmod +x gradlew
RUN ./gradlew build -x test --parallel

EXPOSE 8080

ARG VERSION

CMD ["java", "-jar", "build/libs/wwme-${VERSION}.jar"]
