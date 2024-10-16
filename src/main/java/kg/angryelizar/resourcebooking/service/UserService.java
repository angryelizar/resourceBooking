package kg.angryelizar.resourcebooking.service;

import kg.angryelizar.resourcebooking.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    HttpStatus create(UserDTO userDTO);
}
