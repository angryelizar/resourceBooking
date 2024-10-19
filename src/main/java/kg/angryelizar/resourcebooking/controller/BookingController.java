package kg.angryelizar.resourcebooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingReadDTO;
import kg.angryelizar.resourcebooking.dto.BookingSavedDTO;
import kg.angryelizar.resourcebooking.dto.BookingUpdateDTO;
import kg.angryelizar.resourcebooking.exceptions.ErrorResponseBody;
import kg.angryelizar.resourcebooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@Tag(name = "Управление бронированиями",
        description = "Бронирование ресурса, отмена брони и ее оплата для обычного пользователя, а также просмотр, редактирование и удаление для администратора")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Просмотр всех бронирований с фильтрацией по подтвержденным и неподтвержденным, а также поддержкой пагинации. Доступно только администраторам!",
            tags = "Booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно",
                    content = @Content(schema = @Schema(implementation = BookingReadDTO.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не администратор",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<List<BookingReadDTO>> getBookings(
            @Parameter(description = "Номер страницы (начинается от 0)") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Размер страницы (по умолчанию - 5)") @RequestParam(defaultValue = "5") Integer size,
            @Parameter(description = "Показать подтвержденные или неподтвержденные бронирования (по умолчанию показывает подтвержденные, isConfirmed = true)")
            @RequestParam(defaultValue = "true") Boolean isConfirmed) {
        return ResponseEntity.ok(bookingService.findAll(page, size, isConfirmed));
    }

    @PutMapping("/{bookingId}")
    @Operation(summary = "Редактирование бронирования администраторами", tags = "Booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно - бронирование отредактировано",
                    content = @Content(schema = @Schema(implementation = BookingReadDTO.class))),
            @ApiResponse(responseCode = "409", description = "Произошла ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не обычный юзер",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<BookingReadDTO> editBooking(
            @PathVariable @Parameter(description = "Идентификатор бронирования") Long bookingId,
            @RequestBody @Valid BookingUpdateDTO bookingUpdateDTO,
            Authentication authentication
    ){
        return ResponseEntity.ok(bookingService.edit(bookingUpdateDTO, authentication, bookingId));
    }

    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Удаление бронирования для администраторов и пользователей. Пользователи могут удалить только свои брони, а администраторы - любые",
            tags = "Booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно - бронирование удалено",
                    content = @Content(schema = @Schema(implementation = HttpStatus.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))})
    public ResponseEntity<HttpStatus> delete(
            @PathVariable @Parameter(description = "Идентификатор бронирования") Long bookingId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookingService.delete(bookingId, authentication));
    }

    @PostMapping("/resources/{resourceId}")
    @Operation(summary = "Бронирование ресурса (доступно только обычному пользователю). После успешной брони в ответе выдается ID бронирования, который будет необходим для проведения оплаты",
            tags = "Booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос прошел успешно - бронирование создано",
                    content = @Content(schema = @Schema(implementation = BookingSavedDTO.class))),
            @ApiResponse(responseCode = "409", description = "Произошла ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "400", description = "Произошла ошибка при выполнении запроса",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен - скорее всего вы не обычный юзер",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(schema = @Schema(implementation = ErrorResponseBody.class)))
    })
    public ResponseEntity<BookingSavedDTO> booking(
            @PathVariable @Parameter(description = "Идентификатор ресурса") Long resourceId,
            @Valid @RequestBody BookingCreateDTO bookingCreateDTO,
            Authentication authentication) {
        return ResponseEntity.ok(bookingService.create(resourceId, bookingCreateDTO, authentication));
    }

}
