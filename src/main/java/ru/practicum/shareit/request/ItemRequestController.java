package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @Validated
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен POST запрос /requests");
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    @Validated
    public List<ItemRequestDto> findAllRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "20") @Min(0) int size) {
        log.info("Получен GET запрос /requests?from={}&size={}", from, size);
        return itemRequestService.findAllRequestsByRequestor(userId, from, size);
    }

    @GetMapping("/all")
    @Validated
    public List<ItemRequestDto> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "0") @Min(0) int from,
                                                @RequestParam(defaultValue = "20") @Min(0) int size) {
        log.info("Получен GET запрос /requests/all?from={}&size={}", from, size);
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable long requestId) {
        log.info("Получен GET запрос /requests/{}", requestId);
        return itemRequestService.findRequestById(userId, requestId);
    }
}
