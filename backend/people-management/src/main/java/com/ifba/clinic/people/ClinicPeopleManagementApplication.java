package com.ifba.clinic.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ClinicPeopleManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClinicPeopleManagementApplication.class, args);
  }

}
