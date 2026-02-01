package com.ifba.clinic.people.messaging.intents.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.people.messaging.intents.models.RunProfileIntentMessage;
import com.ifba.clinic.people.messaging.intents.models.responses.ProfileIntentResponse;
import com.ifba.clinic.people.messaging.intents.strategies.ProfileGeneratingStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProfileIntentConsumer {

  private final List<ProfileGeneratingStrategy> strategies;

  private final ObjectMapper objectMapper;
  private final RabbitTemplate rabbitTemplate;

  @RabbitListener(queues = "people.profile.run.queue")
  public void handleRunProfileIntent(String raw) {
    RunProfileIntentMessage request = parseMessage(raw);

    log.info("Received Run Profile Intent Message: {}", request);

    ProfileGeneratingStrategy strategy = strategies.stream()
        .filter(s -> s.getProfileType().equalsIgnoreCase(request.type()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("No strategy found for profile type: " + request.type()));

    log.info("Using strategy: {}", strategy.getClass().getSimpleName());

    ProfileIntentResponse response = strategy.generateProfile(request);

    rabbitTemplate.convertAndSend(
        "access.profile.response",
        "profile.run.response",
        writeResponse(response)
    );

  }

  private RunProfileIntentMessage parseMessage(String raw) {
    try {
      return objectMapper.readValue(raw, RunProfileIntentMessage.class);
    } catch (Exception e) {
      log.error("Failed to parse RunProfileIntentMessage: {}", raw, e);
      throw new RuntimeException("Failed to parse RunProfileIntentMessage", e);
    }
  }

  private String writeResponse(ProfileIntentResponse response) {
    try {
      return objectMapper.writeValueAsString(response);
    } catch (Exception e) {
      log.error("Failed to write ProfileIntentResponse: {}", response, e);

      throw new RuntimeException("Failed to write ProfileIntentResponse", e);
    }
  }
}
