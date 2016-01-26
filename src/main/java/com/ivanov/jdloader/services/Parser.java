package com.ivanov.jdloader.services;

import com.ivanov.jdloader.exceptions.JDParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {

    private String[] args;

    private int countOfThreads;

    private int limitSpeed;

    private String outputFolder;

    private Map<String, Queue<String>> data;

    public Parser(String[] args) {
        this.args = args;
        countOfThreads = 5;
        limitSpeed = 1_048_575;
        outputFolder = "Downloads";
        data = new HashMap<>();
    }

    public void startParse() throws JDParseException {
        if ((args.length % 2) != 0) {
            throw new JDParseException("Неверное количество аргументов");
        }

        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            String value = args[i + 1];

            switch (key) {
                case "-n":
                    parseThreadsCount(value);
                    break;
                case "-l":
                    parseLimitSpeed(value);
                    break;
                case "-o":
                    parseOutputFolder(value);
                    break;
                case "-f":
                    parseFileLinks(value);
                    break;
                default:
                    throw new JDParseException("Недопустимый параметр: " + key);
            }
        }

        if (data.size() == 0) {
            throw new JDParseException("Укажите файл со ссылками");
        }
    }

    private void parseThreadsCount(final String countOfThreads) throws JDParseException {
        Matcher matcher = Pattern
                .compile("^(?<number>\\d+)$")
                .matcher(countOfThreads);

        if (matcher.find()) {
            this.countOfThreads = new Integer(matcher.group("number"));
        } else {
            throw new JDParseException("Неверное количество потоков: " + countOfThreads);
        }
    }

    private void parseLimitSpeed(final String limitSpeed) throws JDParseException {
        Matcher matcher = Pattern
                .compile("^(?<number>\\d+)(?<suffix>[kKmM])?$")
                .matcher(limitSpeed);

        if (matcher.find()) {
            this.limitSpeed = new Integer(matcher.group("number"));

            String suffix = matcher.group("suffix");
            if (suffix != null) {
                switch (suffix) {
                    case "k":
                    case "K":
                        this.limitSpeed *= 1024;
                        break;
                    case "m":
                    case "M":
                        this.limitSpeed *= 1024 * 1024;
                        break;
                    default:
                        break;
                }
            }
        } else {
            throw new JDParseException("Неверное ограничение скорости: " + limitSpeed);
        }
    }

    private void parseOutputFolder(final String outputFolder) {
        this.outputFolder = outputFolder;
    }

    private void parseFileLinks(final String fileName) throws JDParseException {
        Path file = Paths.get(fileName);

        try (BufferedReader in = new BufferedReader(new FileReader(file.toFile()))) {
            String line;
            while ((line = in.readLine()) != null) {
                parseLine(line);
            }
        } catch (IOException e) {
            throw new JDParseException("Ошибка при чтении файла: " + fileName, e);
        }
    }

    private void parseLine(final String line) throws JDParseException {
        Pattern pattern = Pattern.compile("^(?<url>.+)\\s(?<file>.+)$");
        Matcher matcher = pattern.matcher(line);

        if (!matcher.find()) {
            throw new JDParseException("Некорректная строка в файле со ссылками: " + line);
        }

        String url = matcher.group("url"), file = matcher.group("file");

        Queue<String> fileNameQueue = data.get(url);
        if (fileNameQueue == null) {
            fileNameQueue = new ArrayDeque<>();
            fileNameQueue.add(file);
            data.put(url, fileNameQueue);
        } else {
            fileNameQueue.add(file);
        }
    }

    public int getCountOfThreads() {
        return countOfThreads;
    }

    public int getLimitSpeed() {
        return limitSpeed;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public Map<String, Queue<String>> getData() {
        return data;
    }

}
