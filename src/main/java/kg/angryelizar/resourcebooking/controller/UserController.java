package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.angryelizar.resourcebooking.dto.UserDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Работа с пользователями", description = "Регистрация и другие манипуляции")
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Регистрация пользователя (доступно только для анонимных пользователей)", tags = "user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Такой пользователь уже зарегистрирован или BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "409", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registration(@RequestBody @Valid UserDTO userDTO) {
        return ResponseEntity.status(userService.create(userDTO)).body(HttpStatus.CREATED);
    }
}
