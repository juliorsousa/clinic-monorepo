package com.ifba.clinic.appointment.feign.models;

public record SummarizedDoctorResponse(
    String id,
    String credential,
    String name,
    String specialty
) {
}