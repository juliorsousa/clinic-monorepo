package com.ifba.clinic.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class ClinicAppointmentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicAppointmentsApplication.class, args);
	}

}
