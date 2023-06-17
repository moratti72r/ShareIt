package ru.practicum.shareit.booking.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public Item addItem(long idUser, ItemDto itemDto) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        Item resultItem = itemRepository.addItem(idUser, itemDto);
        log.info("Продукт c id={} от Пользователя с id={} добавлен", resultItem.getId(), idUser);
        return resultItem;
    }

    @Override
    public Item updateItem(long idUser, long idItem, ItemDto itemDto) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.contains(idItem)) {
            throw new NotFoundException(ItemRepository.class);
        }
        if (!itemRepository.findItemById(idItem).getOwner().equals(userRepository.findUserById(idUser))) {
            throw new NotFoundException(UserRepository.class);
        }
        log.info("Продукт c id={} успешно изменен Пользователем с id={}", idItem, idUser);
        return itemRepository.updateItem(idUser, idItem, itemDto);
    }

    @Override
    public Item findItemById(long idUser, long idItem) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.contains(idItem)) {
            throw new NotFoundException(ItemRepository.class);
        }
        log.info("Продукт c id={} успешно изменен Пользователем с id={}", idItem, idUser);
        return itemRepository.findItemById(idItem);
    }

    @Override
    public List<Item> findAllItemByUser(long idUser) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        log.info("Список продуктов от Пользователя c id={} получен", idUser);
        return itemRepository.findAllItemByUser(idUser);
    }

    @Override
    public List<Item> searchItem(long idUser, String text) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        log.info("Получен список доступных продуктов в поиске по тексту \"{}\" " +
                "для Пользователя с id={}", text, idUser);
        return itemRepository.searchItem(text);
    }
}
