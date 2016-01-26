package com.ivanov.jdloader;


import com.ivanov.jdloader.services.Config;
import com.ivanov.jdloader.services.ManagedInputStream;
import com.ivanov.jdloader.services.Statistics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Queue;

public class Task implements Runnable {

    final private String url;

    final private Queue<String> fileNameQueue;

    final private Config config;

    final private Statistics statistics;

    public Task(final String url,
                final Queue<String> fileNameQueue,
                final Config config,
                final Statistics statistics) {

        this.url = url;
        this.fileNameQueue = fileNameQueue;
        this.config = config;
        this.statistics = statistics;
    }

    public void run() {
        try {
            URL url = new URL(this.url);
            Path file;
            boolean isDownload;

            do {
                file = Paths.get(config.getOutputFolder() + fileNameQueue.poll());
                isDownload = download(url, file);
            } while(!isDownload && fileNameQueue.size() > 0);

            createOtherFiles(file);
        } catch (MalformedURLException e) {
            System.out.println("Некорректная ссылка: " + url);
        }
    }

    private boolean download(final URL url, final Path file) {
        try (ManagedInputStream in = new ManagedInputStream(url.openStream());
             FileOutputStream out = new FileOutputStream(file.toFile())) {

            System.out.println("Начинается скачивание файла: " + file.getFileName());

            byte[] buffer;
            while ((buffer = in.read(config.getLimitSpeed())) != null) {
                out.write(buffer);
                statistics.addBytes(buffer.length);
            }

            System.out.println("Завершено скачивание файла: " + file.getFileName());

            return true;
        } catch (IOException e) {
            System.out.println("Ошибка при скачивании файла: " + file.getFileName());

            return false;
        }
    }

    private void createOtherFiles(final Path downloadedFile) {
        while (fileNameQueue.size() > 0) {
            Path newFile = Paths.get(config.getOutputFolder() + fileNameQueue.poll());
            try {
                System.out.println("Начинается скачивание файла: " + newFile.getFileName());

                Files.copy(
                        downloadedFile,
                        newFile,
                        StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Завершено скачивание файла: " + newFile.getFileName());
            } catch (IOException | InvalidPathException e) {
                System.out.println("Ошибка при создании файла: " + newFile.getFileName());
            }
        }
    }

}
