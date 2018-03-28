FROM openjdk:8-jre

WORKDIR /

ADD target/cmbot-*.jar cmbot.jar

CMD ["java", "-jar", "cmbot.jar"]