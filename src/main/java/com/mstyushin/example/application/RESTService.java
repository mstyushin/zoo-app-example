package com.mstyushin.example.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RESTService implements Runnable {
    private final Integer appId;
    private final Integer httpPort;

    public RESTService(Integer appId, Integer httpPort) {

        this.appId = appId;
        this.httpPort = httpPort;
    }

    @Override
    public void run() {
        log.info("Application[" + appId + "][RESTService] - starting");
        // start RESTful http netty server
        log.info("Application[" + appId + "][RESTService] - started");
    }
}
