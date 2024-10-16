package kg.angryelizar.resourcebooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "DTO для регистрации пользователя")
public record UserDTO(
        @NotEmpty(message = "Имя не может быть пустым!")
        @NotNull(message = "Имя не может быть пустым!")
        @Schema(description = "Имя пользователя", example = "Дастан")
        String name,
        @NotEmpty(message = "Фамилия не может быть пустой!")
        @NotNull(message = "Фамилия не может быть пустой!")
        @Schema(description = "Фамилия пользователя", example = "Жапаров")
        String surname,
        @Email(message = "Это не очень похоже на электронную почту...")
        @NotEmpty(message = "С пустой почтой продолжить процесс будет невозможно!")
        @Schema(description = "Электронная почта пользователя", example = "japarovdastan@dastan.kg")
        String email,
        @NotEmpty(message = "Пароль не может быть пустым!")
        @Size(min = 6, message = "Пароль должен содержать как минимум 6 символов!")
        @Schema(description = "Пароль пользователя", example = "qwerty")
        String password) {
}
