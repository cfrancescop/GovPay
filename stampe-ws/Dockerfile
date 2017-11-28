FROM openjdk:8-jdk
#VOLUME /tmp
COPY target/stampe-ws-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
EXPOSE 8761
EXPOSE 5000
VOLUME /var/govpay
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
