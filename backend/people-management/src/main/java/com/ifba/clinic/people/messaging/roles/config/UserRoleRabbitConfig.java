package com.ifba.clinic.people.messaging.roles.config;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserRoleRabbitConfig {

  public static final String USER_ROLE_EVENTS_EXCHANGE =
      "people.profile.role.events";

  @Bean
  FanoutExchange userRoleEventsExchange() {
    return new FanoutExchange(USER_ROLE_EVENTS_EXCHANGE);
  }
}
