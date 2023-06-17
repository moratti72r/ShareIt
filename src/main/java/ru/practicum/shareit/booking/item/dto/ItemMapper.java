package ru.practicum.shareit.booking.item.dto;

import ru.practicum.shareit.booking.item.model.Item;

public class ItemMapper {
    public static Item fromItemDto(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return item;
    }
}
