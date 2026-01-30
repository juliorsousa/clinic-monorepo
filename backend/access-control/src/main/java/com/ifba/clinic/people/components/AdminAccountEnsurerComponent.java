package com.ifba.clinic.people.components;

import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAccountEnsurerComponent implements CommandLineRunner {

  private final UserService userService;

  @Override
  public void run(String... args) throws Exception {
    log.info("AdminAccountEnsurerComponent executed on startup.");

    String adminEmail = "admin@clinic.net";
    String adminPassword = "Admin@123";

    try {
      userService.createUser(
          new CreateUserRequest(
              adminEmail,
              adminPassword
          ),
          EnumRole.ADMIN,
          "MUST_CHANGE_PASSWORD"
      );

      log.info("Admin account created with email: {}", adminEmail);
    } catch (ConflictException e) {
      log.info("Admin account already exists with email: {}", adminEmail);
    }

  }
}
