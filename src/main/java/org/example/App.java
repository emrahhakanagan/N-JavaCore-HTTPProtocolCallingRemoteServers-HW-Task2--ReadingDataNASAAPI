package org.example;

public class App {
    public static void main(String[] args) {
        NasaContentDownloader downloader = new NasaContentDownloader();
        try {
            downloader.downloadDailyContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}