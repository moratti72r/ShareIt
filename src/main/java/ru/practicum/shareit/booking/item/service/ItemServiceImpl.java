package ru.practicum.shareit.booking.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.dto.ItemMapper;
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
    public ItemDto addItem(long idUser, ItemDto itemDto) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        Item item = ItemMapper.fromItemDto(new Item(), itemDto);
        item.setOwner(userRepository.findUserById(idUser));

        ItemDto resultItemDto = itemRepository.addItem(item);
        log.info("Продукт c id={} от Пользователя с id={} добавлен", resultItemDto.getId(), idUser);
        return resultItemDto;
    }

    @Override
    public ItemDto updateItem(long idUser, long idItem, ItemDto itemDto) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.contains(idItem)) {
            throw new NotFoundException(ItemRepository.class);
        }
        if (!itemRepository.isExistUser(idItem, idUser)) {
            throw new NotFoundException(UserRepository.class);
        }

        Item item = itemRepository.findItemById(idItem);
        ItemMapper.fromItemDto(item, itemDto);

        ItemDto resultItemDto = itemRepository.updateItem(idItem, item);
        log.info("Продукт c id={} успешно изменен Пользователем с id={}", idItem, idUser);

        return resultItemDto;
    }

    @Override
    public ItemDto findItemById(long idUser, long idItem) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (!itemRepository.contains(idItem)) {
            throw new NotFoundException(ItemRepository.class);
        }

        ItemDto resultItemDto = ItemMapper.toItemDto(itemRepository.findItemById(idItem));
        log.info("Продукт c id={} успешно изменен Пользователем с id={}", idItem, idUser);

        return resultItemDto;
    }

    @Override
    public List<ItemDto> findAllItemByUser(long idUser) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        List<ItemDto> result = itemRepository.findAllItemByUser(idUser);
        log.info("Список продуктов от Пользователя c id={} получен", idUser);

        return result;
    }

    @Override
    public List<ItemDto> searchItem(long idUser, String text) {
        if (!userRepository.contains(idUser)) {
            throw new NotFoundException(UserRepository.class);
        }
        List<ItemDto> result = itemRepository.searchItem(text);
        log.info("Получен список доступных продуктов в поиске по тексту \"{}\" " +
                "для Пользователя с id={}", text, idUser);

        return result;
    }
}
