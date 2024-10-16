package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.UserDTO;
import kg.angryelizar.resourcebooking.enums.Authority;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.AuthorityRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    @Override
    public HttpStatus create(UserDTO userDTO) {
        Optional<User> maybeUser = userRepository.getByEmail(userDTO.email());

        if (maybeUser.isPresent()) {
            log.error("Попытка зарегистрироваться под уже существующей электронной почтой в БД - {}", userDTO.email());
            throw new UserException(String.format("Пользователь с почтой %s уже существует!", userDTO.email()));
        }

       User registeredUser =  userRepository.save(User.builder()
                        .authority(authorityRepository.findByAuthority(Authority.USER.getName()))
                        .name(userDTO.name())
                        .surname(userDTO.surname())
                        .email(userDTO.email())
                        .password(passwordEncoder.encode(userDTO.password()))
                        .isEnabled(true)
                .build());
        log.info("Зарегистрирован новый пользователь -  {}", registeredUser.getEmail());
        return HttpStatus.CREATED;
    }


    @Override
    public Boolean isAdministrator(User user) {
        return user.getAuthority().getAuthority().equalsIgnoreCase(Authority.ADMIN.getName());
    }

    @Override
    public User checkAdministrator(Authentication authentication) {
        Optional<User> maybeAuthor = userRepository.getByEmail(authentication.getName());
        if (maybeAuthor.isEmpty()) {
            log.error("Пользователь {} не найден", authentication.getName());
            throw new UserException(String.format("Пользователь %s не найден", authentication.getName()));
        }

        if (Boolean.FALSE.equals(isAdministrator(maybeAuthor.get()))){
            log.error("Пользователь {} не администратор и не может создать/отредактировать/удалить ресурс!", maybeAuthor.get().getEmail());
            throw new UserException("Вы не администратор для этого действия!");
        }
        return maybeAuthor.get();
    }
}
