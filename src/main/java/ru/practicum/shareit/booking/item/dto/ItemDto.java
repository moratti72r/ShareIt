package ru.practicum.shareit.booking.item.dto;

import lombok.Data;
import ru.practicum.shareit.ValidationMarker;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    @NotNull(groups = ValidationMarker.OnCreate.class)
    @NotEmpty(groups = ValidationMarker.OnCreate.class)
    private String name;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private String description;
    @NotNull(groups = ValidationMarker.OnCreate.class)
    private Boolean available;
}
