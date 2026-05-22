package cl.hitzone.fullstack.Service;

import cl.hitzone.fullstack.dto.RoleStatsDTO;
import cl.hitzone.fullstack.dto.UserRegistrationDTO;
import cl.hitzone.fullstack.dto.UserResponseDTO;

import java.util.List;

public interface AuthService {
    UserResponseDTO registerUser(UserRegistrationDTO dto);
    UserResponseDTO authenticate(String username, String password);
    long getTotalUsersCount();
    List<RoleStatsDTO> getUsersCountByRole();
    List<UserResponseDTO> searchUsersByUsername(String username);
}

