package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ValidationMarker;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Slf4j
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Получен POST запрос /users");
        return userService.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto upDate(@PathVariable long id, @RequestBody UserDto userDto) {
        log.info("Получен PATCH запрос /users/{}", id);
        return userService.updateUser(id, userDto);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Получен GET запрос /users");
        return userService.findAllUser();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.info("Получен GET запрос /users/{}", id);
        return userService.findUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        log.info("Получен DELETE запрос /users/{}", id);
        userService.deleteUserById(id);
    }
}
