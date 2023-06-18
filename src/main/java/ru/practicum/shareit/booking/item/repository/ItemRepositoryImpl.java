package ru.practicum.shareit.booking.item.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.dto.ItemMapper;
import ru.practicum.shareit.booking.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private long idGenerator = 0;

    @Override
    public ItemDto addItem(Item item) {

        idGenerator++;
        item.setId(idGenerator);
        items.put(item.getId(), item);

        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long idItem, Item item) {
        items.put(idItem, item);
        return ItemMapper.toItemDto(items.get(idItem));
    }

    @Override
    public Item findItemById(long id) {
        return items.get(id);
    }

    @Override
    public List<ItemDto> findAllItemByUser(long idUser) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == idUser)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return items.values().stream()
                    .filter(item -> (StringUtils.containsIgnoreCase(item.getName(), text)
                            || StringUtils.containsIgnoreCase(item.getDescription(), text))
                            && item.getAvailable())
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean contains(long id) {
        return items.containsKey(id);
    }

    @Override
    public boolean isExistUser(long idItem, long idUser) {
        return items.get(idItem).getOwner().getId() == idUser;
    }
}
