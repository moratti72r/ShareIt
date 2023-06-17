package ru.practicum.shareit.booking.item.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.dto.ItemMapper;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final UserRepository userRepository;

    private final Map<Long, Item> items = new HashMap<>();

    private long idGenerator = 0;

    @Override
    public Item addItem(long idUser, ItemDto itemDto) {

        idGenerator++;
        Item item = ItemMapper.fromItemDto(new Item(), itemDto);
        item.setId(idGenerator);
        item.setOwner(userRepository.findUserById(idUser));
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItem(long idUser, long idItem, ItemDto itemDto) {
        ItemMapper.fromItemDto(items.get(idItem), itemDto);
        return items.get(idItem);
    }

    @Override
    public Item findItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAllItemByUser(long idUser) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == idUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return items.values().stream()
                    .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text)
                            || StringUtils.containsIgnoreCase(item.getDescription(), text))
                            && item.getAvailable())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean contains(long id) {
        return items.containsKey(id);
    }
}
