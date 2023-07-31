package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Controller
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        log.info("Получен POST запрос /users");
        return ResponseEntity.ok(userService.addUser(userDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> upDate(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос /users/{}", id);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        log.info("Получен GET запрос /users");
        return ResponseEntity.ok(userService.findAllUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable long id) {
        log.info("Получен GET запрос /users/{}", id);
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        log.info("Получен DELETE запрос /users/{}", id);
        userService.deleteUserById(id);
        ResponseEntity.ok(id);
    }
}
