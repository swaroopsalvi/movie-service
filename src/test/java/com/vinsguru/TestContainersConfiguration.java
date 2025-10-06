package com.vinsguru;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class TestContainersConfiguration {

    @Bean
    //The service connection annotation will automatically configure the datasource properties
    //spring.datasource.url=jdbc:postgresql://localhost:5432/movie
    //spring.datasource.username=postgres
    //spring.datasource.password=secret
    //for the PostgreSQLContainer instance
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        //here we are using AWS ECR Public Gallery to avoid rate limiting issues with Docker Hub.
        var dockerImage = DockerImageName.parse("public.ecr.aws/docker/library/postgres:latest").asCompatibleSubstituteFor("postgres");
        return new PostgreSQLContainer<>(dockerImage)
                .withDatabaseName("movie") // database name is movie
                .withInitScript("init-db.sql"); // run this script is in src/test/resources
    }

}
