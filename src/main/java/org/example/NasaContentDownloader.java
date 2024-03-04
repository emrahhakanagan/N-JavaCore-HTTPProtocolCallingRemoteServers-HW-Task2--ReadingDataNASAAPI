package org.example;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class NasaContentDownloader {

    private static final String API_KEY = "kqFc11lWKfsT7Gsb8Auuzn41creU3DcktSFu5ZeQ";
    private static final String NASA_APOD_URL = "https://api.nasa.gov/planetary/apod?api_key=" + API_KEY;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void downloadDailyContent() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        try {
            HttpGet request = new HttpGet(NASA_APOD_URL);
            CloseableHttpResponse response = httpClient.execute(request);

            NasaResponse nasaResponse = objectMapper.readValue(response.getEntity().getContent(), NasaResponse.class);

            if (nasaResponse.getUrl() != null && !nasaResponse.getUrl().isEmpty()) {
                downloadContent(nasaResponse.getUrl());
            }

        } finally {
            httpClient.close();
        }
    }

    private void downloadContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);

        fileName = fileName.replaceAll("\\?.*$", "").replaceAll("[\\\\/:*?\"<>|]", "_");

        String saveDirectoryName = "downloaded_files/";
        File directorySavingFiles = new File(saveDirectoryName);

        if (!directorySavingFiles.exists()) {
            boolean wasCreatedDirectory = directorySavingFiles.mkdir();
            if (!wasCreatedDirectory) {
                System.out.println();
                throw new IOException("Directory for downloading: " + directorySavingFiles.getAbsolutePath()
                        + "could not created!");
            }
        }

        String fullPath = directorySavingFiles + File.separator + fileName;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
             CloseableHttpResponse response = httpClient.execute(new HttpGet(urlString));
             FileOutputStream fos = new FileOutputStream(fullPath)) {

            byte[] contentBytes = EntityUtils.toByteArray(response.getEntity());
            fos.write(contentBytes);

            System.out.println("Content downloaded and saved as " + fullPath);
        }
    }

}
