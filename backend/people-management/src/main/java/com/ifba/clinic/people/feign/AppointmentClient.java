package com.ifba.clinic.people.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @DeleteMapping("/appointments/by-doctor/{id}")
    void deleteDoctorAppointments(@PathVariable String id);

    @DeleteMapping("/appointments/by-patient/{id}")
    void deletePatientAppointments(@PathVariable String id);

}
