package com.ifba.clinic.people.services;

import com.ifba.clinic.people.entities.User;
import com.ifba.clinic.people.entities.UserRole;
import com.ifba.clinic.people.entities.UserTrait;
import com.ifba.clinic.people.entities.enums.EnumRole;
import com.ifba.clinic.people.exceptions.BadRequestException;
import com.ifba.clinic.people.exceptions.ConflictException;
import com.ifba.clinic.people.exceptions.UnauthorizedException;
import com.ifba.clinic.people.models.requests.CreateUserRequest;
import com.ifba.clinic.people.models.requests.ChangePasswordRequest;
import com.ifba.clinic.people.models.response.CreateUserResponse;
import com.ifba.clinic.people.repositories.UserRepository;
import com.ifba.clinic.people.security.annotations.AuthRequired;
import com.ifba.clinic.people.security.annotations.RoleRestricted;
import com.ifba.clinic.people.security.services.AuthenticationService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.people.utils.Messages.PASSWORD_MUST_BE_DIFFERENT;
import static com.ifba.clinic.people.utils.Messages.USER_DUPLICATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final AuthenticationService authenticationService;
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

    return new CreateUserResponse(savedUser, authenticationService.generateToken(savedUser));
  }

  @AuthRequired
  public void changePassword(ChangePasswordRequest request) {
    User currentUser = getCurrentUser();

    log.info("Changing password for user with email: {}", currentUser.getEmail());

    if (passwordEncoder.matches(request.newPassword(), currentUser.getPassword())) {
      log.error("New password cannot be the same as the current password for user with email: {}",
          currentUser.getEmail());

      throw new BadRequestException(PASSWORD_MUST_BE_DIFFERENT);
    }

    currentUser.setPassword(passwordEncoder.encode(request.newPassword()));
    currentUser.getTraits().removeIf(trait -> trait.getTrait().equals("MUST_CHANGE_PASSWORD"));

    userRepository.save(currentUser);

    log.info("Password reset successfully for user with email: {}", currentUser.getEmail());
  }

  @AuthRequired
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.error("No authenticated user found in the security context");
      throw new IllegalStateException("No authenticated user found");
    }

    log.info("Retrieving currently authenticated user from context. {}", authentication);

    User user = authentication.getPrincipal() instanceof User
        ? (User) authentication.getPrincipal()
        : null;

    if (user != null) {
      return user;
    }

    log.error("Authenticated user not found in context.");

    throw  new UnauthorizedException("Authenticated user not found");
  }

  @Transactional
  public void removeTraitsFromUser(String userId, List<String> traits) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    removeTraitsFromUser(user, traits);
  }

  public void removeTraitsFromUser(User user, List<String> traits) {
    user.getTraits().removeIf(trait -> traits.contains(trait.getTrait()));

    userRepository.save(user);

    log.info("Removed traits {} from user with ID: {}", traits, user.getId());
  }

  @Transactional
  public void addTraitToUser(String userId, String trait) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    UserTrait userTrait = UserTrait.builder()
        .trait(trait)
        .deleted(false)
        .user(user)
        .build();

    if (user.getTraits().stream().anyMatch(check -> check.getTrait().equals(trait))) {
      log.info("User with ID: {} already has trait '{}'", user.getId(), trait);
      return;
    }

    user.getTraits().add(userTrait);

    userRepository.save(user);

    log.info("Added trait '{}' to user with ID: {}", trait, user.getId());
  }

}
