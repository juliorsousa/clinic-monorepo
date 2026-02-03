package com.ifba.clinic.appointment.feign;

import com.ifba.clinic.appointment.feign.models.SummarizedDoctorResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "people-management")
public interface DoctorsClient {

  @GetMapping(value = "/doctors/by-specialty/{speciality}/summary", headers = "X-System-Call=true")
  List<SummarizedDoctorResponse> getDoctorsSummaryBySpeciality(@PathVariable String speciality);

  @GetMapping(value = "/doctors/{id}/validity", headers = "X-System-Call=true")
  boolean isDoctorValid(@PathVariable String id);

}
