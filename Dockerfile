FROM openjdk:8-jre

RUN mkdir -p /logs
RUN mkdir -p /telegram-commands

WORKDIR /

ADD target/cmbot-*.jar cmbot.jar

CMD ["java", "-jar", "cmbot.jar"]