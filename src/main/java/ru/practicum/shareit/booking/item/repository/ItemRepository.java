package ru.practicum.shareit.booking.item.repository;

import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item addItem(long idUser, ItemDto itemDto);

    Item updateItem(long idUser, long idItem, ItemDto itemDto);

    Item findItemById(long id);

    List<Item> findAllItemByUser(long idUser);

    List<Item> searchItem(String text);

    boolean contains(long id);
}
