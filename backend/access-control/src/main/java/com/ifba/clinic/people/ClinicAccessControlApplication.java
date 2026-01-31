package com.ifba.clinic.people;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class ClinicAccessControlApplication {

  public static void main(String[] args) {
    SpringApplication.run(ClinicAccessControlApplication.class, args);
  }

}
