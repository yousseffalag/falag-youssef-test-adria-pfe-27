package com.falag.youssef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventConsumer.class);

    @KafkaListener(topics = "${bank.kafka.topic}")
    public void receive(String event,
                        @Header(KafkaHeaders.RECEIVED_KEY) String accountId) {
        log.info("Événement transaction reçu; accountId={}, payload={}", accountId, event);
    }
}
