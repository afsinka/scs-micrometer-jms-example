# Spring Cloud Stream, RabbitMQ, Logback, Zipkin and Micrometer for TraceID Propagation in JMS Messages

- This is an example project to show JMS header propagation for TraceID of Micrometer.
- There is an API to send a message via Spring Cloud Stream (SCS) and RabbitMQ.
- Same application also consumes the relevant queue to receive the message and prints the message in logs.
- Every new HTTP request for that API will get a new TraceID and while sending a message TraceID will be propagated
  from producer to consumer.
- Thus, it will be possible to follow traces of same transaction in both producer and consumer applications via TraceID
  in a real project.

## Prerequisites

- Zipkin server (see. https://zipkin.io/pages/quickstart.html)
- RabbitMQ

## Setup

- Start Zipkin (http://localhost:9411/ for Zipkin UI)
- Start RabbitMQ
- Start this SCS-Rabbit application on your IDE
- Call Hello API e.g.

```
curl http://localhost:8081/hello/Jane
```

- It will send a message to a queue called "queue1" as stated in application.properties
- It will also consume same queue and prints the message in logs
- In the logs you can also see TraceID and SpanID and TraceID is same both produce and consumer sides e.g.
  `68064490accd7cd5ea651b276d62ceb3`

```
21-04-2025 15:13:52.492 [http-nio-8081-exec-4] INFO  68064490accd7cd5ea651b276d62ceb3 ea651b276d62ceb3 org.example.controller.HelloController.hello - Sending a message: Hello from Jane
21-04-2025 15:13:52.498 [queue1.anonymous.V88GLqnpQs6WHj2rZ5nWhA-1] INFO  68064490accd7cd5ea651b276d62ceb3 426ff8d50267fa6d org.example.Application.lambda$receive$0 - Received a message: GenericMessage [payload=Hello from Jane, headers={amqp_receivedDeliveryMode=PERSISTENT, amqp_receivedExchange=queue1, amqp_deliveryTag=2, deliveryAttempt=1, amqp_consumerQueue=queue1.anonymous.V88GLqnpQs6WHj2rZ5nWhA, amqp_redelivered=false, amqp_receivedRoutingKey=queue1, amqp_timestamp=Mon Apr 21 15:13:52 CEST 2025, traceparent=00-68064490accd7cd5ea651b276d62ceb3-b69e8b5a498506f4-01, source-type=amqp, amqp_messageId=e96e8b66-89fe-4278-aa78-e7c1ecadfcba, amqp_retryCount=0, id=b17755fc-f6dc-580f-995d-0f989865e57f, amqp_consumerTag=amq.ctag-OPJ4y6eL3zTrlBlUNcWjXw, sourceData=(Body:'Hello from Jane' MessageProperties [headers={traceparent=00-68064490accd7cd5ea651b276d62ceb3-b69e8b5a498506f4-01, target-protocol=amqp}, timestamp=Mon Apr 21 15:13:52 CEST 2025, messageId=e96e8b66-89fe-4278-aa78-e7c1ecadfcba, contentType=application/json, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=queue1, receivedRoutingKey=queue1, deliveryTag=2, consumerTag=amq.ctag-OPJ4y6eL3zTrlBlUNcWjXw, consumerQueue=queue1.anonymous.V88GLqnpQs6WHj2rZ5nWhA]), contentType=application/json, timestamp=1745241232498}]
```

- `traceparent=00-68064490accd7cd5ea651b276d62ceb3-b69e8b5a498506f4-01` field here in JMS headers shows the propagated
  TraceID
- On Zipkin UI, you can see all the threads which have same TraceID

> [!NOTE]
> Spring Boot is using 'W3C' propagation as default instead of 'B3'. For 'B3'
> see. https://github.com/openzipkin/b3-propagation?tab=readme-ov-file#b3-propagation

### References

- https://docs.spring.io/spring-boot/reference/actuator/tracing.html
- https://spring.io/blog/2022/10/12/observability-with-spring-boot-3
- https://docs.spring.io/spring-cloud-stream/reference/observability.html
- https://www.baeldung.com/tracing-services-with-zipkin