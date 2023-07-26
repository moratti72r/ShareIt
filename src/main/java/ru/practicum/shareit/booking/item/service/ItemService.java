package ru.practicum.shareit.booking.item.service;

import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(long idUser, ItemDto itemDto);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

    ItemDto updateItem(long idUser, long idItem, ItemDto itemDto);

    ItemDto findItemById(long idUser, long idItem);

    List<ItemDto> findAllItemByUser(long idUser, int from, int size);

    List<ItemDto> searchItem(long idUser, String text, int from, int size);
}
