package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.angryelizar.resourcebooking.dto.BookingProfileReadDTO;
import kg.angryelizar.resourcebooking.dto.ResourceReadDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.BookingService;
import kg.angryelizar.resourcebooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Личный профиль", description = "Просмотр бронирований и платежей для пользователей с ролью USER")
public class ProfileController {
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @GetMapping("/bookings")
    @Operation(summary = "Просмотр всех бронирований (подтвержденных и неподтвержденных)", tags = "Profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно", content = @Content(schema = @Schema(implementation = BookingProfileReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<BookingProfileReadDTO>> bookingsProfile(Authentication authentication) {
        return ResponseEntity.ok(bookingService.findAllForUser(authentication));
    }

}
