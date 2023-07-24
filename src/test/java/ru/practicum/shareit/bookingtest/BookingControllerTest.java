package ru.practicum.shareit.bookingtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import javax.xml.bind.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void catchValidationExceptionWhenCreateBookingWithNameNotValidStart() throws Exception {
        BookingDto bookingDto = makeBookingDto(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(2), 1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
    }

    @Test
    void catchValidationExceptionWhenCreateBookingWithNameNotValidEnd() throws Exception {
        BookingDto bookingDto = makeBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().minusHours(2), 1);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
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

    @Test
    void findAllBookingsByParameterAndBooker() throws Exception {
        when(bookingService.findAllBookingsByParameterAndBooker(1, "ALL", 0, 20))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void catchValidationExceptionWhenFindAllBookingsByBookerWithParamNotValid() throws Exception {

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
    }

    @Test
    void findAllBookingsByParameterAndItemOwner() throws Exception {
        when(bookingService.findAllBookingsByParameterAndItemOwner(1, "ALL", 0, 20))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking))));
    }

    @Test
    void catchValidationExceptionWhenFindAllBookingsByItemOwnerWithParamNotValid() throws Exception {

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(ValidationException.class));
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
