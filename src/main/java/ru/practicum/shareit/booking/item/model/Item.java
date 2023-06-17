package ru.practicum.shareit.booking.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    @JsonIgnore
    private User owner;
    @JsonIgnore
    private ItemRequest request;

}
