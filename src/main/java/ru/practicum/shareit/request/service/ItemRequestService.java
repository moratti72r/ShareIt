package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addItemRequest(long idUser, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllRequestsByRequestor(long userId, int from, int size);

    ItemRequestDto findRequestById(long userId, long requestId);

    List<ItemRequestDto> findAllRequests(long userId, int from, int size);
}
