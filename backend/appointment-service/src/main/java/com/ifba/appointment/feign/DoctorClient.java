package com.ifba.appointment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "people-management")
public interface DoctorClient {

    @GetMapping("/doctors/{id}")
    boolean isDoctorAvailable(@PathVariable("id") String id);

}
