package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {

    UserDto addUser(User user);

    UserDto updateUser(long id, User user);

    List<UserDto> findAllUser();

    User findUserById(long id);

    void deleteUserById(long id);

    boolean isNonExistEmail(String email);

    boolean contains(long id);
}
