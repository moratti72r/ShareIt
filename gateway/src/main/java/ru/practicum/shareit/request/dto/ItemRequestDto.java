package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.booking.item.dto.ItemDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestDto {

    private long id;

    @NotNull
    @Size(max = 1000)
    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;
}
