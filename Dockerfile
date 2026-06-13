FROM amazoncorretto:17-alpine

WORKDIR /app

COPY /home/rabindra-jar/demoapp/news_factory/news_ims/target app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]