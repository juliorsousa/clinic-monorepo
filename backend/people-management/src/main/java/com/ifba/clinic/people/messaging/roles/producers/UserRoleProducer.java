package com.ifba.clinic.people.messaging.roles.producers;

import com.ifba.clinic.people.messaging.roles.config.UserRoleRabbitConfig;
import com.ifba.clinic.people.messaging.roles.models.UserRoleDroppedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleProducer {

  private final RabbitTemplate rabbitTemplate;

  public void publishRoleDropped(UserRoleDroppedEvent event) {
    rabbitTemplate.convertAndSend(
        UserRoleRabbitConfig.USER_ROLE_EVENTS_EXCHANGE,
        "",
        event
    );
  }

}
