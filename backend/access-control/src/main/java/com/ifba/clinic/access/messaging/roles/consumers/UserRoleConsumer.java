package com.ifba.clinic.access.messaging.roles.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifba.clinic.access.messaging.roles.models.UserRoleDroppedEvent;
import com.ifba.clinic.access.models.response.ProfileIntentProcessingResponse;
import com.ifba.clinic.access.services.ProfilingService;
import com.ifba.clinic.access.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.access.messaging.roles.config.UserRolesRabbitConfig.ACCESS_QUEUE;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserRoleConsumer {

  private final UserService userService;

  private final ObjectMapper objectMapper;

  @RabbitListener(queues = ACCESS_QUEUE)
  public void handleUserRoleDropped(UserRoleDroppedEvent event) { // we must think about making that method generic with strategies if we have more message types
    log.info("Received role dropped event: {}: {}", event.role(), event.entityId());

    userService.handleUserRoleDroppedEvent(event);
  }
}
