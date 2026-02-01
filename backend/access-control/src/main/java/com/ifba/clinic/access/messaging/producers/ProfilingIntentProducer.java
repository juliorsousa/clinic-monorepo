package com.ifba.clinic.access.messaging.producers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.access.entities.ProfileIntent;
import com.ifba.clinic.access.models.RunProfileIntentMessage;
import com.ifba.clinic.access.models.requests.profiles.ProfileIntentRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfilingIntentProducer {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public void sendRunProfileIntent(ProfileIntent intent) throws JsonProcessingException {
    RunProfileIntentMessage message = new RunProfileIntentMessage(
        intent.getId(),
        intent.getUser().getId(),
        intent.getType().name(),
        objectMapper.readValue(
            intent.getBody(),
            ProfileIntentRequest.class
        ),
        Instant.now().toString()
    );

    try {
      rabbitTemplate.convertAndSend(
          "people.profile.command",
          "profile.run",
          objectMapper.writeValueAsString(message)
      );
    } catch (Exception e) {
      throw new RuntimeException("Failed to send RunProfileIntentMessage", e);
    }
  }

}