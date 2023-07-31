package ru.practicum.shareit.booking.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.booking.item.client.ItemClient;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable("id") Long itemId) {
        log.info("Получен GET запрос /items/{}", itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    @Validated
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero
                                         @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive
                                         @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET запрос /items");
        return itemClient.getAll(userId, from, size);
    }

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody @Valid ItemDto item) {
        log.info("Получен POST запрос /items от пользователя {}", userId);
        return itemClient.create(userId, item);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long itemId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemDto item) {
        log.info("Получен PATCH запрос /items/{}", itemId);
        return itemClient.update(userId, itemId, item);
    }

    @GetMapping("/search")
    @Validated
    public ResponseEntity<Object> getSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam String text,
                                            @PositiveOrZero
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @Positive
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Получен GET запрос /items/search?text={}", text);
        return itemClient.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody CommentDto comment,
                                                @PathVariable("itemId") Long itemId) {
        log.info("Получен POST запрос /items/{itemId}/comment", itemId);
        return itemClient.createComment(userId, itemId, comment);
    }
}
