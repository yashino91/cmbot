LABEL maintainer="s.ploner@gmx.net"

FROM openjdk:8-jre

WORKDIR /

COPY target/cmbot-*.jar cmbot.jar

CMD ["java", "-jar", "cmbot.jar"]