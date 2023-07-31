package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @Validated
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid BookingDto bookingDto) {
        log.info("Получен POST запрос /bookings от пользователя {}", userId);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable("bookingId") Long bookingId,
                                               @RequestParam Boolean approved) {
        log.info("Получен PATCH запрос /bookings/{}", bookingId);
        return bookingClient.updateStatus(userId, bookingId, approved);
    }

    @GetMapping
    @Validated
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") String state,
                                               @PositiveOrZero
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET запрос /bookings?state={}", state);
        return bookingClient.getAllByUser(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("bookingId") Long bookingId) {
        log.info("Получен GET запрос /bookings/{}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping("/owner")
    @Validated
    public ResponseEntity<Object> getAllByItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") String state,
                                                @PositiveOrZero
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @Positive
                                                @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET запрос /bookings/owner?state={}", state);
        return bookingClient.getAllByItem(userId, state, from, size);
    }
}
