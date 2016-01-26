package com.ivanov.jdloader.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

public class Config {

    private int countOfThreads;

    private int limitSpeed;

    private String outputFolder;

    private Map<String, Queue<String>> data;

    private ThreadPoolExecutor taskPool;

    public Config(final Parser parser) {
        this.countOfThreads = parser.getCountOfThreads();
        this.outputFolder = parser.getOutputFolder();
        this.limitSpeed = parser.getLimitSpeed();
        this.data = parser.getData();
    }

    public void setTaskPool(final ThreadPoolExecutor taskPool) {
        this.taskPool = taskPool;
    }

    public int getCountOfThreads() {
        return countOfThreads;
    }

    public String getOutputFolder() {
        return outputFolder + "/";
    }

    public int getLimitSpeed() {
        return limitSpeed / taskPool.getActiveCount();
    }

    public Map<String, Queue<String>> getData() {
        return data;
    }

    public void initOutputFolder() throws IOException {
        Path folder = Paths.get(outputFolder);

        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
            System.out.println("Папка \"" + folder + "\" создана");
        }
    }

}
