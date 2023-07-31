package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto updateUser(long id, UserDto userDto);

    List<UserDto> findAllUser();

    UserDto findUserById(long id);

    void deleteUserById(long id);
}
