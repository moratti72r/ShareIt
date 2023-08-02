package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.ItemBaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemBaseClient itemRequestClient;

    @GetMapping("/all")
    @Validated
    public ResponseEntity<Object> getSort(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PositiveOrZero
                                          @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive
                                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET запрос /requests/all?from={}&size={}", from, size);
        return itemRequestClient.getSort(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("requestId") Long requestId) {
        log.info("Получен GET запрос /requests/{}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping
    @Validated
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET запрос /requests");
        return itemRequestClient.getAll(userId);
    }

    @PostMapping
    @Validated
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid
    @RequestBody ItemRequestDto itemRequest) {
        log.info("Получен POST запрос /requests от пользователя {}", userId);
        return itemRequestClient.create(userId, itemRequest);
    }
}
