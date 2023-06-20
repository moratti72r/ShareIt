package ru.practicum.shareit.booking.item.repository;

import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.model.Item;

import java.util.List;

public interface ItemRepository {

    ItemDto addItem(Item item);

    ItemDto updateItem(long idItem, Item item);

    Item findItemById(long id);

    List<ItemDto> findAllItemByUser(long idUser);

    List<ItemDto> searchItem(String text);

    boolean contains(long id);

    boolean isExistUser(long idItem, long idUser);
}
