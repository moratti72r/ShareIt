package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Controller
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST запрос /requests от пользователя {}", userId);
        return ResponseEntity.ok(itemRequestService.addItemRequest(userId, itemRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> findAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET запрос /requests");
        return ResponseEntity.ok(itemRequestService.findAllRequestsByRequestor(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(name = "from", required = false) Integer from,
                                                                @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получен GET запрос /requests/all?from={}&size={}", from, size);
        return ResponseEntity.ok(itemRequestService.findAllRequests(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long requestId) {
        log.info("Получен GET запрос /requests/{}", requestId);
        return ResponseEntity.ok(itemRequestService.findRequestById(userId, requestId));
    }
}
