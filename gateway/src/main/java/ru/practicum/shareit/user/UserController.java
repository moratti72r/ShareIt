package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping(path = "/users")
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        log.info("Получен GET запрос /users/{}", id);
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Получен GET запрос /users");
        return userClient.getAll();
    }

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto user) {
        log.info("Получен POST запрос /users");
        return userClient.create(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long id, @RequestBody UserDto user) {
        log.info("Получен PATCH запрос /users/{}", id);
        return userClient.update(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        log.info("Получен DELETE запрос /users/{}", id);
        return userClient.delete(id);
    }
}
