package com.ifba.clinic.people.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @DeleteMapping("/appointments/delete/doctor/{id}")
    void deleteDoctorAppointments(@PathVariable("id") String id);

    @DeleteMapping("/appointments/delete/patient/{id}")
    void deletePatientAppointments(@PathVariable("id") String id);

}
