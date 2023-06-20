package ru.practicum.shareit.booking.item.model;

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
    private User owner;
    private ItemRequest request;

}
