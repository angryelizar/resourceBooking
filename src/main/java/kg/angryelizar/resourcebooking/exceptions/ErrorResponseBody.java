package kg.angryelizar.resourcebooking.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "DTO для просмотра информации о произошедшей ошибке")
public class ErrorResponseBody {
    @Schema(description = "Название ошибки", example = "Ошибка авторизации")
    private String title;
    @Schema(description = "Причины ошибки")
    private List<String> reasons;
}
