package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.User;
import com.ifba.clinic.people.entities.UserRole;
import com.ifba.clinic.people.entities.UserTrait;
import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import com.ifba.clinic.people.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.people.utils.Messages.USER_DUPLICATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public CreateUserResponse createUser(CreateUserRequest request, EnumRole role, String... traits) {
    log.info("Creating new '{}' user with email: {}", role.name(), request.email());

    boolean userAlreadyExists =
        userRepository.findByEmail(
            request.email()
        ).isPresent();

    if (userAlreadyExists) {
      throw new ConflictException(USER_DUPLICATED);
    }

    User userEntity = User.builder()
        .email(request.email())
        .password(passwordEncoder.encode(request.password()))
        .deleted(false)
        .build();

    if (role != EnumRole.GUEST) {

      UserRole userRole = UserRole.builder()
          .role(role)
          .referencedEntityId(null)
          .deleted(false)
          .user(userEntity)
          .build();

      userEntity.setRoles(List.of(userRole));
    }

    if (traits != null && traits.length > 0) {

      List<UserTrait> userTraits = new ArrayList<>();

      for (String trait : traits) {
        userTraits.add(
            UserTrait.builder()
                .trait(trait)
                .deleted(false)
                .user(userEntity)
                .build()
        );
      }

      userEntity.setTraits(userTraits);
    }

    User savedUser = userRepository.save(userEntity);

    log.info("User with email: {} created successfully", request.email());

    return new CreateUserResponse(savedUser);
  }

}
