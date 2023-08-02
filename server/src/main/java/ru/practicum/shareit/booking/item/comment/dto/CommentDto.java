package ru.practicum.shareit.booking.item.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private long id;

    private String text;

    private String authorName;

    private LocalDateTime created;
}
