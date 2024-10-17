package kg.angryelizar.resourcebooking.service.impl;

import kg.angryelizar.resourcebooking.dto.BookingCreateDTO;
import kg.angryelizar.resourcebooking.dto.BookingReadDTO;
import kg.angryelizar.resourcebooking.exceptions.ResourceException;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import kg.angryelizar.resourcebooking.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ResourceService resourceService;
    private static final String NAME_PATTERN = "%s %s";
    @Value("${spring.application.limitDaysToBook}")
    private Long limitDaysToBook;

    @Override
    public void deleteBookingsByResource(Resource resource) {
        List<Booking> bookings = bookingRepository.findByResource(resource);
        for (Booking booking : bookings) {
            paymentRepository.deleteAll(paymentRepository.findAllByBooking(booking));
            log.info("Удалены все платежи для брони c ID {}", booking.getId());
        }
        bookingRepository.deleteAll(bookings);
        log.info("Удалены все бронирования для ресурса {} (ID - {})", resource.getTitle(), resource.getId());
    }

    @Override
    public ResponseEntity<BookingReadDTO> create(Long resourceId, BookingCreateDTO bookingCreateDTO, Authentication authentication) {

        if (!resourceService.isAvailableForBookingById(resourceId)) {
            log.error("Ресурс {} недоступен для бронирования - сущность деактивирована или не существует", resourceId);
            throw new ResourceException(String.format("Ресурс %s недоступен для бронирования - сущность деактивирована или не существует", resourceId));
        }

        if (bookingCreateDTO.startDate().equals(bookingCreateDTO.endDate())) {
            log.error("{} и {}: одинаковые даты, бронирование невозможно", bookingCreateDTO.startDate(), bookingCreateDTO.endDate());
            throw new ResourceException(String.format("%s и %s: одинаковые даты, бронирование невозможно", bookingCreateDTO.startDate(), bookingCreateDTO.endDate()));
        }

        // Проверка доступности брони по времени
        // Расчет суммы
        // Выставление счета
        return null;
    }

    @Override
    public List<BookingReadDTO> findAll(Integer page, Integer size, Boolean isConfirmed) {
        return bookingRepository.findByIsConfirmed(isConfirmed, PageRequest.of(page, size)).getContent().stream().map(this::toDTO).toList();
    }

    private Boolean isOverlapLocalDateTime(LocalDateTime a, LocalDateTime b, LocalDateTime c, LocalDateTime d) {
        //// Вычисляем, нет ли пересечений в наших временных интервалах.
        //// Переводим все 4 даты в целое количественное значение секунд, прошедших с 1 января 1970 года.
        //// Это необходимо нам, для того, чтобы по формуле (min(B, D) - max(A, C)) >= 0 понять есть ли пересечения.
        //// Если значение больше 0 - пересечение есть и бронь невозможна
        ZoneOffset zoneOffset = ZoneOffset.UTC;

        long aRes = a.toEpochSecond(zoneOffset);
        long bRes = b.toEpochSecond(zoneOffset);
        long cRes = c.toEpochSecond(zoneOffset);
        long dRes = d.toEpochSecond(zoneOffset);

        // A: Дата начала первого диапазона дат (бронь 1).
        // B: дата окончания первого диапазона дат (бронь 1).
        // C: дата начала второго диапазона дат (бронь 2).
        // D: дата окончания второго диапазона дат (бронь 2).
        return (Math.min(bRes, dRes) - Math.max(aRes, cRes)) > 0;
    }

    private Boolean isBookingPossible(LocalDateTime start, LocalDateTime end, Resource resource) {

        LocalDate startDate = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        List<Booking> bookings;

        if (startDate.equals(endDate)) {
            bookings = bookingRepository.findByIsConfirmedAndResource(true, resource, start, end);
            return isOverlapBookings(start, end, bookings);
        }
        Long daysBetween = calculateDaysBetween(startDate, endDate);


        /// Если бронирование слишком заранее (например за 30, 300, 3000 дней - зависит от нашего лимита) -
        // мы не лезем в БД и грузим ее для проверки перекрытия дат, а отклоняем бронь
        if (daysBetween > limitDaysToBook) {
            return false;
        }

        bookings = bookingRepository.findByIsConfirmedAndResource(false, resource, start, end);
        return isOverlapBookings(start, end, bookings);
    }

    private Boolean isOverlapBookings(LocalDateTime start, LocalDateTime end, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (Boolean.TRUE.equals(isOverlapLocalDateTime(start, end, booking.getStartDate(), booking.getEndDate()))) {
                return true;
            }
        }
        return false;
    }

    private Long calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    private BookingReadDTO toDTO(Booking booking) {
        User author = booking.getAuthor();
        User updatedBy = booking.getUpdatedBy();
        String authorName = String.format(NAME_PATTERN, author.getName(), author.getSurname());
        String updatedAuthorName = String.format(NAME_PATTERN, updatedBy.getName(), updatedBy.getSurname());
        return new BookingReadDTO(booking.getResource().getTitle(), booking.getResource().getId(), booking.getId(),
                booking.getStartDate(), booking.getEndDate(), booking.getIsConfirmed(), authorName, updatedAuthorName,
                booking.getCreatedAt(), booking.getUpdatedAt());
    }
}
