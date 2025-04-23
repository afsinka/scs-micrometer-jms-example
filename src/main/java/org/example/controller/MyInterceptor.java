package org.example.controller;

import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.MessageValues;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Service;

@Service
@GlobalChannelInterceptor
public class MyInterceptor implements ChannelInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);
    private final Tracer tracer;

    public MyInterceptor(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        logger.debug("Intercepting for the message: {}", message);
        MessageValues messageValues = new MessageValues(message);

        var tracerData = messageValues.get("traceparent"); //b3 for B3, traceparent for W3C
        logger.debug("[INTERCEPTOR] Trace data in JMS headers: {}", tracerData);
        var traceId = tracer.currentSpan().context().traceId();
        logger.debug("[INTERCEPTOR] Trace ID in current span: {}", traceId);

        //messageValues.put("CUSTOM_HEADER", "CUSTOM_VALUE");

        return messageValues.toMessage();
    }
}
