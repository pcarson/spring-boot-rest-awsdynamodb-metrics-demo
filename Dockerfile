FROM openjdk:17-jdk-slim

ENV TZ=Europe/Berlin
ENV LC_ALL C.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/spring-boot-rest-awsdynamodb-metrics-demo-0.0.1-SNAPSHOT.jar /app.jar

#ENV AWS_DEFAULT_REGION=eu-central-1
#ENV AWS_ACCESS_KEY_ID=needed-for-localstack-dynamo
#ENV AWS_SECRET_KEY=sssshhh

COPY container_start.sh /container_start.sh
RUN chmod 755 /container_start.sh

ENTRYPOINT /container_start.sh