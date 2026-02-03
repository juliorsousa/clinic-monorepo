package com.ifba.clinic.people.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @DeleteMapping(path = "/appointments/by-doctor/{id}", headers = "X-System-Call=true")
    void deleteDoctorAppointments(@PathVariable String id);

    @DeleteMapping(path = "/appointments/by-patient/{id}", headers = "X-System-Call=true")
    void deletePatientAppointments(@PathVariable String id);

}
