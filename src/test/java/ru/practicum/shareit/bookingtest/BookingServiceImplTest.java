package ru.practicum.shareit.bookingtest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.InternalServerException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@TestPropertySource(locations = "classpath:test.properties")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    private final BookingService bookingService;
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final EntityManager entityManager;

    private User user1;
    private User user2;

    private User user3;
    private Item item1;
    private Item item2;

    private Booking bookingAtMomentWithStatusWaiting;
    private Booking bookingInPastWithStatusRejected;
    private Booking bookingInFutureWithStatusApproved;

    private BookingDto bookingDtoAdd;

    @BeforeEach
    void addEntityOnDb() {
        user1 = userRepository.save(makeUser("Viktor", "viktor@mail.ru"));
        user2 = userRepository.save(makeUser("Michail", "misha@mail.ru"));
        user3 = userRepository.save(makeUser("Anna", "anna@mail.ru"));
        item1 = itemRepository.save(makeItem("Инструмент1", "Описание1", true, user1));
        item2 = itemRepository.save(makeItem("Инструмент2", "Описание2", true, user2));
        bookingAtMomentWithStatusWaiting = bookingRepository.save(makeBooking(LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1), item1, user2, StatusType.WAITING));
        bookingInPastWithStatusRejected = bookingRepository.save(makeBooking(LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1), item1, user2, StatusType.REJECTED));
        bookingInFutureWithStatusApproved = bookingRepository.save(makeBooking(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), item2, user1, StatusType.APPROVED));
        bookingDtoAdd = makeBookingDto(LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4), item2.getId());
    }

    @Test
    void addBooking() {

        bookingService.addBooking(user3.getId(), bookingDtoAdd);

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.booker.id = :id", Booking.class);
        Booking booking = query.setParameter("id", user3.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingDtoAdd.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingDtoAdd.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingDtoAdd.getItemId()));
        assertThat(booking.getBooker(), equalTo(user3));
        assertThat(booking.getStatus(), equalTo(StatusType.WAITING));
    }

    @Test
    void catchIncorrectArgumentExceptionWhenAddBookingWithIncorrectTimes() {
        bookingDtoAdd.setStart(LocalDateTime.now().plusHours(2));
        bookingDtoAdd.setEnd(LocalDateTime.now().minusHours(2));

        Exception exception = assertThrows(IncorrectArgumentException.class, () -> {
            bookingService.addBooking(user3.getId(), bookingDtoAdd);
        });
        assertTrue(BookingRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenAddBookingWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(99, bookingDtoAdd);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenAddBookingWithNotExistItem() {

        bookingDtoAdd.setItemId(99);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.addBooking(user3.getId(), bookingDtoAdd);
        });
        assertTrue(ItemRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void changeBookingStatus() {
        bookingService.changeBookingStatus(user2.getId(), bookingInFutureWithStatusApproved.getId(), false);

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.item.id = :id", Booking.class);
        Booking booking = query.setParameter("id", item2.getId())
                .getSingleResult();

        assertThat(booking.getId(), equalTo(bookingInFutureWithStatusApproved.getId()));
        assertThat(booking.getStatus(), equalTo(StatusType.REJECTED));
    }

    @Test
    void catchNotFoundExceptionWhenChangeBookingStatusWithNotExistUser() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.changeBookingStatus(99, bookingInFutureWithStatusApproved.getId(), false);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenChangeBookingStatusWithUserIsNotOwner() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.changeBookingStatus(user1.getId(), bookingInFutureWithStatusApproved.getId(), false);
        });
        assertTrue(BookingService.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenChangeBookingStatusWithNotExistBooking() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.changeBookingStatus(user1.getId(), 99, false);
        });
        assertTrue(BookingRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findBookingByIdWhereUserIsBooker() {
        Booking result = bookingService.findBookingById(user2.getId(), bookingAtMomentWithStatusWaiting.getId());

        assertEquals(result, bookingAtMomentWithStatusWaiting);
    }

    @Test
    void findBookingByIdWhereUserIsOwner() {
        Booking result = bookingService.findBookingById(user1.getId(), bookingAtMomentWithStatusWaiting.getId());

        assertEquals(result, bookingAtMomentWithStatusWaiting);
    }

    @Test
    void catchNotFoundExceptionWhenFindBookingByIdWhereUserIsNotOwnerAndBooker() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.findBookingById(user3.getId(), bookingAtMomentWithStatusWaiting.getId());
        });
        assertTrue(BookingRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchNotFoundExceptionWhenFindBookingByIdWhereNotExistBooking() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.findBookingById(user3.getId(), 99);
        });
        assertTrue(BookingRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void findAllBookingsByParameterIsWaitingAndBooker() {
        List<Booking> bookings = bookingService.findAllBookingsByParameterAndBooker(user2.getId(), "WAITING", 0, 20);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);

    }

    @Test
    void findAllBookingsByParameterIsAllAndBooker() {
        List<Booking> bookings = bookingService.findAllBookingsByParameterAndBooker(user2.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
        assertEquals(bookings.get(1), bookingInPastWithStatusRejected);
    }

    @Test
    void catchNotFoundExceptionWhenFindAllBookingsByParameterAndNotExistBooker() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.findAllBookingsByParameterAndBooker(99, "ALL", 0, 20);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchInternalServerExceptionWhenFindAllBookingsByIncorrectParameterAndBooker() {

        Exception exception = assertThrows(InternalServerException.class, () -> {
            bookingService.findAllBookingsByParameterAndBooker(user2.getId(), "KUKU", 0, 20);
        });
        assertTrue("Unknown state: KUKU".contains(exception.getMessage()));
    }

    @Test
    void findAllBookingsByParameterIsRejectedAndItemOwner() {
        List<Booking> bookings = bookingService.findAllBookingsByParameterAndItemOwner(user1.getId(), "REJECTED", 0, 20);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0), bookingInPastWithStatusRejected);
    }

    @Test
    void findAllBookingsByParameterIsAllAndItemOwner() {
        List<Booking> bookings = bookingService.findAllBookingsByParameterAndItemOwner(user1.getId(), "ALL", 0, 20);

        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0), bookingAtMomentWithStatusWaiting);
        assertEquals(bookings.get(1), bookingInPastWithStatusRejected);
    }

    @Test
    void catchNotFoundExceptionWhenFindAllBookingsByParameterAndNotExistItemOwner() {

        Exception exception = assertThrows(NotFoundException.class, () -> {
            bookingService.findAllBookingsByParameterAndItemOwner(99, "ALL", 0, 20);
        });
        assertTrue(UserRepository.class.getName().contains(exception.getMessage()));
    }

    @Test
    void catchInternalServerExceptionWhenFindAllBookingsByIncorrectParameterAndItemOwner() {

        Exception exception = assertThrows(InternalServerException.class, () -> {
            bookingService.findAllBookingsByParameterAndItemOwner(user2.getId(), "KUKU", 0, 20);
        });
        assertTrue("Unknown state: KUKU".contains(exception.getMessage()));
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, long itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);

        return bookingDto;
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
