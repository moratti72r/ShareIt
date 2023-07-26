package ru.practicum.shareit.booking.item.dto;

import lombok.Data;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    private long id;

    @NotEmpty(groups = ValidationMarker.OnCreate.class)
    @Size(max = 255)
    private String name;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    @Size(max = 1000)
    private String description;

    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean available;

    private Long requestId;

    private BookingDto lastBooking;

    private BookingDto nextBooking;

    private List<CommentDto> comments;
}
