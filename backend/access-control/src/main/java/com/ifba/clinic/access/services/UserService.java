package com.ifba.clinic.access.services;

import com.ifba.clinic.access.entities.User;
import com.ifba.clinic.access.entities.UserRole;
import com.ifba.clinic.access.entities.UserTrait;
import com.ifba.clinic.access.entities.enums.EnumRole;
import com.ifba.clinic.access.exceptions.BadRequestException;
import com.ifba.clinic.access.exceptions.ConflictException;
import com.ifba.clinic.access.exceptions.UnauthorizedException;
import com.ifba.clinic.access.messaging.roles.models.UserRoleDroppedEvent;
import com.ifba.clinic.access.models.requests.CreateUserRequest;
import com.ifba.clinic.access.models.requests.ChangePasswordRequest;
import com.ifba.clinic.access.models.response.CreateUserResponse;
import com.ifba.clinic.access.models.response.UserRoleResponse;
import com.ifba.clinic.access.models.response.ValidateUserResponse;
import com.ifba.clinic.access.repositories.UserRepository;
import com.ifba.clinic.access.repositories.UserRoleRepository;
import com.ifba.clinic.access.security.annotations.AuthRequired;
import com.ifba.clinic.access.security.services.AuthenticationService;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.ifba.clinic.access.utils.Messages.PASSWORD_MUST_BE_DIFFERENT;
import static com.ifba.clinic.access.utils.Messages.USER_DUPLICATED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
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
      throw new IllegalStateException("No authenticated user found");
    }

    User user = authentication.getPrincipal() instanceof User
        ? (User) authentication.getPrincipal()
        : null;

    if (user != null) {
      return user;
    }

    throw new UnauthorizedException("Authenticated user not found");
  }

  @Transactional
  public void addRoleToUser(String userId, EnumRole role, String entityId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    UserRole userRole = UserRole.builder()
        .role(role)
        .referencedEntityId(entityId)
        .deleted(false)
        .user(user)
        .build();

    if (user.getRoles().stream().anyMatch(check -> check.getRole().equals(role))) {
      log.info("User with ID: {} already has role '{}'", user.getId(), role.name());
      return;
    }

    user.getRoles().add(userRole);

    userRepository.save(user);

    log.info("Added role '{}' to user with ID: {}", role.name(), user.getId());
  }

  @Transactional
  public void handleUserRoleDroppedEvent(UserRoleDroppedEvent event) {
    String entityId = event.entityId();
    EnumRole role = EnumRole.valueOf(event.role());

    Optional<UserRole> optionalUserRole = userRoleRepository.findByReferencedEntityIdAndRole(entityId, role);

    if (optionalUserRole.isEmpty()) {
      log.info("No user role found for entity ID: {} and role: {}", entityId, role.name());
      return;
    }

    UserRole userRole = optionalUserRole.get();

    userRoleRepository.delete(userRole);

    log.info("Removed role '{} - {}' from user with ID: {}", role.name(), entityId, userRole.getUser().getId());

    ensureAtLeastOneRole(userRole.getUser().getId(), event);
  }

  @Transactional
  protected void ensureAtLeastOneRole(String userId, UserRoleDroppedEvent event) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    log.info("Ensuring user with ID: {} has at least one role - {}", user.getId(), user.getRoles());

    boolean isUserRolesAlreadyEmpty = user.getRoles().isEmpty();
    boolean isUserUniqueRoleDropped = false;

    if (!isUserRolesAlreadyEmpty) {
      UserRole firstRole = user.getRoles().get(0);

      boolean isSameRole = firstRole.getRole().equals(EnumRole.valueOf(event.role()));
      boolean isSameEntity = (firstRole.getReferencedEntityId() == null && event.entityId() == null) ||
          (firstRole.getReferencedEntityId() != null && firstRole.getReferencedEntityId().equals(event.entityId()));

      isUserUniqueRoleDropped = isSameRole && isSameEntity;
    }

    if (user.getRoles().isEmpty() || isUserUniqueRoleDropped) {
      userRepository.delete(user);

      log.info("Deleted user with ID: {} as they had no roles left", user.getId());
    }
  }

  public boolean hasAnyTrait(String userId, List<String> traits) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BadRequestException("User not found"));

    return user.getTraits().stream()
        .anyMatch(trait -> traits.contains(trait.getTrait()));
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

  public ValidateUserResponse getAuthenticatedUser() {
    User user = getCurrentUser();

    return new ValidateUserResponse(
        user.getId(),
        user.getEmail(),
        user.getRoles().stream().map(UserRoleResponse::new).toList(),
        user.getTraits().stream().map(UserTrait::getTrait).toList()
    );
  }

}
