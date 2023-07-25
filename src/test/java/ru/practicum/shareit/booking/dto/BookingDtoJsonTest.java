package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws IOException {
        BookingDto bookingDto = new BookingDto();

        bookingDto.setId(1);
        bookingDto.setStart(LocalDateTime.of(2020, 11, 11, 11, 11, 11));
        bookingDto.setEnd(LocalDateTime.of(2020, 11, 12, 11, 11, 11));
        bookingDto.setItemId(1);
        bookingDto.setBookerId(1);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2020-11-11T11:11:11");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2020-11-12T11:11:11");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);

    }
}
