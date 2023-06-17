package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    private long idGenerator = 0;

    @Override
    public User addUser(UserDto userDto) {
        idGenerator++;
        User user = UserMapper.fromUserDto(new User(), userDto);
        user.setId(idGenerator);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(long id, UserDto userDto) {
        UserMapper.fromUserDto(users.get(id), userDto);
        return users.get(id);
    }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(users.values());
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
