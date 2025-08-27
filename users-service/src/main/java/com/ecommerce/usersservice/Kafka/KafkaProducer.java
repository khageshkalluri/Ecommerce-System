package com.ecommerce.usersservice.Kafka;


import User.Events.UserEvent;
import com.ecommerce.usersservice.model.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger logger=LoggerFactory.getLogger(KafkaProducer.class);
    private @Value("${KAFKA_TOPIC:users}") String topicName;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendKafkaMessage(Users users) {
        logger.info("Sending users to Kafka Topic");
        UserEvent userEvent=UserEvent.newBuilder().setName(users.getName()).setEmail(users.getEmail()).build();
        logger.info("Sent kafka user event: "+userEvent.toString());
        this.kafkaTemplate.send(this.topicName,userEvent.toByteArray());
    }

}
