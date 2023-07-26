package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.dto.ItemMapper;
import ru.practicum.shareit.booking.item.model.Item;
import ru.practicum.shareit.booking.item.repository.ItemRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(long idUser, ItemRequestDto itemRequestDto) {
        Optional<User> userOptional = userRepository.findById(idUser);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            itemRequestDto.setCreated(LocalDateTime.now());
            ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(user, itemRequestDto);

            ItemRequest result = itemRequestRepository.save(itemRequest);
            log.info("Запрос c id={} от Пользователя с id={} добавлен", result.getId(), idUser);

            return ItemRequestMapper.toItemRequestDto(result);
        } else throw new NotFoundException(UserRepository.class);
    }

    @Override
    public List<ItemRequestDto> findAllRequestsByRequestor(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }
        List<ItemRequestDto> requestDtos = new ArrayList<>();
        if (itemRequestRepository.existsByRequestorId(userId)) {
            requestDtos = itemRequestRepository
                    .findAllByRequestorId(userId, PageRequest.of(from / size, size, Sort.by("created").descending()))
                    .stream()
                    .map(ItemRequestMapper::toItemRequestDto)
                    .collect(Collectors.toList());
            for (ItemRequestDto ird : requestDtos) {
                List<Item> items = itemRepository.findAllByRequestId(ird.getId());
                List<ItemDto> itemDtos = new ArrayList<>();
                if (!items.isEmpty()) {
                    itemDtos = items.stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                }
                ird.setItems(itemDtos);
            }
            log.info("Список собственных запросов от Пользователя с id={} получен", userId);
            return requestDtos;
        }
        log.info("Список собственных запросов от Пользователя с id={} получен", userId);
        return requestDtos;
    }

    @Override
    public ItemRequestDto findRequestById(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }
        if (itemRequestRepository.existsById(requestId)) {
            ItemRequestDto ird = ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).get());
            List<Item> items = itemRepository.findAllByRequestId(ird.getId());
            List<ItemDto> itemDtos = new ArrayList<>();
            if (!items.isEmpty()) {
                itemDtos = items.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
            }
            ird.setItems(itemDtos);

            log.info("Запрос c id={} от Пользователя с id={} получен", requestId, userId);
            return ird;
        } else throw new NotFoundException(ItemRequestRepository.class);
    }

    @Override
    public List<ItemRequestDto> findAllRequests(long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(UserRepository.class);
        }
        List<ItemRequestDto> requestDtos = itemRequestRepository
                .findAllByRequestorIdIsNot(userId, PageRequest.of(from / size, size, Sort.by("created").descending()))
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto ird : requestDtos) {
            List<Item> items = itemRepository.findAllByRequestId(ird.getId());
            List<ItemDto> itemDtos = new ArrayList<>();
            if (!items.isEmpty()) {
                itemDtos = items.stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList());
            }
            ird.setItems(itemDtos);
        }

        log.info("Список запросов от Пользователя с id={} получен", userId);
        return requestDtos;
    }
}
