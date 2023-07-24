package ru.practicum.shareit.booking.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.booking.item.comment.dto.CommentDto;
import ru.practicum.shareit.booking.item.dto.ItemDto;
import ru.practicum.shareit.booking.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long idUser, @RequestBody @Valid ItemDto itemDto) {
        log.info("Получен POST запрос /items");
        return itemService.addItem(idUser, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @Validated({ValidationMarker.OnCreate.class})
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long idUser,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        log.info("Получен POST запрос /items");
        return itemService.addComment(idUser, itemId, commentDto);
    }

    @PatchMapping("/{idItem}")
    public ItemDto upDate(@RequestHeader("X-Sharer-User-Id") long idUser,
                          @PathVariable long idItem,
                          @RequestBody ItemDto itemDto) {
        log.info("Получен PATCH запрос /items/{}", idItem);
        return itemService.updateItem(idUser, idItem, itemDto);
    }

    @GetMapping("/{id}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long idUser,
                            @PathVariable long id) {
        log.info("Получен GET запрос /items/{}", id);
        return itemService.findItemById(idUser, id);
    }

    @GetMapping
    public List<ItemDto> findAllItemsByUser(@RequestHeader("X-Sharer-User-Id") long idUser,
                                            @RequestParam(defaultValue = "0") @Min(0) int from,
                                            @RequestParam(defaultValue = "20") @Min(0) int size) {
        log.info("Получен GET запрос /items");
        return itemService.findAllItemByUser(idUser, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                    @RequestParam String text,
                                    @RequestParam(defaultValue = "0") @Min(0) int from,
                                    @RequestParam(defaultValue = "20") @Min(0) int size) {
        log.info("Получен GET запрос /items/search?text={}", text);
        return itemService.searchItem(idUser, text, from, size);
    }

}
