package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IncorrectArgumentException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void addEntity() {
        bookingDto = makeBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2), 1);
        booking = makeBooking(1, bookingDto, 1);
    }

    @Test
    void createBooking() throws Exception {

        when(bookingService.addBooking(anyLong(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking)));
    }

    @Test
    void catchIncorrectArgumentExceptionWhenCreateBookingWithIncorrectEndAndStart() throws Exception {

        when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new IncorrectArgumentException(BookingRepository.class));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(IncorrectArgumentException.class));
    }

    @Test
    void catchNotFoundExceptionWhenCreateBookingWithUserIsNotExist() throws Exception {

        when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new NotFoundException(UserRepository.class));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void catchNotFoundExceptionWhenCreateBookingWithItemIsNotExist() throws Exception {

        when(bookingService.addBooking(anyLong(), any()))
                .thenThrow(new NotFoundException(ItemRepository.class));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void updateStatus() throws Exception {
        booking.setStatus(StatusType.APPROVED);
        when(bookingService.changeBookingStatus(1, 1, true))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking)));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateStatusWithUserIsNotExist() throws Exception {

        when(bookingService.changeBookingStatus(1, 1, true))
                .thenThrow(new NotFoundException(UserRepository.class));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateStatusWithBookingIsNotExist() throws Exception {

        when(bookingService.changeBookingStatus(1, 1, true))
                .thenThrow(new NotFoundException(BookingRepository.class));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void catchNotFoundExceptionWhenUpdateStatusWithUserIsNotOwner() throws Exception {

        when(bookingService.changeBookingStatus(1, 1, true))
                .thenThrow(new NotFoundException(BookingService.class));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotFoundException.class));
    }

    @Test
    void catchIncorrectArgumentExceptionWhenUpdateStatusWithStatusIsIncorrect() throws Exception {

        when(bookingService.changeBookingStatus(1, 1, true))
                .thenThrow(new IncorrectArgumentException(BookingRepository.class));

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(IncorrectArgumentException.class));
    }

    @Test
    void getById() throws Exception {
        when(bookingService.findBookingById(1, 1))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(booking)));
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, long itemId) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItemId(itemId);

        return bookingDto;
    }

    private Booking makeBooking(long id, BookingDto bookingDto, long userId) {
        Booking booking = new Booking();
        Item item = new Item();
        User user = new User();
        booking.setId(id);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        item.setId(bookingDto.getItemId());
        booking.setItem(item);
        user.setId(userId);
        booking.setBooker(user);
        booking.setStatus(StatusType.WAITING);

        return booking;
    }
}
