package com.ifba.clinic.access.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessControlRabbitConfig {

  public static final String COMMAND_EXCHANGE = "people.profile.command";
  public static final String RESPONSE_EXCHANGE = "access.profile.response";
  public static final String RESPONSE_QUEUE = "access.profile.response.queue";

  public static final String RESPONSE_DLX = "access.profile.response.dlx";
  public static final String RESPONSE_DLQ = "access.profile.response.dlq";
  public static final String DLQ_ROUTING_KEY = "profile.run.response.dlq";

  @Bean
  DirectExchange commandExchange() {
    return new DirectExchange(COMMAND_EXCHANGE);
  }

  @Bean
  DirectExchange responseExchange() {
    return new DirectExchange(RESPONSE_EXCHANGE);
  }

  @Bean
  DirectExchange responseDlqExchange() {
    return new DirectExchange(RESPONSE_DLX);
  }

  @Bean
  Queue responseQueue() {
    return QueueBuilder.durable(RESPONSE_QUEUE)
        .withArgument("x-dead-letter-exchange", RESPONSE_DLX)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  Binding responseBinding() {
    return BindingBuilder
        .bind(responseQueue())
        .to(responseExchange())
        .with("profile.run.response");
  }

  @Bean
  Queue responseDlqQueue() {
    return new Queue(RESPONSE_DLQ, true);
  }

  @Bean
  Binding responseDlqBinding() {
    return BindingBuilder
        .bind(responseDlqQueue())
        .to(responseDlqExchange())
        .with(DLQ_ROUTING_KEY);
  }

  @Bean
  public MessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
