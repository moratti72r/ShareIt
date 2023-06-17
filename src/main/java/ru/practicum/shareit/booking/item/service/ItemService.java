package ru.practicum.shareit.booking.item.service;

import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(long idUser, ItemDto itemDto);

    Item updateItem(long idUser, long idItem, ItemDto itemDto);

    Item findItemById(long idUser, long idItem);

    List<Item> findAllItemByUser(long idUser);

    List<Item> searchItem(long idUser, String text);
}
