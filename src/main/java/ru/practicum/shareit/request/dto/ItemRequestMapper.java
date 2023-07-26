package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        return itemRequestDto;
    }

    public static ItemRequest fromItemRequestDto(User user, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        if (itemRequestDto.getDescription() != null) {
            itemRequest.setDescription(itemRequestDto.getDescription());
        }
        if (itemRequestDto.getCreated() != null) {
            itemRequest.setCreated(itemRequestDto.getCreated());
        }
        itemRequest.setRequestor(user);
        return itemRequest;
    }
}
