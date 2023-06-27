package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {

    private long id;

    @NotNull
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    private LocalDateTime end;

    @NotNull
    private long itemId;

    private long bookerId;
}
