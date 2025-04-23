package org.example;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@ComponentScan
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private Tracer tracer;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Consumer<Message<String>> receive() {
        return message -> {
            logger.info("Received a message: {}", message);
            var traceId = tracer.currentSpan().context().traceId();
            var spanId = tracer.currentSpan().context().spanId();
            logger.debug("[RECEIVER] TraceId: {}, SpanId: {}", traceId, spanId);
        };
    }

}