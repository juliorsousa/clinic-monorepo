package com.ifba.clinic.appointment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "people-management")
public interface DoctorsClient {

  @GetMapping("/doctors/{id}/validity")
  boolean isDoctorValid(@PathVariable String id);

}
