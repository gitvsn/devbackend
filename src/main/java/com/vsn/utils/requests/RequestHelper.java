package com.vsn.utils.requests;

import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

public class RequestHelper {

    private static HttpClient httpClient;

    public static HttpClient getHttpClient() {
        if (httpClient == null) {
            SSLParameters parameters = new SSLParameters();
            parameters.setProtocols(new String[]{"TLSv1.2"});
            httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMinutes(1))
                    .sslParameters(parameters)
                    .build();
        }
        return httpClient;
    }

    public static String GET_Request(String url, String... headers) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).timeout(Duration.ofMinutes(1)).GET().uri(new URI(url));
            if (headers != null && headers.length != 0) request.headers(headers);
            HttpResponse<String> send = getHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
            return send.body();
        } catch (URISyntaxException ignored) {
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String POST_Request(String url, String params, String... headers) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .timeout(Duration.ofMinutes(1))
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .uri(new URI(url));

            if (headers != null && headers.length != 0) request.headers(headers);
            HttpResponse<String> stringHttpResponse = getHttpClient().sendAsync(request.build(), HttpResponse.BodyHandlers.ofString()).get();

            //    String code = String.valueOf(stringHttpResponse.statusCode());
            //     System.out.println("Status: " + stringHttpResponse.statusCode());
            return stringHttpResponse.body();
        } catch (URISyntaxException ignored) {
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String DELETE_Request(String url, String... headers) {
        try {
            HttpRequest.Builder request = HttpRequest.newBuilder().version(HttpClient.Version.HTTP_1_1).timeout(Duration.ofMinutes(1)).DELETE().uri(new URI(url));
            if (headers != null && headers.length != 0) request.headers(headers);
            HttpResponse<String> send = getHttpClient().send(request.build(), HttpResponse.BodyHandlers.ofString());
            return send.body();
        } catch (URISyntaxException ignored) {
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

