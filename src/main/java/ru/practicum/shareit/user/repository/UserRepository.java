package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {

    User addUser(UserDto userDto);

    User updateUser(long id, UserDto userDto);

    List<User> findAllUser();

    User findUserById(long id);

    void deleteUserById(long id);

    boolean isNonExistEmail(String email);

    boolean contains(long id);
}
