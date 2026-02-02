package com.ifba.clinic.people.messaging.intents.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileIntentRabbitConfig {

  public static final String COMMAND_EXCHANGE = "people.profile.command";
  public static final String COMMAND_QUEUE = "people.profile.run.queue";

  public static final String RESPONSE_EXCHANGE = "access.profile.response";

  public static final String COMMAND_DLQ_EXCHANGE = "people.profile.command.dlx";
  public static final String COMMAND_DLQ_QUEUE = "people.profile.run.dlq";

  public static final String DLQ_ROUTING_KEY = "profile.run.dlq";

  @Bean
  DirectExchange commandExchange() {
    return new DirectExchange(COMMAND_EXCHANGE);
  }

  @Bean
  DirectExchange commandDlqExchange() {
    return new DirectExchange(COMMAND_DLQ_EXCHANGE);
  }

  @Bean
  Queue commandQueue() {
    return QueueBuilder.durable(COMMAND_QUEUE)
        .withArgument("x-dead-letter-exchange", COMMAND_DLQ_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
        .build();
  }

  @Bean
  Binding commandBinding() {
    return BindingBuilder
        .bind(commandQueue())
        .to(commandExchange())
        .with("profile.run");
  }

  @Bean
  Queue commandDlqQueue() {
    return new Queue(COMMAND_DLQ_QUEUE, true);
  }

  @Bean
  Binding commandDlqBinding() {
    return BindingBuilder
        .bind(commandDlqQueue())
        .to(commandDlqExchange())
        .with(DLQ_ROUTING_KEY);
  }

  @Bean
  DirectExchange responseExchange() {
    return new DirectExchange(RESPONSE_EXCHANGE);
  }
}
