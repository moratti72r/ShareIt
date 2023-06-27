package ru.practicum.shareit.booking.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private long id;

    @NotNull
    @NotEmpty
    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;
}
