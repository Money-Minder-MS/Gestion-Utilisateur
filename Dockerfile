FROM eclipse-temurin:17
COPY target/Gestion_Utilisateur-0.0.1-SNAPSHOT.jar user.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","user.jar"]