FROM amazoncorretto:17-alpine

WORKDIR /app

COPY news_ims/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]