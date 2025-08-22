package com.ecommerce.analyticsservice.Kafka;


import User.Events.UserEvent;
import com.ecommerce.analyticsservice.Model.UserEvents;
import com.ecommerce.analyticsservice.Repository.EventRepository;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventConsumer {
 private static final Logger log = LoggerFactory.getLogger(KafkaEventConsumer.class);
     private EventRepository eventRepository;
     public KafkaEventConsumer(EventRepository eventRepository) {
         this.eventRepository=eventRepository;
     }

    @KafkaListener(topics = "users",groupId = "analytics-service")
    public void getUserEvent(byte[] byteEvent) throws InvalidProtocolBufferException {
         UserEvent userEvent= UserEvent.parseFrom(byteEvent);
        log.info("Received user event {}", userEvent.toString());
     UserEvents userEvents= UserEvents.builder().username(userEvent.getName()).email(userEvent.getEmail()).build();
      this.eventRepository.save(userEvents);
    }
}
