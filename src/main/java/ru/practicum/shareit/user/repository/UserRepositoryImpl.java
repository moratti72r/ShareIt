package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private long idGenerator = 0;

    @Override
    public UserDto addUser(User user) {
        idGenerator++;
        user.setId(idGenerator);
        users.put(idGenerator, user);

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, User user) {
        users.put(id, user);
        return UserMapper.toUserDto(users.get(id));
    }

    @Override
    public List<UserDto> findAllUser() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public User findUserById(long id) {
        return users.get(id);
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }

    @Override
    public boolean isNonExistEmail(String email) {
        return users.values().stream()
                .noneMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean contains(long id) {
        return users.containsKey(id);
    }
}
