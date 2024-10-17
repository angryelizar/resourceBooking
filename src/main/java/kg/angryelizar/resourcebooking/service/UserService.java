package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.UserDTO;
import kg.angryelizar.resourcebooking.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    HttpStatus create(UserDTO userDTO);
    Boolean isAdministrator(User user);
    User checkAdministrator(Authentication authentication);
}
