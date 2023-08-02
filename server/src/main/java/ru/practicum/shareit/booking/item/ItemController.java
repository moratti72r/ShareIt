package ru.practicum.shareit.booking.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") long idUser, @RequestBody ItemDto itemDto) {
        log.info("Получен POST запрос /items от пользователя {}", idUser);
        return ResponseEntity.ok(itemService.addItem(idUser, itemDto));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> createComment(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                    @PathVariable long itemId,
                                                    @RequestBody CommentDto commentDto) {
        log.info("Получен POST запрос /items/{itemId}/comment", itemId);
        return ResponseEntity.ok(itemService.addComment(idUser, itemId, commentDto));
    }

    @PatchMapping("/{idItem}")
    public ResponseEntity<ItemDto> upDate(@RequestHeader("X-Sharer-User-Id") long idUser,
                                          @PathVariable long idItem,
                                          @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH запрос /items/{}", idItem);
        return ResponseEntity.ok(itemService.updateItem(idUser, idItem, itemDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@RequestHeader("X-Sharer-User-Id") long idUser,
                                            @PathVariable long id) {
        log.info("Получен GET запрос /items/{}", id);
        return ResponseEntity.ok(itemService.findItemById(idUser, id));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                            @RequestParam(name = "from", required = false) Integer from,
                                                            @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получен GET запрос /items");
        return ResponseEntity.ok(itemService.findAllItemByUser(idUser, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                    @RequestParam(name = "text", required = false) String text,
                                                    @RequestParam(name = "from", required = false) Integer from,
                                                    @RequestParam(name = "size", required = false) Integer size) {
        log.info("Получен GET запрос /items/search?text={}", text);
        return ResponseEntity.ok(itemService.searchItem(idUser, text, from, size));
    }

}
