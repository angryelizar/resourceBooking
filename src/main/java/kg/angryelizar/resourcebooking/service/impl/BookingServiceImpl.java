package kg.angryelizar.resourcebooking.service.impl;

import jakarta.transaction.Transactional;
import kg.angryelizar.resourcebooking.dto.*;
import kg.angryelizar.resourcebooking.enums.Authority;
import kg.angryelizar.resourcebooking.exceptions.BookingException;
import kg.angryelizar.resourcebooking.exceptions.ResourceException;
import kg.angryelizar.resourcebooking.exceptions.UserException;
import kg.angryelizar.resourcebooking.model.Booking;
import kg.angryelizar.resourcebooking.model.Resource;
import kg.angryelizar.resourcebooking.model.User;
import kg.angryelizar.resourcebooking.repository.BookingRepository;
import kg.angryelizar.resourcebooking.repository.PaymentRepository;
import kg.angryelizar.resourcebooking.repository.ResourceRepository;
import kg.angryelizar.resourcebooking.repository.UserRepository;
import kg.angryelizar.resourcebooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ResourceRepository resourceRepository;

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    private static final String NAME_PATTERN = "%s %s";
    private final UserRepository userRepository;
    @Value("${spring.application.limitDaysToBook}")
    private Long limitDaysToBook;
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден!";
    private static final String BOOKING_NOT_FOUND_MESSAGE = "Бронирование не найдено!";

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
    @Transactional
    public BookingSavedDTO create(Long resourceId, BookingCreateDTO bookingCreateDTO, Authentication authentication) {

        if (Boolean.FALSE.equals(isAvailableForBooking(resourceId))) {
            log.error("Ресурс {} недоступен для бронирования - сущность деактивирована или не существует", resourceId);
            throw new ResourceException(String.format("Ресурс %s недоступен для бронирования - сущность деактивирована или не существует", resourceId));
        }

        if (bookingCreateDTO.startDate().equals(bookingCreateDTO.endDate())) {
            log.error("{} и {}: одинаковые даты, бронирование невозможно", bookingCreateDTO.startDate(), bookingCreateDTO.endDate());
            throw new ResourceException(String.format("%s и %s: одинаковые даты, бронирование невозможно", bookingCreateDTO.startDate(), bookingCreateDTO.endDate()));
        }

        Resource resource = resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceException("Ресурс не найден, ID " + resourceId));
        if (Boolean.FALSE.equals(isBookingPossible(bookingCreateDTO.startDate(), bookingCreateDTO.endDate(), resource))) {
            throw new ResourceException("Бронирование недоступно, есть перекрытие по другой брони либо вы пытаетесь забронировать слишком заранее :)");
        }

        User author = userRepository.getByEmail(authentication.getName()).orElseThrow(() -> new UserException(USER_NOT_FOUND_MESSAGE));


        Booking booking = Booking.builder()
                .startDate(bookingCreateDTO.startDate())
                .endDate(bookingCreateDTO.endDate())
                .isConfirmed(false)
                .resource(resource)
                .author(author)
                .build();
        booking.setUpdatedBy(author);
        // Расчет суммы (получаем BigDecimal, который является суммой за аренду ресурса)
        BigDecimal amount = getAmountForBooking(bookingCreateDTO.startDate(), bookingCreateDTO.endDate(), resource.getHourlyRate());
        booking = bookingRepository.save(booking);
        log.info("Получено новое бронирование: {} (ID {}), c {} до {}, автор - {} {}, статус подтверждения -  {}",
                booking.getResource().getTitle(), booking.getId(), booking.getStartDate(), booking.getEndDate(),
                booking.getAuthor().getName(), booking.getAuthor().getSurname(), booking.getIsConfirmed());
        return new BookingSavedDTO(resource.getTitle(), booking.getId(), booking.getStartDate(), booking.getEndDate(),
                String.format(NAME_PATTERN, booking.getAuthor().getName(), booking.getAuthor().getSurname()),
                amount.doubleValue(), booking.getIsConfirmed());
    }

    @Override
    public BigDecimal getAmountForBooking(LocalDateTime startDate, LocalDateTime endDate, BigDecimal hourlyRate) {
        // Вычисляем количество секунд между двумя датами
        long resultBetweenInSeconds = ChronoUnit.SECONDS.between(startDate, endDate);

        // Преобразуем секунды в часы (включая дробную часть)
        double resultBetweenInHours = resultBetweenInSeconds / 3600.0;
        log.info("Часов между двумя датами - {}", resultBetweenInHours);

        // Вычисляем итоговую сумму на основе почасовой ставки
        return BigDecimal.valueOf(resultBetweenInHours).multiply(hourlyRate);
    }


    @Override
    public List<BookingReadDTO> findAll(Integer page, Integer size, Boolean isConfirmed) {
        return bookingRepository.findByIsConfirmed(isConfirmed, PageRequest.of(page, size)).getContent().stream().map(this::toDTO).toList();
    }

    @Override
    public List<BookingProfileReadDTO> findAllForUser(Authentication authentication, Integer page, Integer size) {
        User author = userRepository.getByEmail(authentication.getName()).orElseThrow(() -> new UserException(USER_NOT_FOUND_MESSAGE));
        return bookingRepository.findByAuthor(author, PageRequest.of(page, size)).getContent().stream().map(this::toProfileDTO).toList();
    }

    private Boolean isOverlapLocalDateTime(LocalDateTime a, LocalDateTime b, LocalDateTime c, LocalDateTime d) {
        //// Вычисляем, нет ли пересечений в наших временных интервалах.
        //// Переводим все 4 даты в целое количественное значение секунд, прошедших с 1 января 1970 года.
        //// Это необходимо нам, для того, чтобы по формуле (min(B, D) - max(A, C)) >= 0 понять есть ли пересечения.
        //// Если значение больше 0 - пересечение есть и бронь невозможна

        long aRes = a.toEpochSecond(ZONE_OFFSET);
        long bRes = b.toEpochSecond(ZONE_OFFSET);
        long cRes = c.toEpochSecond(ZONE_OFFSET);
        long dRes = d.toEpochSecond(ZONE_OFFSET);

        // A: Дата начала первого диапазона дат (бронь 1).
        // B: дата окончания первого диапазона дат (бронь 1).
        // C: дата начала второго диапазона дат (бронь 2).
        // D: дата окончания второго диапазона дат (бронь 2).
        return (Math.min(bRes, dRes) - Math.max(aRes, cRes)) > 0;
    }

    @Override
    public Boolean isBookingPossible(LocalDateTime start, LocalDateTime end, Resource resource) {

        LocalDate startDate = start.toLocalDate();
        List<Booking> bookings;

        /// Если бронирование слишком заранее (например за 30, 300, 3000 дней - зависит от нашего лимита) -
        // мы не лезем в БД для проверки перекрытия дат и перебора всех броней, а отклоняем бронь
        Long daysBetweenStart = calculateDaysBetween(LocalDate.now(), startDate);
        if (daysBetweenStart > limitDaysToBook) {
            log.error("Попытка забронировать бронь более чем за {} дней", limitDaysToBook);
            return false;
        }
        bookings = bookingRepository.findByIsConfirmedAndResource(true, resource, start, end);
        return !isOverlapBookings(start, end, bookings);
    }

    @Override
    public HttpStatus delete(Long bookingId, Authentication authentication) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingException(BOOKING_NOT_FOUND_MESSAGE));
        User user = userRepository.getByEmail(authentication.getName()).orElseThrow(
                () -> new UserException(USER_NOT_FOUND_MESSAGE));
        if (user.getAuthority().getAuthority().equals(Authority.USER.getName())) {
            return deleteBookingByUser(booking, user);
        }
        return deleteBookingByAdministrator(booking);
    }

    @Override
    public BookingReadDTO edit(BookingUpdateDTO bookingUpdateDTO, Authentication authentication, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new BookingException(BOOKING_NOT_FOUND_MESSAGE));
        User user = userRepository.getByEmail(authentication.getName()).orElseThrow(
                () -> new UserException(USER_NOT_FOUND_MESSAGE));

        List<Booking> bookingsForCheck = bookingRepository.findByIsConfirmedAndResource(
                true, booking.getResource(), bookingUpdateDTO.startDate(), bookingUpdateDTO.endDate());

        /// При проверке на возможность даты бронирования убираем из списка на сравнение нашу же бронь
        bookingsForCheck.removeIf(e -> e.getId().equals(bookingId));

        if (Boolean.TRUE.equals(isOverlapBookings(bookingUpdateDTO.startDate(), bookingUpdateDTO.endDate(), bookingsForCheck))) {
            throw new ResourceException(
                    "Бронирование недоступно, есть перекрытие по другой брони, " +
                            "либо вы пытаетесь забронировать слишком заранее :)");
        }

        booking.setUpdatedBy(user);
        booking.setIsConfirmed(bookingUpdateDTO.isConfirmed());
        booking.setStartDate(bookingUpdateDTO.startDate());
        booking.setEndDate(bookingUpdateDTO.endDate());

        return toDTO(bookingRepository.saveAndFlush(booking));
    }

    private HttpStatus deleteBookingByAdministrator(Booking booking) {
        if (Boolean.FALSE.equals(isDeletable(booking))) {
            throw new BookingException("Удаление бронирования невозможно - оно уже оплачено!");
        }
        bookingRepository.delete(booking);
        return HttpStatus.OK;
    }

    public HttpStatus deleteBookingByUser(Booking booking, User user) {
        if (!user.getEmail().equals(booking.getAuthor().getEmail())) {
            log.error("Попытка удалить чужую бронь с ID {} пользователем {}", booking.getId(), user.getEmail());
            throw new BookingException("Это не ваше бронирование!");
        }
        if (Boolean.FALSE.equals(isDeletable(booking))) {
            log.error("Удаление бронирования с ID {} невозможно по причине наличия связанного с бронью платежа", booking.getId());
            throw new BookingException("Удаление бронирования невозможно - оно уже оплачено!");
        }
        bookingRepository.delete(booking);
        return HttpStatus.OK;
    }

    private Boolean isDeletable(Booking booking) {
        return paymentRepository.findAllByBooking(booking).isEmpty();
    }

    private Boolean isOverlapBookings(LocalDateTime start, LocalDateTime end, List<Booking> bookings) {
        for (Booking booking : bookings) {
            if (Boolean.TRUE.equals(isOverlapLocalDateTime(start, end, booking.getStartDate(), booking.getEndDate()))) {
                log.error("Есть перекрытие по брони!");
                log.error("Бронь с ID {} пересекается с бронью по дате {} (старт) и {} (конец)", booking.getId(), start, end);
                return true;
            }
        }
        return false;
    }

    private Long calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
    }

    @Override
    public Boolean isAvailableForBooking(Long resourceId) {
        Optional<Resource> resource = resourceRepository.findById(resourceId);
        if (resource.isPresent()) {
            return resource.get().getIsActive();
        }
        return false;
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

    private BookingProfileReadDTO toProfileDTO(Booking booking) {
        return new BookingProfileReadDTO(booking.getResource().getTitle(), booking.getResource().getId(), booking.getId(),
                booking.getStartDate(), booking.getEndDate(), booking.getIsConfirmed(),
                booking.getCreatedAt(), booking.getUpdatedAt());
    }
}