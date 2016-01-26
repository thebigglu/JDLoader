package com.ivanov.jdloader;

import com.ivanov.jdloader.exceptions.JDParseException;
import com.ivanov.jdloader.services.Config;
import com.ivanov.jdloader.services.Parser;
import com.ivanov.jdloader.services.Statistics;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JDLoader {

    public static void main(String[] args) {
        try {
            Parser parser = new Parser(args);
            parser.startParse();

            Config config = new Config(parser);
            config.initOutputFolder();

            Statistics statistics = new Statistics();
            statistics.startTimer();

            Map<String, Queue<String>> data = config.getData();

            ThreadPoolExecutor taskPool =  new ThreadPoolExecutor(
                    config.getCountOfThreads(), config.getCountOfThreads(),
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());

            config.setTaskPool(taskPool);

            data.forEach((url, fileNameQueue) -> {
                Task task = new Task(url, fileNameQueue, config, statistics);
                taskPool.execute(task);
            });

            taskPool.shutdown();
            taskPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            statistics.finishTimer();
            statistics.show();
        } catch (JDParseException | IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

}
