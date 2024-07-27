FROM amazoncorretto:17

ARG VERSION
ENV VERSION=$VERSION

ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS

COPY gradlew build.gradle settings.gradle /server/
COPY gradle /server/gradle
COPY src /server/src

WORKDIR /server

RUN chmod +x gradlew
RUN ./gradlew build -x test --parallel

EXPOSE 8080
EXPOSE 8081

CMD java ${JAVA_OPTS} -jar "build/libs/wwme-${VERSION}.jar"
