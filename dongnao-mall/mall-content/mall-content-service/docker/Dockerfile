# Name:     mall-content-service
# Time:     2018-07-31

FROM java:8-jre-alpine

MAINTAINER dongnao <2505507040@qq.com>

RUN mkdir /app

WORKDIR /app

COPY xbin-store-web-sso.jar /app

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/xbin-store-web-sso.jar"]

EXPOSE 8104