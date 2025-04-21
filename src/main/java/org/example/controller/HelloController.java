package org.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final StreamBridge streamBridge;

    @Value("${demo.binding.name}")
    private String bindingName;

    public HelloController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable String name) {
        var message = "Hello from " + name;
        logger.info("Sending a message: {}", message);
        streamBridge.send(bindingName, message);
    }

}