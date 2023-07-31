package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Controller
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> create(@RequestHeader("X-Sharer-User-Id") long idUser,
                                          @RequestBody BookingDto bookingDto) {
        log.info("Получен POST запрос /bookings от пользователя {}", idUser);
        return ResponseEntity.ok(bookingService.addBooking(idUser, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Booking> updateStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId,
                                                @RequestParam boolean approved) {
        log.info("Получен PATCH запрос /bookings/{}", bookingId);
        return ResponseEntity.ok(bookingService.changeBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        log.info("Получен GET запрос /bookings/{}", bookingId);
        return ResponseEntity.ok(bookingService.findBookingById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<List<Booking>> findAllBookingsByParameterAndBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                             @RequestParam(required = false) String state,
                                                                             @RequestParam(required = false) Integer from,
                                                                             @RequestParam(required = false) Integer size) {
        log.info("Получен GET запрос /bookings?state={}", state);
        return ResponseEntity.ok(bookingService.findAllBookingsByParameterAndBooker(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<Booking>> findAllBookingsByParameterAndItemOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                                @RequestParam(required = false) String state,
                                                                                @RequestParam(required = false) Integer from,
                                                                                @RequestParam(required = false) Integer size) {
        log.info("Получен GET запрос /bookings/owner?state={}", state);
        return ResponseEntity.ok(bookingService.findAllBookingsByParameterAndItemOwner(userId, state, from, size));
    }
}
