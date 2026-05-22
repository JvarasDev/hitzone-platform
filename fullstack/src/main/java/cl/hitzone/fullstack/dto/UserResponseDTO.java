package cl.hitzone.fullstack.dto;

import cl.hitzone.fullstack.Model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private LocalDateTime createdAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles().stream().map(r -> r.getNameRol()).collect(Collectors.toList());
        this.createdAt = user.getCreated_at();
    }
}
