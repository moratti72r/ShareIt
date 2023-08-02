package ru.practicum.shareit.booking.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
