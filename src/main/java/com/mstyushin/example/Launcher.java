package com.mstyushin.example;

import com.google.common.base.Preconditions;
import com.mstyushin.example.application.ElectionService;
import com.mstyushin.example.application.RESTService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.*;

@Slf4j
public class Launcher {
    private static final Integer httpPort = 8080;

    public static void main(String[] args) {
        final ExecutorService application = Executors.newFixedThreadPool(2);
        ConcurrentHashMap<Integer, Future<?>> services = new ConcurrentHashMap<>();
        Preconditions.checkArgument((args.length == 2), "Usage: java -jar <jar_file_name> <app id> <zkhost:port>");

        // no validations, mind your what you're passing!
        final Integer appId = Integer.parseInt(args[0]);
        final String zkURL = args[1];

        try {
            services.put(1, application.submit(new ElectionService(appId, zkURL)));
            services.put(2, application.submit(new RESTService(appId, httpPort)));

            // I'm too lazy to do proper future handling here
            services.get(1).get();
            services.get(2).get();

        } catch (IOException e) {
            log.error(e.getMessage());
            System.exit(1);
        } catch (InterruptedException | ExecutionException e) {
            application.shutdown();
        }
    }
}
