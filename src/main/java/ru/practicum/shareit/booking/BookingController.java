package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") long idUser,
                          @RequestBody @Valid BookingDto bookingDto) {
        log.info("Получен POST запрос /bookings");
        return bookingService.addBooking(idUser, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable long bookingId,
                                @RequestParam boolean approved) {
        log.info("Получен PATCH запрос /bookings/{}", bookingId);
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Получен GET запрос /bookings/{}", bookingId);
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> findAllBookingsByParameterAndBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен GET запрос /bookings?state={}", state);
        return bookingService.findAllBookingsByParameterAndBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> findAllBookingsByParameterAndItemOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получен GET запрос /bookings/owner?state={}", state);
        return bookingService.findAllBookingsByParameterAndItemOwner(userId, state);
    }
}
