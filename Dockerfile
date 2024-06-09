FROM amazoncorretto:17

ARG VERSION
ENV VERSION=$VERSION

COPY gradlew build.gradle settings.gradle /server/
COPY gradle /server/gradle
COPY src /server/src

WORKDIR /server

RUN chmod +x gradlew
RUN ./gradlew build -x test --parallel

EXPOSE 8080

CMD java -jar "build/libs/wwme-${VERSION}.jar"
