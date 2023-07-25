package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {

    private final PageRequest pr = PageRequest.of(0, 20);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    private Booking bookingAtMomentWithStatusWaiting;
    private Booking bookingInPastWithStatusRejected;
    private Booking bookingInFutureWithStatusApproved;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));
        item1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user1));
        item2 = itemRepository.save(makeItem("Инструмент2", "Описание2", true, user2));
        bookingAtMomentWithStatusWaiting = bookingRepository.save(makeBooking(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), item1, user2, StatusType.WAITING));
        bookingInPastWithStatusRejected = bookingRepository.save(makeBooking(LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1), item1, user2, StatusType.REJECTED));
        bookingInFutureWithStatusApproved = bookingRepository.save(makeBooking(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), item2, user1, StatusType.APPROVED));
    }

    @Test
    void verifyFindAllByBookerIdOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(user2.getId(), pr);

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
        assertEquals(bookings.get(1), bookingInPastWithStatusRejected);
    }

    @Test
    void verifyFindAllByBookerIdAndStatusEqualsOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusEqualsOrderByStartDesc(user2.getId(),
                StatusType.WAITING,
                pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
    }

    @Test
    void verifyFindAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user2.getId(),
                LocalDateTime.now(), LocalDateTime.now(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
    }

    @Test
    void verifyFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(user2.getId(),
                LocalDateTime.now(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingInPastWithStatusRejected);
    }

    @Test
    void verifyFindAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(user1.getId(),
                LocalDateTime.now(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingInFutureWithStatusApproved);
    }

    @Test
    void verifyFindAllBookingsByItemOwner() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwner(user1.getId(), pr);

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
        assertEquals(bookings.get(1), bookingInPastWithStatusRejected);
    }

    @Test
    void verifyFindAllBookingsByItemOwnerWithStatusWaiting() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwnerWithStatusWaiting(user1.getId(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
    }

    @Test
    void verifyFindAllBookingsByItemOwnerWithStatusCanceled() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwnerWithStatusCanceled(user1.getId(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingInPastWithStatusRejected);
    }

    @Test
    void verifyFindAllBookingsByItemOwnerCurrent() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwnerCurrent(user1.getId(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
    }

    @Test
    void verifyFindAllBookingsByItemOwnerPast() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwnerPast(user1.getId(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingInPastWithStatusRejected);
    }

    @Test
    void verifyFindAllBookingsByItemOwnerFuture() {
        List<Booking> bookings = bookingRepository.findAllBookingsByItemOwnerFuture(user2.getId(), pr);

        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0), bookingInFutureWithStatusApproved);
    }

    @Test
    void verifyFindLastBookingByItemId() {
        Optional<Booking> lastBooking = bookingRepository.findLastBookingByItemId(item1.getId());
        Booking result = lastBooking.orElse(null);

        assertNotNull(result);
        assertEquals(result, bookingAtMomentWithStatusWaiting);
        assertNotEquals(result.getStatus(), StatusType.REJECTED);
        assertNotEquals(result.getStatus(), StatusType.CANCELED);
    }

    @Test
    void verifyFindNextBookingByItemId() {
        Optional<Booking> lastBooking = bookingRepository.findNextBookingByItemId(item2.getId());
        Booking result = lastBooking.orElse(null);

        assertNotNull(result);
        assertEquals(result, bookingInFutureWithStatusApproved);
        assertNotEquals(result.getStatus(), StatusType.REJECTED);
        assertNotEquals(result.getStatus(), StatusType.CANCELED);
    }

    @Test
    void verifyExistsByItemIdAndBookerIdAndStatusAndEndIsBefore() {
        boolean result = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndIsBefore(item1.getId(), user2.getId(),
                StatusType.REJECTED, LocalDateTime.now());

        assertTrue(result);
    }

    private Item makeItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return item;
    }

    private User makeUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Booking makeBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, StatusType status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);

        return booking;
    }
}
