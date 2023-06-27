package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {


    Booking addBooking(long userId, BookingDto bookingDto);

    Booking changeBookingStatus(long user, long bookingId, boolean answer);

    Booking findBookingById(long userId, long bookingId);

    List<Booking> findAllBookingsByParameterAndBooker(long userId, String parameter);

    List<Booking> findAllBookingsByParameterAndItemOwner(long userId, String parameter);
}
