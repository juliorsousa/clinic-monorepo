package com.ifba.clinic.access.messaging.roles.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRolesRabbitConfig {

  public static final String USER_ROLE_EVENTS_EXCHANGE =
      "people.profile.role.events";

  public static final String ACCESS_QUEUE =
      "access.user-role.queue";

  public static final String ACCESS_DLQ =
      "access.user-role.dlq";

  public static final String ACCESS_DLX =
      "access.user-role.dlx";

  public static final String ACCESS_DLQ_ROUTING_KEY =
      "access.user-role.dlq.rk";

  @Bean
  FanoutExchange userRoleEventsExchange() {
    return new FanoutExchange(USER_ROLE_EVENTS_EXCHANGE);
  }

  @Bean
  DirectExchange accessDlqExchange() {
    return new DirectExchange(ACCESS_DLX);
  }

  @Bean
  Queue accessQueue() {
    return QueueBuilder.durable(ACCESS_QUEUE)
        .withArgument("x-dead-letter-exchange", ACCESS_DLX)
        .withArgument("x-dead-letter-routing-key", ACCESS_DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  Binding accessBinding() {
    return BindingBuilder
        .bind(accessQueue())
        .to(userRoleEventsExchange());
  }

  @Bean
  Queue accessDlqQueue() {
    return new Queue(ACCESS_DLQ, true);
  }

  @Bean
  Binding accessDlqBinding() {
    return BindingBuilder
        .bind(accessDlqQueue())
        .to(accessDlqExchange())
        .with(ACCESS_DLQ_ROUTING_KEY);
  }
}
