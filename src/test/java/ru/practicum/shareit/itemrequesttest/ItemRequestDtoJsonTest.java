package ru.practicum.shareit.itemrequesttest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws IOException {
        ItemRequestDto requestDto = new ItemRequestDto();

        requestDto.setId(1);
        requestDto.setDescription("Описание");
        requestDto.setCreated(LocalDateTime.of(2020, 11, 11, 11, 11, 11));

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2020-11-11T11:11:11");
    }
}
