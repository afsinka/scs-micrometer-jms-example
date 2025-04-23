package org.example.controller;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);
    private final Tracer tracer;
    private final StreamBridge streamBridge;

    @Value("${demo.binding.name}")
    private String bindingName;

    public HelloController(Tracer tracer, StreamBridge streamBridge) {
        this.tracer = tracer;
        this.streamBridge = streamBridge;
    }

    @GetMapping("/hello/{name}")
    public void hello(@PathVariable String name) {
        var message = "Hello from " + name;
        logger.info("Sending a message: {}", message);
        streamBridge.send(bindingName, message);

        var traceId = tracer.currentSpan().context().traceId();
        var spanId = tracer.currentSpan().context().spanId();
        var sampled = tracer.currentSpan().context().sampled();
        logger.info("[SENDER] TraceId: {}, SpanId: {}", traceId, spanId);

        propagateTraceIdToNewThread();
        propagateTraceIdManually(traceId, spanId, sampled);
    }

    //just an extra example to show how to propagate TraceId to new thread
    private void propagateTraceIdToNewThread() {
        logger.debug("Trace ID will be propagated to new thread via ContextExecutorService");
        var executorService = ContextExecutorService.wrap(Executors.newFixedThreadPool(1),
                ContextSnapshotFactory.builder().build()::captureAll);
        Runnable runnableTask = () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                logger.debug("New thread has same traceId!");
            } catch (InterruptedException ex) {
                logger.error("error", ex);
            }
        };
        executorService.execute(runnableTask);
    }

    //just an extra example to show how to propagate TraceId manually
    private void propagateTraceIdManually(final String traceId, final String spanId, final Boolean sampled) {
        TraceContext tc = tracer.traceContextBuilder()
                .traceId(traceId)
                .spanId(spanId)
                .sampled(sampled)
                .build();

        try (var sc = tracer.currentTraceContext().newScope(tc)) {
            logger.debug("Still using same traceId even there is a new context");
        }
    }

}