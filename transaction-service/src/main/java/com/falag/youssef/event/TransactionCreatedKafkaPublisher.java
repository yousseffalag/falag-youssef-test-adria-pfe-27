package com.falag.youssef.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionCreatedKafkaPublisher {

    private static final Logger log = LoggerFactory.getLogger(TransactionCreatedKafkaPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public TransactionCreatedKafkaPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                            ObjectMapper objectMapper,
                                            @Value("${bank.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(TransactionCreatedMessage event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.accountId().toString(), payload)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("Échec publication de l'événement {} dans {}", event.eventId(), topic, error);
                        } else {
                            log.info("Événement {} publié dans {} avec la clé {}",
                                    event.eventId(), topic, event.accountId());
                        }
                    });
        } catch (JsonProcessingException exception) {
            log.error("Impossible de sérialiser l'événement {}", event.eventId(), exception);
        }
    }
}
