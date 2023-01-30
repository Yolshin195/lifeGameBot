package com.yolshin.lifegamebot.component.lifeGame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolshin.lifegamebot.component.lifeGame.dto.AuthDTO;
import com.yolshin.lifegamebot.component.lifeGame.dto.FileDTO;
import com.yolshin.lifegamebot.component.lifeGame.dto.OrderPictureDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LifeGameRestApiClient {

    @Value("${life.game.rest.api.client.username}")
    private String username;

    @Value("${life.game.rest.api.client.password}")
    private String password;

    @Value("${life.game.rest.api.client.client.id}")
    private String clientId;

    @Value("${life.game.rest.api.client.secret}")
    private String secret;

    private AuthDTO auth;

    @PostConstruct
    public void lifeGameRestApiClientPostConstructor() throws IOException, InterruptedException {
        auth();
    }

    public void auth() throws IOException, InterruptedException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "password");
        parameters.put("username", username);
        parameters.put("password", password);

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(clientId, secret.toCharArray());
                    }
                })
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/oauth/token"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        auth = new ObjectMapper().readValue(response.body(), AuthDTO.class);
    }

    public void authRefresh() throws IOException, InterruptedException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "refresh_token");
        parameters.put("refresh_token", auth.getRefreshToken());

        String form = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator(){
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(clientId, secret.toCharArray());
                    }
                })
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/oauth/token"))
                .headers("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        auth = new ObjectMapper().readValue(response.body(), AuthDTO.class);
    }

    public FileDTO createFile(File file, String fileName) throws IOException, InterruptedException {
        Path path = Paths.get(file.getAbsolutePath());
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/rest/files?name="+fileName))
                .headers("Authorization", auth.getAuthorization())
                .POST(HttpRequest.BodyPublishers.ofFile(path))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(response.body(), FileDTO.class);
    }

    public void createEntityOrderPicture(OrderPictureDTO orderPicture) throws IOException, InterruptedException {
        String body = new ObjectMapper().writeValueAsString(orderPicture);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/rest/entities/lg_OrderPicture"))
                .headers("Authorization", auth.getAuthorization())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

}
