package com.ivanov.jdloader.services;

import java.util.Date;

public class Statistics {

    private long bytes = 0;

    private long start;

    private long time;

    public void addBytes(final long bytes) {
        this.bytes += bytes;
    }

    public void startTimer() {
        start = new Date().getTime();
    }

    public void finishTimer() {
        time = (new Date().getTime() - start) / 1000;
    }

    public void show() {
        System.out.println();
        System.out.println("Всего скачано: " + bytes + " байт");
        System.out.println("Время работы: " + time + " секунд");
        System.out.println("Скорость: " + (bytes / time) + " байт в секунду");
    }

}
