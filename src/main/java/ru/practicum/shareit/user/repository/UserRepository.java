package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {

    User addUser(User user);

    User updateUser(UserDto userDto);

    List<User> findAllUser();

    User findUserById(long id);

    void deleteUserById(long id);
}
