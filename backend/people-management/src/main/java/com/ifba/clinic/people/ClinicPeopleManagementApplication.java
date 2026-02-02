package com.ifba.clinic.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableDiscoveryClient
public class ClinicPeopleManagementApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClinicPeopleManagementApplication.class, args);
  }

}
