package com.ifba.clinic.access.messaging.intents.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.access.models.response.ProfileIntentProcessingResponse;
import com.ifba.clinic.access.services.ProfilingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProfileIntentResponseListener {

  private final ProfilingService profilingService;

  private final ObjectMapper objectMapper;

  @RabbitListener(queues = "access.profile.response.queue")
  public void handleProfileIntentResponse(String raw) {
    ProfileIntentProcessingResponse response = parseMessage(raw);

    if (response.status() == null) {
      log.error("Received Profile Intent Processing Response with null status: {}", response);

      throw new IllegalArgumentException("Profile Intent Processing Response status cannot be null");
    }

    log.info("Received Profile Intent Processing Response: {}", response);

    profilingService.processProfileIntentResponse(response);
  }

  private ProfileIntentProcessingResponse parseMessage(String raw) {
    try {
      return objectMapper.readValue(raw, ProfileIntentProcessingResponse.class);
    } catch (Exception e) {
      log.error("Failed to parse RunProfileIntentMessage: {}", raw, e);

      throw new RuntimeException("Failed to parse RunProfileIntentMessage", e);
    }
  }
}
