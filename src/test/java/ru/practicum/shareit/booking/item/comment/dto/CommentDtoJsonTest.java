package ru.practicum.shareit.booking.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testCommentDto() throws IOException {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1);
        commentDto.setText("Текст");
        commentDto.setAuthorName("Комментатор");
        commentDto.setCreated(LocalDateTime.of(2020, 12, 12, 12, 12, 12));

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Текст");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Комментатор");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2020-12-12T12:12:12");
    }
}
