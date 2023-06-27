package ru.practicum.shareit.booking.item.dto;

import lombok.Data;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    private long id;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    @NotEmpty(groups = ValidationMarker.OnCreate.class)
    private String name;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String description;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean available;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
