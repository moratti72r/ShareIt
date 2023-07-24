package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static java.text.MessageFormat.format;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addBooking(long userId, BookingDto bookingDto) {

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new IncorrectArgumentException(BookingRepository.class);
        }
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.existsById(bookingDto.getItemId())) {
            throw new NotFoundException(ItemRepository.class);
        }
        Booking booking = BookingMapper.fromBookingDto(bookingDto);

        Optional<Item> itemOptional = itemRepository.findById(bookingDto.getItemId());
        Item item = itemOptional.get();
        if (!item.getAvailable()) {
            throw new IncorrectArgumentException(BookingRepository.class);
        }
        if (item.getOwner().getId() == userId) {
            throw new NotFoundException(BookingRepository.class);
        }
        booking.setItem(item);

        Optional<User> userOptional = userRepository.findById(userId);
        booking.setBooker(userOptional.get());

        booking.setStatus(StatusType.WAITING);

        Booking result = bookingRepository.save(booking);
        log.info("Бронирование c id={} от Пользователя с id={} Продукта с id={} добавлено",
                result.getId(), userId, result.getItem().getId());
        return result;
    }

    @Override
    public Booking changeBookingStatus(long userId, long bookingId, boolean answer) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Item item = booking.getItem();
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException(UserRepository.class);
            }
            if (item.getOwner().getId() != userId) {
                throw new NotFoundException(BookingService.class);
            }

            StatusType bookingStatus;
            if (answer) {
                bookingStatus = StatusType.APPROVED;
            } else {
                bookingStatus = StatusType.REJECTED;
            }

            if (booking.getStatus() == bookingStatus) {
                throw new IncorrectArgumentException(BookingRepository.class);
            } else {
                booking.setStatus(bookingStatus);
            }

            bookingRepository.save(booking);
            log.info("Изменение статуса Бронирования c id={} от Пользователя с id={} на статус {} прошло успешно",
                    booking.getId(), userId, bookingStatus);
            return booking;
        } else throw new NotFoundException(BookingRepository.class);
    }

    @Override
    public Booking findBookingById(long userId, long bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            Item item = booking.getItem();
            User user = booking.getBooker();
            if (item.getOwner().getId() != userId && user.getId() != userId) {
                throw new NotFoundException(BookingRepository.class);
            }
            log.info("Бронирование c id={} получен Пользователем с id={}", booking.getId(), userId);
            return booking;
        } else throw new NotFoundException(BookingRepository.class);
    }

    @Override
    public List<Booking> findAllBookingsByParameterAndBooker(long userId, String parameter, int from, int size) {
        ApproveStatus status = ApproveStatus.toApproveStatus(parameter);
        if (status == null) throw new InternalServerException(format("Unknown state: {0}", parameter));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Booking> result = new ArrayList<>();
        switch (status) {
            case ALL:
                result = bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, StatusType.WAITING, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(userId, StatusType.REJECTED, pageRequest);
                break;
        }
        log.info("Список Бронирований получен Пользователем(бронирующий) с id={} по параметру {}", userId, parameter);
        return result;
    }

    @Override
    public List<Booking> findAllBookingsByParameterAndItemOwner(long userId, String parameter, int from, int size) {
        ApproveStatus status = ApproveStatus.toApproveStatus(parameter);

        if (status == null) throw new InternalServerException(format("Unknown state: {0}", parameter));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<Booking> result = new ArrayList<>();
        switch (status) {
            case ALL:
                result = bookingRepository.findAllBookingsByItemOwner(userId, pageRequest);
                break;
            case CURRENT:
                result = bookingRepository.findAllBookingsByItemOwnerCurrent(userId, pageRequest);
                break;
            case PAST:
                result = bookingRepository.findAllBookingsByItemOwnerPast(userId, pageRequest);
                break;
            case FUTURE:
                result = bookingRepository.findAllBookingsByItemOwnerFuture(userId, pageRequest);
                break;
            case WAITING:
                result = bookingRepository.findAllBookingsByItemOwnerWithStatusWaiting(userId, pageRequest);
                break;
            case REJECTED:
                result = bookingRepository.findAllBookingsByItemOwnerWithStatusCanceled(userId, pageRequest);
                break;
        }
        log.info("Список Бронирований получен Пользователем(владелец вещи) с id={} по параметру {}", userId, parameter);
        return result;
    }

}
