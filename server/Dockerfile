FROM amazoncorretto:11
ADD target/shareit-server-0.0.1-SNAPSHOT.jar app.jar
#ENV TZ=Europe/Moscow
#ENV TZ=Asia/Yekaterinburg
ENTRYPOINT ["java","-jar","/app.jar"]